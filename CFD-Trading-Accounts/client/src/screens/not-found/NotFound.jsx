import { Grid, Typography, Button } from "@mui/material";
import { useCookies } from "react-cookie";
import { useNavigate } from "react-router-dom";

const NotFound = ({}) => {
  const navigate = useNavigate();
  const [cookies, setCookie, removeCookies] = useCookies();
  return (
    <Grid container justifyContent="center" sx={styles.wrapper}>
      <Grid container justifyContent="center" flexDirection={"column"}>
        <Typography variant="h3">OOPS! PAGE NOT FOUND</Typography>
        <Typography variant="h6">
          Sorry the page you're looking for doesn't exists.
        </Typography>
      </Grid>
      {cookies.secure ? (
        <Button
          variant="contained"
          size="large"
          sx={styles.button}
          onClick={() => navigate("/")}
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
