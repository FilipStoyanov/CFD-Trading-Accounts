import React from "react";
import { formatTimestamp } from "../../utils";
import { Typography, Grid } from "@mui/material";
const CustomTooltip = ({ active, payload }) => {
  if (active && payload && payload.length) {
    const data = payload[0].payload;
    return (
      <Grid sx={styles.wrapper}>
        <Typography>{`Time: ${formatTimestamp(data.time)}`}</Typography>
        {data.sell ? (
          <Typography>{`Price: ${data.sell}`}</Typography>
        ) : (
          <Typography>{`Price: ${data.buy}`}</Typography>
        )}
      </Grid>
    );
  }

  return null;
};

const styles = {
  wrapper: {
    backgroundColor: "#00a7e1",
    color: "#ffffff",
    border: "10px solid #00a7e1",
  },
};

export default CustomTooltip;
