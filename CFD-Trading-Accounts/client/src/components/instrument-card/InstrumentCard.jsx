import React, { useState } from "react";
import Box from "@mui/material/Box";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Button from "@mui/material/Button";
import { Grid, TextField } from "@mui/material";
import Typography from "@mui/material/Typography";

const InstrumentCard = ({
  id,
  name,
  sellPrice,
  buyPrice,
  minQuantity,
  marketName,
  margin,
  openPosition
}) => {
  const [hover, setHover] = useState(false);
  return (
    <Card
      sx={{ cursor: "pointer", minWidth: 200, backgroundColor: hover ? "#00a7e1" : "#ffffff", color: hover ? "#ffffff" : "#000000" }}
      onClick={() => localStorage.setItem("graphic", JSON.stringify(name))}
      onMouseOver={() => setHover(true)}
      onMouseOut={() => setHover(false)}
    >
      <CardContent>
        <Typography sx={{ fontSize: 15 }} color={hover ? "#ffffff" : "#3f3f3f"} gutterBottom>
          {name}
        </Typography>
        <TextField
          label="Quantity"
          type="number"
          inputProps={{ min: minQuantity }}
          sx={{ minHeight: "20px", border: "none" }}
          defaultValue={minQuantity}
        />
        <Grid container justifyContent={"space-between"} mt={2}>
          <Button sx={styles.button} onClick = {(e) => {e.stopPropagation(); openPosition({id: id, quantity: minQuantity, buyPrice: buyPrice, sellPrice: sellPrice, positionType: "SHORT"})}}>
            <Typography sx={styles.font}>SELL</Typography>
            <Box display="block">
              <Typography sx={[styles.font, styles.currency, {color: hover ? "#ffffff" : "#000000"}]}>
                $<span className="price">{sellPrice}</span>
              </Typography>
            </Box>
          </Button>
          <Button sx={styles.button} onClick = {(e) => {e.stopPropagation(); openPosition({id: id, quantity: minQuantity, buyPrice: buyPrice, sellPrice: sellPrice, positionType: "LONG"})}}>
            <Typography sx={styles.font}>BUY</Typography>
            <Typography sx={[styles.font, styles.currency, {color: hover ? "#ffffff" : "#000000"}]} variant="body2">
              $<span className={styles.price}>{buyPrice}</span>
            </Typography>
          </Button>
        </Grid>
        <Grid container justifyContent={"center"}>
          <Typography sx={styles.text}>Market Name: {marketName}</Typography>
        </Grid>
        <Grid container justifyContent={"center"}>
          <Typography sx={styles.text}>Margin: {margin} %</Typography>
        </Grid>
      </CardContent>
    </Card>
  );
};

export default InstrumentCard;

const styles = {
  font: {
    fontSize: 12,
    fontWeight: "bold",
    color: "#1bc47d",
  },
  currency: {
    fontSize: 15,
    color: "#000000",
    fontWeight: "normal",
  },
  text: {
    display: "block",
    fontSize: 14,
  },
  button: {
    display: "block",
  },
  normal: {
    display: "block",
  },
  price: {
    color: "#000000",
    fontWeight: "normal",
    fontSize: "26px",
  },
};
