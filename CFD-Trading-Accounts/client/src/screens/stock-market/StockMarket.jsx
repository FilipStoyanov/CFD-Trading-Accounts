import React, { useState, useEffect } from "react";
import PositionsTable from "../../components/positions-table/PositionsTable";
import { useNavigate } from "react-router-dom";
import LinearProgressWithLabel from "../../components/progress-bar/LinearProgressWithLabel";
import { useCookies } from "react-cookie";
import AlarmAddOutlinedIcon from "@mui/icons-material/AlarmAddOutlined";
import { useSelector, useDispatch } from "react-redux";
import { sum, calculateStatus } from "../../utils";
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
import { fetchAccountBalance, getTypesOfOpenPositions, getInstrumentsWithPagination } from "../../requests";
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
const StockMarket = ({}) => {
  const navigate = useNavigate();
  const user = useSelector((state) => state.user.user);
  const instr = useSelector((state) => state.user.instrument);
  const [cookies, setCookie, removeCookies] = useCookies();
  const [instruments, setInstruments] = useState([]);
  const [openPositions, setOpenPositions] = useState([]);
  const [freeCash, setFreeCash] = useState(0);
  const [lockedCash, setLockedCash] = useState(0);
  const [liveResult, setLiveResult] = useState(0);
  const [status, setStatus] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [openFirstTime, setOpenFirstTime] = useState(false);
  const [loading, setLoading] = useState(true);
  const [chosenInstrument, setChosenInstrument] = useState(0);
  const [chartData, setChartData] = useState([]);
  const [waitNotification, setWaitNotification] = useState(false);
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [searchValue, setSearchValue] = useState("");
  const [listOfInstruments, setListOfInstruments] = useState([]);
  const [client, setClient] = useState();
  const [page, setPage] = useState(0);
  const [showLoadButton, setShowLoadButton] = useState(true);
  const [firstOnTheView, setFirstOnTheView] = useState(0);
  const INSTRUMENT_CARD_HEIGHT = 240;
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
    setChartData([]);
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

  // CONVERT BLOB OBJECT TO JSON
  const handleBlob = (blob) => {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = () => {
        try {
          const jsonString = reader.result;
          const jsonObject = JSON.parse(jsonString);
          resolve(jsonObject);
        } catch (error) {
          reject(error);
        }
      };
      reader.onerror = () => {
        reject(reader.error);
      };

      reader.readAsText(blob);
    });
  };

  const openWS = async () => {
    const result = await getTypesOfOpenPositions(user.id);
    if (result.status === 200 && result.data && result.data.result) {
      const types = result.data.result.filter((elem, index, self) => {
        return index === self.indexOf(elem);
      });
      types.map((current) => {
        const URL = `http://localhost:8079/websocket?category=${current}`;
        const DEV_URL = "http://localhost:8080/websocket";
        const sock = new SockJS(
          DEV_URL,
          {},
          {
            withCredentials: true,
            transports: ["websocket"],
            headers: {
              "Access-Control-Allow-Origin": "*",
            },
          }
        );
        const client = Stomp.over(sock);
        setClient(client);
        client.heartbeat.incoming = 2000;
        client.heartbeat.outgoing = 2000;
        client.connect({}, () => {
          client.subscribe(`/cfd/quotes/${user.id}`, (message) => {
            const jsonObject = JSON.parse(message.body);
            setTimeout(() => {
              setLoading(false);
            }, 3000);
            setShowErrorModal(false);
            setLiveResult(jsonObject.result.toFixed(2));
            setOpenPositions(Object.values(jsonObject.openPositions));
            let cash = parseFloat(localStorage.getItem("cash"));
            setLockedCash(jsonObject.lockedCash.toFixed(2));
            const free = cash + parseFloat(jsonObject.result) - jsonObject.lockedCash;
            setFreeCash(free.toFixed(2));
            localStorage.setItem("locked", jsonObject.lockedCash.toFixed(2));
            setStatus(calculateStatus(jsonObject.lockedCash, cash));
          });
          client.subscribe(`/cfd/balance/${user.id}`, (message) => {
            console.log("live: " + liveResult);
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
        });
        client.onWebSocketClose = () => {
          openWS();
        };
      });
    }
  };

  useEffect(() => {
    localStorage.removeItem("input");
    localStorage.removeItem("scroll");
    localStorage.removeItem("instruments");
    const intervalId = setInterval(() => {
      checkForMarginCall();
    }, 1000);

    return () => clearInterval(intervalId);
  }, []);

  useEffect(() => {
    const paginationMsg = { page: firstOnTheView };
    if (client) {
        client.send(
          `/app/${user.id}/instruments`,
          {},
          JSON.stringify(paginationMsg)
        );
    }
  },[firstOnTheView, loading]);

  useEffect(() => {
    if(client) {
      client.send(
        `/app/${user.id}/graphic`,
        {},
        JSON.stringify({instrumentName: JSON.parse(localStorage.getItem("instruments"))[chosenInstrument].ticker})
      )
    }
  },[loading, chosenInstrument]);

  const getInstruments = async () => {
    const res = await getInstrumentsWithPagination(page);
    if(res.status === 200 && res.data && res.data.result) {
      const instruments = [...listOfInstruments, ...res.data.result];
      if(res.data.result.length === 0) {
        setShowLoadButton(false);
      }
      dispatch(update(instruments));
      localStorage.setItem("instruments", JSON.stringify(instruments));
      setListOfInstruments([...instruments]);
    }
  }

  const handleScroll = (event) => {
    let pos = Math.floor((event.target.scrollTop - 100) / INSTRUMENT_CARD_HEIGHT);
    pos = (pos < 0) ? 0 : pos;
    localStorage.setItem("scroll", pos);
    setFirstOnTheView(pos);
  }

  useEffect(() => {
    getInstruments();
  },[page]);


  useEffect(() => {
    getAccountBalance();
    if(loading) {
      openWS();
    }
  }, [loading]);

  useEffect(() => {
      if(client) {
        client.subscribe(
          `/cfd/users/${user.id}/instruments/`,
          (message) => {
            const jsonObject = JSON.parse(message.body);
            if(listOfInstruments.length > 0) {
              const ins = JSON.parse(localStorage.getItem("instruments"));
              setListOfInstruments([...ins.slice(0, parseInt(localStorage.getItem("scroll"))), ...jsonObject, ...ins.slice(parseInt(localStorage.getItem("scroll"))  + jsonObject.length)]);
            }
            // if (
            //   !localStorage.getItem("input") ||
            //   localStorage.getItem("input") === ""
            // ) {
            //   setListOfInstruments(jsonObject.concat(new Array(7)));
            // } else {
            //   const ins = jsonObject.filter(
            //     (elem1) =>
            //       elem1.name
            //         .toLowerCase()
            //         .indexOf(localStorage.getItem("input").toLowerCase()) > -1
            //   );
            //   setListOfInstruments(ins);
            // }
            // setInstruments(jsonObject);
            // addDataToChart(jsonObject[chosenInstrument]);
          }
        );

        client.subscribe(
          `/cfd/users/${user.id}/graphic`,
          (message) => {
            const jsonObject = JSON.parse(message.body);
            addDataToChart(jsonObject);
          }
        )
      }
  },[loading])

  return (
    <React.Fragment>
      {loading ? (
        <Loading />
      ) : (
        <Grid container justifyContent="center" sx={styles.wrapper}>
          <Grid container item xs={12} xl={10} sx={styles.header}>
            <Header cash={(parseFloat(localStorage.getItem("cash")) + parseFloat(liveResult))} />
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
              onScroll = {handleScroll} 
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
                sx={{width: "100%"}}
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
                    <>
                    <Grid
                      item
                      xs={12}
                      key={current.name}
                      data-key={index}
                      onClick={handleOnClick}
                      my={3}
                    >
                      <InstrumentCard
                        name={current.name}
                        sellPrice={current.sell.toFixed(2)}
                        buyPrice={current.buy.toFixed(2)}
                        minQuantity={current.quantity}
                        marketName={current.marketName}
                        margin={(current.leverage * 100).toFixed(0)}
                      />
                    </Grid>
                    {index === (listOfInstruments.length - 1) && showLoadButton ? <Button sx={styles.loadMore} onClick = {() => {setPage(page+1)}}>Load More</Button> : null}
                    </>
                  );
                })}
                
              {listOfInstruments.length === 0 && (
                <Typography px={1} mt={4} textAlign="center">Not found results</Typography>
              )}
              </Box>
            </Grid>
            <Grid item xs={12} xl={10} sx={styles.positions}>
              {localStorage.getItem("instruments") ? (
                <Typography variant="h4" color="#ffffff" mb={4}>
                  {JSON.parse(localStorage.getItem("instruments"))[chosenInstrument].name}
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
                  <PositionsTable rows={openPositions} />
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
                        {liveResult}
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={2}>
                    <Box sx={styles.footer}>
                      FREE FUNDS{" "}
                      <Typography sx={styles.numbers}>{freeCash}</Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={2}>
                    <Box sx={styles.footer}>
                      BLOCKED FUNDS{" "}
                      <Typography sx={styles.numbers}>{lockedCash}</Typography>
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
};

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
    marginBottom: "20px"
  }
};
