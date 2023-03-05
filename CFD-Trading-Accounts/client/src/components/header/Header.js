import { SvgIcon } from "@mui/joy";
import { Typography, Grid, Image } from "@mui/material";
import React from "react";
const Header = ({ cash }) => {
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
  },
  account: {
    fontSize: "15px",
    color: "#747980",
    fontWeight: "bold",
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
