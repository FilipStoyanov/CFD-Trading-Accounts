import React from 'react';
import { Grid, Typography, Button } from "@mui/material";
import { useCookies } from "react-cookie";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";

const NotFound = ({}) => {
  const navigate = useNavigate();
  const user = useSelector((state) => state.user.user);
  return (
    <Grid container justifyContent="center" sx={styles.wrapper}>
      <Grid container justifyContent="center" flexDirection={"column"}>
        <Typography variant="h3">OOPS! PAGE NOT FOUND</Typography>
        <Typography variant="h6">
          Sorry the page you're looking for doesn't exists.
        </Typography>
      </Grid>
      {user.id ? (
        <Button
          variant="contained"
          size="large"
          sx={styles.button}
          onClick={() => navigate("/home")}
        >
          RETURN HOME
        </Button>
      ) : (
        <Button
          variant="contained"
          size="large"
          sx={styles.button}
          onClick={() => navigate("/login")}
        >
          RETURN HOME
        </Button>
      )}
    </Grid>
  );
};

export default NotFound;

const styles = {
  wrapper: {
    marginTop: "200px",
  },
  text: {
    display: "block",
  },
  button: {
    marginTop: "25px",
  },
};
