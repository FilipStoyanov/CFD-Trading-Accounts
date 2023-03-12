import React, { useState, useEffect, useRef, useCallback } from "react";
import PositionsTable from "../../components/positions-table/PositionsTable";
import LinearProgressWithLabel from "../../components/progress-bar/LinearProgressWithLabel";
import AlarmAddOutlinedIcon from "@mui/icons-material/AlarmAddOutlined";
import { useSelector, useDispatch } from "react-redux";
import { calculateStatus, formatTimestamp } from "../../utils";
import { styles } from "./StockMarketStyles";
import Header from "../../components/header/Header";
import logo from "../../assets/images/logo.svg";
import {
  Grid,
  Typography,
  Box,
  IconButton,
  Modal,
  Button,
  ToggleButtonGroup,
  ToggleButton,
} from "@mui/material";
import {
  fetchAccountBalance,
  getInstrumentsWithPagination,
  getInstrumentsWithOffset,
  getGraphicDataForInstrument,
  openMarketPosition,
} from "../../requests";
import InstrumentCard from "../../components/instrument-card/InstrumentCard";
import { styled } from "@mui/material/styles";
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import Loading from "../../components/loading/Loading";
import {
  AreaChart,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  Area,
} from "recharts";
import T212Modal from "../../components/modal/Modal";
import { update } from "../../store/slices/instrumentSlice";
import CustomTooltip from "../../components/tooltip/CustomTooltip";

