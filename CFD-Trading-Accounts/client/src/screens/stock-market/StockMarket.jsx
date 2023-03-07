import React, { useState, useEffect, useRef, useCallback } from "react";
import PositionsTable from "../../components/positions-table/PositionsTable";
import LinearProgressWithLabel from "../../components/progress-bar/LinearProgressWithLabel";
import AlarmAddOutlinedIcon from "@mui/icons-material/AlarmAddOutlined";
import { useSelector, useDispatch } from "react-redux";
import { calculateStatus } from "../../utils";
import Header from "../../components/header/Header";
import {
  Grid,
  Typography,
  TextField,
  Box,
  IconButton,
  Modal,
  Button,
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
  LineChart,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  Line,
} from "recharts";
import T212Modal from "../../components/modal/Modal";
import { update } from "../../store/slices/instrumentSlice";

const MARGIN_CALL_PERCENTAGE = 45;
const StyledIconButton = styled(IconButton)(({ theme }) => ({
  "&:hover": {
    backgroundColor: "transparent",
  },
}));
const StockMarket = React.memo(({}) => {
  const user = useSelector((state) => state.user.user);
  const instr = useSelector((state) => state.user.instrument);
  const [instruments, setInstruments] = useState([]);
  const [openPositions, setOpenPositions] = useState([]);
  const [freeCash, setFreeCash] = useState(0);
  const [lockedCash, setLockedCash] = useState(0);
  const [liveResult, setLiveResult] = useState(0);
  const [status, setStatus] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [openFirstTime, setOpenFirstTime] = useState(false);
  const [loading, setLoading] = useState(true);
  const [chosenInstrument, setChosenInstrument] = useState(1);
  const [chartData, setChartData] = useState([]);
  const [waitNotification, setWaitNotification] = useState(false);
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [searchValue, setSearchValue] = useState("");
  const [listOfInstruments, setListOfInstruments] = useState([]);
  const [page, setPage] = useState(0);
  const [showLoadButton, setShowLoadButton] = useState(true);
  const INSTRUMENT_CARD_HEIGHT = 250;
  const VIEWED_INSTRUMENTS = 4;
  const PAGE_SIZE = 10;
  const opened = useRef(0);
  const DEV_URL = "http://localhost:8080/websocket";
  const HAPROXY_URL = "http://localhost:8079/websocket";
  const dispatch = useDispatch();

  const handleOpen = () => {
    setShowModal(true);
  };

  const handleOnChangeSearchInput = (e) => {
    setSearchValue(e.target.value);
    localStorage.setItem("input", e.target.value);
    setListOfInstruments(
      instruments.filter(
        (stock) =>
          stock.name.toLowerCase().indexOf(e.target.value.toLowerCase()) > -1
      )
    );
  };

  const handleClose = () => {
    setShowModal(false);
    setOpenFirstTime(false);
    setWaitNotification(false);
  };

  const handleOnClick = (event) => {
    const instr = JSON.parse(localStorage.getItem("instruments")).find(
      (e) => (e.id = event.currentTarget.getAttribute("data-key"))
    );
    setChartData([
      {
        time: new Date().toLocaleTimeString(),
        sell: instr.sell,
        buy: instr.buy,
      },
    ]);
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

  const getAccountBalance = async () => {
    const res = await fetchAccountBalance(user.id);
    if (res.data.status === 200) {
      if (res.data.result && res.data.result.balance) {
        localStorage.setItem("cash", res.data.result.balance);
      }
    }
  };
  const addDataToChart = (currentChartInstrument) => {
    const clearData = (data) => {
      if (data.length > 50) {
        return data.splice(Math.floor(data.length / 2), 1);
      }
      return data;
    };
    setChartData((prevData) => [
      ...clearData(prevData),
      {
        time: new Date().toLocaleTimeString(),
        sell: currentChartInstrument.sell,
        buy: currentChartInstrument.buy,
      },
    ]);
  };

  const openConnection = useCallback(() => {
    const sock = new SockJS(
      HAPROXY_URL,
      {sessionId: true},
      {
        withCredentials: true,
        transports: ["websocket"],
        headers: {
          "Access-Control-Allow-Origin": "*",
          Upgrade: "WebSocket",
        },
      }
    );
    console.log("OPEN");

    // const client = Stomp.over(sock);
    // client.heartbeat.incoming = 2000;
    // client.heartbeat.outgoing = 2000;

    // client.connect({}, () => {
    //   setTimeout(() => {
    //     setLoading(false);
    //   }, 4000);
    //   setShowErrorModal(false);
    //   client.subscribe(`/cfd/quotes/${user.id}`, (message) => {
    //     const jsonObject = JSON.parse(message.body);
    //     const newArr = [...openPositions];
    //     Object.values(jsonObject.openPositions).map((item) => {
    //       const indexOfElement = openPositions.findIndex(
    //         (element) => element.ticker == item.ticker
    //       );
    //       if (indexOfElement == -1) {
    //         newArr.push(item);
    //       } else {
    //         newArr[indexOfElement] = item;
    //       }
    //       setOpenPositions(newArr);
    //     });
    //     setLockedCash(jsonObject.lockedCash);
    //     setLiveResult(jsonObject.result);
    //     const cash =
    //       parseFloat(localStorage.getItem("cash")) + jsonObject.result;
    //     setFreeCash(cash - jsonObject.lockedCash);
    //     setStatus(
    //       calculateStatus(
    //         jsonObject.lockedCash,
    //         parseFloat(localStorage.getItem("cash")) + liveResult
    //       )
    //     );
    //   });
    //   client.subscribe(`/cfd/balance/${user.id}`, (message) => {
    //     const jsonObject = JSON.parse(message.body);
    //     if (jsonObject && jsonObject.balance) {
    //       localStorage.setItem("cash", jsonObject.balance);
    //     }
    //   });
    //   client.subscribe(`/cfd/errors`, (message) => {
    //     const jsonObject = JSON.parse(message.body);
    //     if (jsonObject.status === "error") {
    //       setShowErrorModal(true);
    //     }
    //   });
    //   client.onWebSocketClose = () => {
    //     openConnection();
    //   };
    // });
  }, []);

  useEffect(() => {
    localStorage.removeItem("input");
    localStorage.removeItem("scroll");
    localStorage.removeItem("instruments");
    const intervalId = setInterval(() => {
      checkForMarginCall();
    }, 1000);

    return () => clearInterval(intervalId);
  }, []);

  const getInstruments = async () => {
    const res = await getInstrumentsWithPagination(page, PAGE_SIZE);
    if (res.status === 200 && res.data && res.data.result) {
      const instruments = [...listOfInstruments, ...res.data.result];
      if (res.data.result.length === 0) {
        setShowLoadButton(false);
      }
      dispatch(update(instruments));
      localStorage.setItem("instruments", JSON.stringify(instruments));
      setListOfInstruments([...instruments]);
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
    setChartData([
      {
        time: new Date().toLocaleTimeString(),
        sell: instr.sell,
        buy: instr.buy,
      },
    ]);
    setChosenInstrument(newRow);
  };

  const getDataForGraphic = async () => {
    let id = 1;
    if (localStorage.getItem("instruments")) {
      id = JSON.parse(localStorage.getItem("instruments")).find(
        (e) => e.id == chosenInstrument
      )?.id;
    }
    const res = await getGraphicDataForInstrument(id);
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
    const intervalId = setInterval(() => {
      getDataForGraphic();
    }, 10000);
    return () => clearInterval(intervalId);
  }, []);

  useEffect(() => {
    getInstruments();
    if (chartData.length === 0) {
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
                      ? `calc(100vh + ${openPositions.length}*35px)`
                      : `calc(100vh + 175px)`,
                },
              ]}
            >
              <Grid
                item
                xs={12}
                justifyContent="center"
                my={1}
                sx={{ width: "100%" }}
              >
                <TextField
                  id="outlined-basic"
                  label="Search"
                  variant="outlined"
                  onChange={handleOnChangeSearchInput}
                  value={searchValue}
                />
              </Grid>
              <Box sx={styles.scrollView}>
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
              {localStorage.getItem("instruments") ? (
                <Typography variant="h4" color="#ffffff" mb={4}>
                  {
                    JSON.parse(localStorage.getItem("instruments")).find(
                      (e) => e.id == chosenInstrument
                    )?.name
                  }
                </Typography>
              ) : null}
              <Grid
                container
                sx={{ width: "100%", height: "350px" }}
                justifyContent={"center"}
              >
                <LineChart width={685} height={400} data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="time" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line
                    type="monotone"
                    dataKey="sell"
                    stroke="red"
                    dot={false}
                  />
                  <Line
                    type="monotone"
                    dataKey="buy"
                    stroke="blue"
                    dot={false}
                  />
                </LineChart>
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

const styles = {
  wrapper: {
    position: "relative",
    width: "100%",
    height: "100%",
    backgroundColor: "#00a7e1",
  },
  header: {
    position: "absolute",
    right: 0,
    zIndex: 2,
  },
  title: {
    position: "absolute",
    top: "50px",
  },
  instruments: {
    overflow: "auto",
    padding: "0 10px",
    backgroundColor: "#E7EBF0",
  },
  text: {
    display: "block",
  },
  button: {
    marginTop: "25px",
  },
  positions: {
    margin: "30px auto 0",
  },
  result: {
    height: "50px",
    backgroundColor: "#f6f6f8",
    marginTop: "-20px",
  },
  status: {
    marginTop: "2.5px",
    marginLeft: "10px",
    width: "150px",
  },
  content: {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: 400,
    bgcolor: "background.paper",
    border: "2px solid #00a7e1",
    backgroundColor: "#00a7e1",
    boxShadow: 24,
    padding: "50px",
    color: "#ffffff",
  },
  icon: {
    ":hover": {
      backgroundColor: "none",
    },
    position: "absolute",
    color: "#FF0000",
    left: 10,
    top: 8,
    zIndex: 10,
  },
  footer: {
    fontSize: 10,
    color: "#747980",
    textTransform: "uppercase",
    fontWeight: "bold",
    display: "flex",
    alignItems: "center",
  },
  numbers: {
    marginLeft: "5px",
    color: "#3f3f3f",
    fontWeight: 600,
    fontSize: 14,
  },
  scrollView: {
    width: "100%",
    height: "115vh",
    paddingLeft: "40px",
  },
  loadMore: {
    marginBottom: "20px",
  },
};
