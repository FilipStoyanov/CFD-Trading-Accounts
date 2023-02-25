import React, { useState, useEffect } from "react";
import PositionsTable from "../../components/positions-table/PositionsTable";
import { useNavigate } from "react-router-dom";
import LinearProgressWithLabel from "../../components/progress-bar/LinearProgressWithLabel";
import { useCookies } from "react-cookie";
import AlarmAddOutlinedIcon from "@mui/icons-material/AlarmAddOutlined";
import {
  Grid,
  Typography,
  TextField,
  Box,
  IconButton,
  Modal,
  Button,
} from "@mui/material";
import { openWebsocketConnection } from "../../requests";
import InstrumentCard from "../../components/instrument-card/InstrumentCard";
import { styled } from '@mui/material/styles';
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

const MARGIN_CALL_PERCENTAGE = 45;
const StyledIconButton = styled(IconButton)(({ theme }) => ({
  '&:hover': {
    backgroundColor: 'transparent',
  },
}));
const StockMarket = ({}) => {
  const navigate = useNavigate();
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

  const handleOpen = () => {
    setShowModal(true);
  };

  const handleClose = () => {
    setShowModal(false);
  };

  const handleOnClick = (event) => {
    setChartData([]);
    setChosenInstrument(event.currentTarget.getAttribute("data-key"));
  };

  const checkForMarginCall = () => {
    if (!sessionStorage.getItem("marginCall") && status < 45) {
      sessionStorage.setItem("marginCall", true);
      setOpenFirstTime(true);
    }
    if (sessionStorage.getItem("marginCall") && status > 45) {
        sessionStorage.clear();
        setOpenFirstTime(false);
    }
  }

  const addDataToChart = (currentChartInstrument) => {
    if(Date.now() % 5 === 0) {
      setChartData((prevData) => [
        ...prevData,
        {
          time: new Date().toLocaleTimeString(),
          sell: currentChartInstrument.sellPrice,
          buy: currentChartInstrument.buyPrice,
        },
      ]);
    }
  }

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
    const websocketResponse = await openWebsocketConnection(cookies.secure);
    if (websocketResponse.status === 200) {
      // todo: get url from the websocketResponse
      const sock = new SockJS(
        `${websocketResponse.data.result}`,
        {},
        {
          withCredentials: false,
        }
      );
      const client = Stomp.over(sock);
      client.heartbeat.incoming = 2000;
      client.heartbeat.outgoing = 2000;
      client.connect({}, function () {
        client.subscribe("/cfd/quotes/1", function (message) {
          const jsonObject = JSON.parse(message.body);
          console.log(jsonObject);
          setLoading(false);
          const currentChartInstrument = Object.values(
            jsonObject["instruments"]
          )[chosenInstrument];
          addDataToChart(currentChartInstrument);
          let sum = Object.values(jsonObject.openPositions).reduce(function (
            accumulator,
            currentValue
          ) {
            return accumulator + currentValue.result;
          },
          0);
          setLiveResult(sum);
          setOpenPositions(Object.values(jsonObject.openPositions));
          setFreeCash(jsonObject.freeCash);
          setLockedCash(jsonObject.lockedCash);
          setStatus(jsonObject.status + 40);
        })
        client.subscribe("/cfd/stocks/most-used", function (message) {
          const jsonObject = JSON.parse(message.body);
          setInstruments(Object.values(jsonObject));
        });
      });
      client.onWebSocketClose = function () {
          openWS();
      }
    }
  };

  useEffect (() => {
    setTimeout(() => {
      checkForMarginCall();
    }, 3000);
  }, [openFirstTime]);

  useEffect(() => {
    openWS();
  }, []);


  return loading ? (
    <Loading />
  ) : (
    <Grid container justifyContent="center" sx={styles.wrapper}>
      <Grid container justifyContent="flex-start" mt={5} sx={styles.wrapper}>
        <Grid
          container
          item
          xs={12}
          xl={2}
          spacing={5}
          px={0}
          sx={styles.instruments}
        >
          <Grid
            item
            xs={12}
            justifyContent="center"
            my={1}
            sx={{ width: "100%" }}
          >
            <TextField id="outlined-basic" label="Search" variant="outlined" />
          </Grid>
          {instruments.map((current, index) => {
            return (
              <Grid
                item
                xs={12}
                key={current.name}
                data-key={index}
                onClick={handleOnClick}
              >
                <InstrumentCard
                  name={current.name}
                  sellPrice={current.sell.toFixed(2)}
                  buyPrice={current.buy.toFixed(2)}
                  minQuantity={current.minQuantity}
                  marketName={current.marketName}
                  margin={current.margin}
                />
              </Grid>
            );
          })}
        </Grid>
        <Grid item xs={12} xl={10} sx={styles.positions}>
          {/* <Typography variant="h4" color="#ffffff" mb={4}>
            {instruments[chosenInstrument].name}
          </Typography> */}
          {/* <Grid
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
          </Grid> */}
          <Box sx={{ position: "absolute", bottom: 0, width: "83.3%" }}>
            <Grid
              item
              alignContent={"center"}
              sx={{ marginBottom: 0, marginTop: "57px", position: "relative" }}
            >
              {status < MARGIN_CALL_PERCENTAGE ? (
                <StyledIconButton
                  sx={styles.icon}
                  aria-label="upload picture"
                  component="label"
                  onClick={handleOpen}
                >
                  <AlarmAddOutlinedIcon />
                <Typography color="#FF0000" sx={{style: "bold"}}>Margin Call</Typography>
                </StyledIconButton>
              ) : null}
              <PositionsTable rows={openPositions} />
            </Grid>
            <Grid
              container
              justifyContent={"center"}
              alignItems={"center"}
              sx={styles.result}
            >
              <Grid item xs={3}>
                <Typography>LIVE RESULT {liveResult.toFixed(2)}</Typography>
              </Grid>
              <Grid item xs={3}>
                <Typography>FREE FUNDS {freeCash.toFixed(2)}</Typography>
              </Grid>
              <Grid item xs={3}>
                <Typography>BLOCKED FUNDS {lockedCash.toFixed(2)}</Typography>
              </Grid>
              <Grid container item xs={3} justifyContent={"center"}>
                <Typography>Status: </Typography>
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
          open={openFirstTime|| showModal}
          onClose={handleClose}
          aria-labelledby="parent-modal-title"
          aria-describedby="parent-modal-description"
        >
          <Box sx={[styles.content, { width: 400 }]}>
            <Typography sx={styles.modalTitle} variant="h5" fontWeight="bold">
              Margin Call
            </Typography>
            <Typography sx={styles.modalContent} fontWeight="bold">
              Your account status drops below 45%.
            </Typography>
            <Typography>
              Open positions could be closed if account status drops below 25%.
              Please, deposit additional funds.
            </Typography>
          </Box>
        </Modal>
      </div>
    </Grid>
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
  title: {
    position: "absolute",
    top: "50px",
  },
  instruments: {
    maxHeight: "100vh",
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
    margin: "0 auto",
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
      backgroundColor: "none"
    },
    position: "absolute",
    color: "#FF0000",
    left: 10,
    top: 0,
    zIndex: 10,
  },
};
