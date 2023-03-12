export const styles = {
  wrapper: {
    position: "relative",
    width: "100%",
    height: "100%",
    backgroundColor: "#ffffff",
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
    backgroundColor: "#d3d4d9",
  },
  text: {
    display: "block",
  },
  button: {
    "&.MuiTable-root": {
      border: "10px solid #00a7e1",
    },
    "&.Mui-selected": {
      backgroundColor: "#00a7e1"
    },
    marginTop: "25px",
  },
  positions: {
    margin: "10px auto 30px",
    width: "100%",
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
  group: {
    backgroundColor: "#E7EBF0",
    width: "100%",
  },
  scrollView: {
    width: "100%",
    height: "115vh",
    paddingLeft: "40px",
    paddingTop: "30px"
  },
  loadMore: {
    marginBottom: "20px",
    color: "#00a7e1"
  },
  button: {
    backgroundColor: "#ffffff",
    width: "50%",
    fontWeight: "500",
    "&:hover": {
      backgroundColor: "#E7EBF0",
    },
    "&:active": {
      backgroundColor: "#E7EBF0",
    },
  },
};
