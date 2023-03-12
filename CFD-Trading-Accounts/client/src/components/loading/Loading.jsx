import React from "react";
import LinearProgress from "@mui/material/LinearProgress";
import { Grid, Typography } from "@mui/material";

const Loading = () => {
  return (
    <Grid sx={styles.wrapper}>
      <Grid sx={styles.progress}>
        <Typography color="white" variant="h3" mb={4} fontFamily="Pacifica">
          Trading 212
        </Typography>
        <LinearProgress sx = {styles.loading} />
      </Grid>
    </Grid>
  );
};

export default Loading;

const styles = {
  wrapper: {
    backgroundColor: "#00a7e1",
    position: "absolute",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
  },
  progress: {
    position: "relative",
    width: "400px",
    margin: "0 auto",
    top: "50%",
    height: "100px",
    transform: "translate(0%, -60%)",
  },
  loading: {
    height: "6px"
  }
};