const MARGIN_CALL_PERCENTAGE = 45;
const StyledIconButton = styled(IconButton)(({ theme }) => ({
  "&:hover": {
    backgroundColor: "transparent",
  },
}));
const StockMarket = React.memo(({}) => {
  const user = useSelector((state) => state.user.user);
  const [disableSell, setDisableSell] = useState(false);
  const [disableBuy, setDisableBuy] = useState(false);
  const [openPositions, setOpenPositions] = useState([]);
  const [freeCash, setFreeCash] = useState(0);
  const [lockedCash, setLockedCash] = useState(0);
  const [liveResult, setLiveResult] = useState(0);
  const [status, setStatus] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [openFirstTime, setOpenFirstTime] = useState(false);
  const [loading, setLoading] = useState(true);
  const [chosenInstrument, setChosenInstrument] = useState(1);
  const [chartSellData, setSellChartData] = useState([]);
  const [chartBuyData, setBuyChartData] = useState([]);
  const [waitNotification, setWaitNotification] = useState(false);
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [listOfInstruments, setListOfInstruments] = useState([]);
  const [page, setPage] = useState(0);
  const [showLoadButton, setShowLoadButton] = useState(true);
  const [graphicForPosition, setGraphicForPosition] = useState(false);
  const INSTRUMENT_CARD_HEIGHT = 250;
  const VIEWED_INSTRUMENTS = 4;
  const PAGE_SIZE = 10;
  const opened = useRef(0);
  const [startTime, setStartTime] = useState();
  const DEV_URL = "http://localhost:8080/websocket";
  const HAPROXY_URL = "http://localhost:8079/websocket";
  const dispatch = useDispatch();
  const sellChartRef = useRef();
  const buyChartRef = useRef();
  const [graphic, setGraphic] = useState("sell");

  const handleOpen = () => {
    setShowModal(true);
  };

  const handleButtonClick = (event, newValue) => {
    setGraphic(newValue);
  };

  const handleClose = () => {
    setShowModal(false);
    setOpenFirstTime(false);
    setWaitNotification(false);
  };

  const handleOnClick = (event) => {
    setDisableBuy(false);
    setDisableSell(false);
    localStorage.removeItem("chosen");
    const instr = JSON.parse(localStorage.getItem("instruments")).find(
      (e) => e.id == event.currentTarget.getAttribute("data-key")
    );
    localStorage.setItem("graphic", instr.ticker);
    setGraphicForPosition(false);
    initSellArr();
    initBuyArr();
    setChosenInstrument(event.currentTarget.getAttribute("data-key"));
  };

  const checkForMarginCall = () => {
    const accountStatus = calculateStatus(
      JSON.parse(localStorage.getItem("locked")),
      JSON.parse(localStorage.getItem("cash"))
    );
    if (!sessionStorage.getItem("marginCall") && accountStatus < 45) {
      sessionStorage.setItem("marginCall", true);
      setOpenFirstTime(true);
      setWaitNotification(true);
      setTimeout(() => {
        setWaitNotification(false);
      }, 3000);
    }
    if (sessionStorage.getItem("marginCall") && accountStatus > 60) {
      sessionStorage.clear();
      setOpenFirstTime(false);
      setWaitNotification(false);
    }
  };

  const handleOnRemove = (row) => {
    if (
      localStorage.getItem("chosen") &&
      row.ticker + "_" + row.type == localStorage.getItem("chosen")
    ) {
      localStorage.removeItem("chosen");
    }
    setGraphicForPosition(false);
    setChosenInstrument(1);
    initSellArr();
    setDisableBuy(false);
    setDisableSell(false);
    initBuyArr();
    setGraphic("sell");
  };

  const getAccountBalance = async () => {
    const res = await fetchAccountBalance(user.id);
    if (res.data.status === 200) {
      if (res.data.result && res.data.result.balance) {
        localStorage.setItem("cash", res.data.result.balance);
      }
    }
  };

  const addDataToChart = (currentChartInstrument) => {
    const current = new Date().getTime() - 200 * 1000;
    const result = current - startTime;
    const ind = Math.floor(result / 1000);
    if (chartSellData && chartSellData[40 + ind]) {
      const milliseconds = chartSellData[40 + ind].time;
      const point = {
        time: milliseconds,
        sell: currentChartInstrument.sell,
      };
      setSellChartData((prevData) => [
        ...prevData.slice(0, 40 + ind),
        point,
        ...prevData.slice(41 + ind),
      ]);
    }
    if (chartBuyData && chartBuyData[40 + ind]) {
      const milliseconds = chartBuyData[40 + ind].time;
      const point = {
        time: milliseconds,
        buy: currentChartInstrument.buy,
      };
      setBuyChartData((prevData) => [
        ...prevData.slice(0, 40 + ind),
        point,
        ...prevData.slice(41 + ind),
      ]);
    }
  };

  const openConnection = useCallback(() => {
    const sock = new SockJS(HAPROXY_URL, {
      withCredentials: true,
      transports: ["websocket"],
      headers: {
        "Access-Control-Allow-Origin": "*",
      },
    });

    const client = Stomp.over(sock);
    client.heartbeat.incoming = 10000;
    client.heartbeat.outgoing = 10000;
    client.connect({}, () => {
      setTimeout(() => {
        setLoading(false);
      }, 4000);
      setShowErrorModal(false);
      client.subscribe(`/cfd/quotes/${user.id}`, (message) => {
        const jsonObject = JSON.parse(message.body);
        const newArr = [...openPositions];
        const currentChartInstrument = Object.entries(
          jsonObject.openPositions
        ).find(([key, value]) => key == localStorage.getItem("chosen"));
        Object.values(jsonObject.openPositions).map((item) => {
          const indexOfElement = openPositions.findIndex(
            (element) => element.ticker == item.ticker
          );
          if (indexOfElement == -1) {
            newArr.push(item);
          } else {
            newArr[indexOfElement] = item;
          }
          setOpenPositions(newArr);
        });
        setLockedCash(jsonObject.lockedCash);
        setLiveResult(jsonObject.result);
        const cash =
          parseFloat(localStorage.getItem("cash")) + jsonObject.result;
        setFreeCash(cash - jsonObject.lockedCash);
        setStatus(
          calculateStatus(
            jsonObject.lockedCash,
            parseFloat(localStorage.getItem("cash")) + liveResult
          )
        );
      });
      client.subscribe(`/cfd/balance/${user.id}`, (message) => {
        const jsonObject = JSON.parse(message.body);
        if (jsonObject && jsonObject.balance) {
          localStorage.setItem("cash", jsonObject.balance);
        }
      });
      client.subscribe(`/cfd/errors`, (message) => {
        const jsonObject = JSON.parse(message.body);
        if (jsonObject.status === "error") {
          setShowErrorModal(true);
        }
      });
      client.onWebSocketClose = () => {
        openConnection();
      };
    });
  }, []);

  const initSellArr = () => {
    const start = new Date().getTime() - 200 * 1000;
    localStorage.setItem("startTime", start);
    setStartTime(start);
    const initialize = [];
    initialize.push({
      time: start,
      sell: 188,
    });
    for (let i = 1; i < 200; i += 5) {
      initialize.push({
        time: start + i * 1000,
        sell: Math.floor(Math.random() * (193 - 187 + 1) + 187),
      });
    }
    for (let i = 200; i < 1000; ++i) {
      initialize.push({
        time: start + i * 1000,
        sell: null,
      });
    }
    setSellChartData(initialize);
  };
  const initBuyArr = () => {
    const start = new Date().getTime() - 200 * 1000;
    localStorage.setItem("startTime", start);
    setStartTime(start);
    const initialize = [];
    initialize.push({
      time: start,
      buy: 185,
    });
    for (let i = 1; i < 200; i += 5) {
      initialize.push({
        time: start + i * 1000,
        buy: Math.floor(Math.random() * (190 - 185 + 1) + 185),
      });
    }
    for (let i = 200; i < 1000; ++i) {
      initialize.push({
        time: start + i * 1000,
        buy: null,
      });
    }
    setBuyChartData(initialize);
  };

  useEffect(() => {
    sellChartRef.current = [...chartSellData];
  }, [chartSellData]);

  useEffect(() => {
    buyChartRef.current = [...chartBuyData];
  }, [chartBuyData]);
  useEffect(() => {
    initSellArr();
    initBuyArr();
    localStorage.removeItem("input");
    localStorage.removeItem("chosen");
    localStorage.removeItem("scroll");
    localStorage.removeItem("instruments");
    localStorage.removeItem("graphic");
    const intervalId = setInterval(() => {
      checkForMarginCall();
    }, 1000);
    return () => clearInterval(intervalId);
  }, []);

  useEffect(() => {
    const intervalId = setInterval(() => {
        getDataForGraphic();
    }, 1000);
    return () => clearInterval(intervalId);
  }, [graphicForPosition, startTime]);

  const getInstruments = async () => {
    const res = await getInstrumentsWithPagination(
      page,
      PAGE_SIZE,
    );
    if (res.status === 200 && res.data && res.data.result) {
      const instruments = [...listOfInstruments, ...res.data.result];
      if (res.data.result.length === 0) {
        setShowLoadButton(false);
      }
      dispatch(update(instruments));
      if (instruments && instruments[0]) {
        localStorage.setItem("graphic", instruments[0].ticker);
        localStorage.setItem("instruments", JSON.stringify(instruments));
        setListOfInstruments([...instruments]);
      }
    }
  };

  const getInstrumentsOnTheView = async () => {
    let firstItemAtTheView = parseInt(localStorage.getItem("scroll"));
    firstItemAtTheView = firstItemAtTheView ? firstItemAtTheView : 0;
    const nRows =
      JSON.parse(localStorage.getItem("instruments")).length -
        firstItemAtTheView >=
      VIEWED_INSTRUMENTS
        ? VIEWED_INSTRUMENTS
        : JSON.parse(localStorage.getItem("instruments")).length -
          firstItemAtTheView;
    const res = await getInstrumentsWithOffset(firstItemAtTheView, nRows);
    if (res.status === 200 && res.data && res.data.result) {
      setListOfInstruments((instruments) => [
        ...instruments.slice(0, firstItemAtTheView),
        ...res.data.result,
        ...instruments.slice(firstItemAtTheView + res.data.result.length),
      ]);
    }
  };

  const handleScroll = (event) => {
    let pos = Math.floor(
      (event.target.scrollTop - 100) / INSTRUMENT_CARD_HEIGHT
    );
    pos = pos < 0 ? 0 : pos;
    localStorage.setItem("scroll", pos);
  };

  const setRow = (newRow) => {
    setGraphicForPosition(true);
    if (newRow.type === "SHORT") {
      initBuyArr();
      setDisableSell(true);
      setDisableBuy(false);
      setGraphic("buy");
    } else if (newRow.type === "LONG") {
      initSellArr();
      setGraphic("sell");
      setDisableSell(false);
      setDisableBuy(true);
    }
    localStorage.setItem("chosen", newRow.ticker + "_" + newRow.type);
    setChosenInstrument(newRow.ticker + "_" + newRow.type);
  };

  const getDataForGraphic = async () => {
    const currentInstrument = localStorage.getItem("graphic");
    let id = 1;
    if (localStorage.getItem("instruments")) {
      const instr = JSON.parse(localStorage.getItem("instruments")).find(
        (e) => e.ticker == currentInstrument
      );
      if (instr) {
        id = instr.id;
      }
    }
    const res = await getGraphicDataForInstrument(id);
    console.log(res);
    if (res.status === 200 && res.data.result) {
      addDataToChart(res.data.result);
    }
  };

  const openNewPosition = async (position) => {
    const res = await openMarketPosition(position, user.id);
    if (res.status === 200 && res.data && res.data.result) {
      const pos = res.data.result;
      const price = pos.type === "SHORT" ? pos.sellPrice : pos.buyPrice;
      const currentPrice = pos.type === "LONG" ? pos.buyPrice : pos.sellPrice;
      setOpenPositions((openPositions) => [
        ...openPositions,
        {
          ticker: pos.ticker,
          type: pos.type,
          quantity: pos.quantity,
          price: price,
          currentPrice: currentPrice,
          margin: "",
          result: "",
        },
      ]);
    }
  };

  useEffect(() => {
    dispatch(update([]));
    const intervalId = setInterval(() => {
      getInstrumentsOnTheView();
    }, 5000);
    return () => clearInterval(intervalId);
  }, []);

  useEffect(() => {
    getInstruments();
    if (chartSellData.length === 0 || chartBuyData.length === 0) {
      getDataForGraphic();
    }
  }, [page]);

  useEffect(() => {
    if (opened.current == 0) {
      getAccountBalance();
      openConnection();
    }
    opened.current = opened.current + 1;
  }, []);

  return (
    <React.Fragment>
      {loading ? (
        <Loading />
      ) : (
        <Grid container justifyContent="center" sx={styles.wrapper}>
          <Grid container item xs={12} xl={10} sx={styles.header}>
            <Header
              cash={(
                parseFloat(localStorage.getItem("cash")) +
                parseFloat(liveResult)
              ).toFixed(2)}
            />
          </Grid>
          <Grid
            container
            justifyContent="flex-start"
            mt={5}
            sx={styles.wrapper}
          >
            <Grid
              container
              item
              xs={12}
              xl={2}
              spacing={5}
              px={0}
              onScroll={handleScroll}
              sx={[
                styles.instruments,
                {
                  maxHeight:
                    openPositions.length <= 5
                      ? `calc(105vh + ${openPositions.length}*35px)`
                      : `calc(100vh + 175px)`,
                },
              ]}
            >
              <Box sx={styles.scrollView}>
              <img src={logo}  alt="Trading212 logo" />
                {listOfInstruments.length > 0 &&
                  listOfInstruments.map((current, index) => {
                    return (
                      <div id={index}>
                        <Grid
                          item
                          xs={12}
                          key={current.name}
                          data-key={current.id}
                          onClick={handleOnClick}
                          my={3}
                        >
                          <InstrumentCard
                            id={current.id}
                            name={current.name}
                            sellPrice={current.sell.toFixed(2)}
                            buyPrice={current.buy.toFixed(2)}
                            minQuantity={current.quantity}
                            marketName={current.marketName}
                            margin={(current.leverage * 100).toFixed(0)}
                            openPosition={openNewPosition}
                          />
                        </Grid>
                        {index === listOfInstruments.length - 1 &&
                        showLoadButton ? (
                          <Button
                            sx={styles.loadMore}
                            onClick={() => {
                              setPage(page + 1);
                            }}
                          >
                            Load More
                          </Button>
                        ) : null}
                      </div>
                    );
                  })}

                {listOfInstruments.length === 0 && (
                  <Typography px={1} mt={4} textAlign="center">
                    Not found results
                  </Typography>
                )}
              </Box>
            </Grid>
            <Grid item xs={12} xl={10} sx={styles.positions}>
              <ToggleButtonGroup
                value={graphic}
                exclusive
                onChange={handleButtonClick}
                aria-label="chart"
                sx={styles.group}
              >
                <ToggleButton
                  color="primary"
                  value="sell"
                  aria-label="sell"
                  sx={[styles.button, { borderBottom: "2px solid #d3d4d9" }]}
                  disabled={disableSell}
                >
                  Sell
                </ToggleButton>
                <ToggleButton
                  color="primary"
                  value="buy"
                  aria-label="buy"
                  sx={[styles.button, { borderBottom: "2px solid #d3d4d9" }]}
                  disabled={disableBuy}
                >
                  buy
                </ToggleButton>
              </ToggleButtonGroup>
              {localStorage.getItem("instruments") && graphicForPosition ? (
                <Typography variant="h4" color="#00a7e1" my={2}>
                  {chosenInstrument.split("_")[0]}
                </Typography>
              ) : (
                <Typography variant="h4" color="#00a7e1" my={2}>
                  {localStorage.getItem("graphic")}
                </Typography>
              )}
              <Grid
                container
                sx={{ width: "100%", height: "350px"}}
                justifyContent={"center"}
              >
                <AreaChart
                  margin={{right: 55}}
                  width={1250}
                  height={400}
                  data={graphic == "sell" ? chartSellData : chartBuyData}
                  animationEasing="linear"
                  type="monotone"
                >
                  {graphic === "sell" ? (
                    <XAxis
                      tickFormatter={formatTimestamp}
                      domain={
                        [
                          chartSellData[0].time,
                          chartSellData[chartSellData.length - 1].time,
                        ]
                      }
                      dataKey="time"
                      interval={0}
                      type="number"
                      tickCount={6}
                    />
                  ) : (
                    <XAxis
                      tickFormatter={formatTimestamp}
                      domain={[
                        chartBuyData[0].time,
                        chartBuyData[chartBuyData.length - 1].time,
                      ]}
                      dataKey="time"
                      interval={0}
                      type="number"
                      tickCount={6}
                    />
                  )}
                  <YAxis domain={[180, 200]} />
                  <Tooltip content={<CustomTooltip />} />
                  <Legend />
                  {graphic === "sell" ? (
                    <Area dataKey="sell" stroke="#00a7e1" fill="#f1f9fc" />
                  ) : null}
                  {graphic === "buy" ? (
                    <Area
                      dataKey="buy"
                      stroke="#00a7e1"
                      fill="#f1f9fc"
                      type="stepAfter"
                    />
                  ) : null}
                </AreaChart>
              </Grid>
              <Box sx={{ position: "absolute", bottom: 0, width: "83.3%" }}>
                <Grid
                  item
                  alignContent={"center"}
                  sx={{
                    marginBottom: 0,
                    marginTop: "57px",
                    position: "relative",
                  }}
                >
                  {status < MARGIN_CALL_PERCENTAGE ? (
                    <StyledIconButton
                      sx={styles.icon}
                      aria-label="upload picture"
                      component="label"
                      onClick={handleOpen}
                    >
                      <AlarmAddOutlinedIcon />
                      <Typography color="#FF0000" sx={{ style: "bold" }}>
                        Margin Call
                      </Typography>
                    </StyledIconButton>
                  ) : null}
                  <PositionsTable
                    rows={openPositions}
                    setRow={setRow}
                    handleOnRemove={handleOnRemove}
                    setData={setOpenPositions}
                  />
                </Grid>
                <Grid
                  container
                  justifyContent={"flex-end"}
                  alignItems={"center"}
                  sx={styles.result}
                >
                  <Grid item xs={2}>
                    <Box sx={styles.footer}>
                      LIVE RESULT{" "}
                      <Typography
                        sx={[
                          styles.numbers,
                          liveResult < 0
                            ? { color: "#fa6464" }
                            : { color: "#3f3f3f" },
                        ]}
                      >
                        {liveResult.toFixed(2)}
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={2}>
                    <Box sx={styles.footer}>
                      FREE FUNDS{" "}
                      <Typography sx={styles.numbers}>
                        {freeCash.toFixed(2)}
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={2}>
                    <Box sx={styles.footer}>
                      BLOCKED FUNDS{" "}
                      <Typography sx={styles.numbers}>
                        {lockedCash.toFixed(2)}
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid container item xs={2} justifyContent={"center"}>
                    <Box sx={styles.footer}>Status: </Box>
                    <Box sx={styles.status}>
                      <LinearProgressWithLabel value={Math.floor(status)} />
                    </Box>
                  </Grid>
                </Grid>
              </Box>
            </Grid>
          </Grid>
          <div>
            <Modal
              open={openFirstTime || showModal || waitNotification}
              onClose={handleClose}
              aria-labelledby="parent-modal-title"
              aria-describedby="parent-modal-description"
            >
              <Box sx={[styles.content, { width: 400 }]}>
                <Typography
                  sx={styles.modalTitle}
                  variant="h5"
                  fontWeight="bold"
                >
                  Margin Call
                </Typography>
                <Typography sx={styles.modalContent} fontWeight="bold">
                  Your account status drops below 45%.
                </Typography>
                <Typography>
                  Open positions could be closed if account status drops below
                  25%. Please, deposit additional funds.
                </Typography>
              </Box>
            </Modal>
          </div>
        </Grid>
      )}
      {showErrorModal ? <T212Modal /> : null}
    </React.Fragment>
  );
});

export default StockMarket;
