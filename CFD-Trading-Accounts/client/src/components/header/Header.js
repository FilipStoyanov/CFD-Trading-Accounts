import { Typography, Grid } from "@mui/material";
import LogoutIcon from '@mui/icons-material/Logout';
import React from "react";
import { useNavigate } from "react-router-dom";
const Header = ({ cash }) => {
  const navigate = useNavigate();
  const logout = () => {
    localStorage.clear();
    navigate("/login");
  }
  return (
    <Grid container sx={styles.header} pl={3} justifyContent="space-between">
      <Grid item xs={4} lg={1}>
        <Typography sx={styles.title}>Home</Typography>
      </Grid>
      <Grid container item xs={6} justifyContent={"space-evenly"}>
        <Grid item sx={{display: "flex"}}>
          <Typography sx={styles.account}>Account value</Typography>
          <Typography pl={1} sx={styles.amount}>{cash} $</Typography>
        </Grid>
        <Grid item sx={{display: "flex"}}>
        <Typography sx={styles.account}>Hello, </Typography>
          <Typography pl={1} sx={styles.username}>filip</Typography>
        </Grid>
        <Grid item sx={{display: "flex"}}>
        <LogoutIcon sx={{color: "#00a7e1", marginRight: "10px"}} onClick = {logout} />
        <Typography sx={styles.logout} onClick = {logout}>Logout</Typography>
        </Grid>
      </Grid>
    </Grid>
  );
};

export default Header;

const styles = {
  header: {
    height: "50px",
    backgroundColor: "#ffffff",
    borderBottom: "1px solid #d3d4d9",
    alignItems: "center",
  },
  title: {
    fontSize: "24px",
    fontStyle: "normal",
    color: "#00a7e1"
  },
  account: {
    fontSize: "15px",
    color: "#747980",
    fontWeight: "bold",
  },
  logout: {
    fontSize: "15px",
    color: "#747980",
    fontWeight: "bold",
    cursor: "pointer",
    "&:hover": {
      color: "#00a7e1"
    }
  },
  amount: {
    fontSize: "15px",
    fontWeight: "bold",
    textTransform: "uppercase"
  },
  username: {
    fontSize: "15px"
  }
};
