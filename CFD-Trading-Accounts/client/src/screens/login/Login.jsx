import React, { FC, useState, useEffect } from "react";
import { Button, Grid, Paper, TextField, Typography } from "@mui/material";
import { loginStyle, textFieldStyle } from "./LoginStyle";
import { loginUser } from "../../requests";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { add } from "../../store/slices/userSlice";

const Login = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const defaultFormValues = {
    email: "",
    password: "",
  };
  const defaultFormErrors = {
    emailError: "",
    passwordError: "",
  };
  const [formValues, setFormValues] = useState(defaultFormValues);
  const [formErrors, setFormErrors] = useState(defaultFormErrors);
  const handleEmailInputChange = (e) => {
    const newEmailValue = e.currentTarget.value;
    setFormValues({
      ...formValues,
      email: newEmailValue,
    });
  };
  const handlePasswordInputChange = (e) => {
    const newPasswordValue = e.currentTarget.value;
    setFormValues({
      ...formValues,
      password: newPasswordValue,
    });
  };
  const handleSubmit = async (event) => {
    let isValidPassword = formValues.password.length >= 6;
    event.preventDefault();

    if (isValidPassword) {
      try {
        const res = await loginUser(formValues.email, formValues.password);
        if (res.data && res.status === 200) {
          dispatch(add(res.data.result));
          localStorage.setItem("user", res.data.result.id);
          navigate("/home");
        }
      } catch (error) {
        if (error.response.status === 401) {
          setFormErrors({
            ...formErrors,
            passwordError: "Invalid credentials",
          });
        }
      }
    }
  };

  useEffect(() => {
    localStorage.clear();
    sessionStorage.clear();
  }, []);
  return (
    <>
      <Grid
        container
        item
        xs={11}
        md={7}
        justifyContent="center"
        direction="column"
        sx={loginStyle}
      >
        <Paper elevation={3} sx={styles.paper}>
          <Typography sx={{fontSize: "35px", fontWeight: "bold"}}>Sign In</Typography>
          <Grid container item justifyContent="center" xs={12} rowSpacing={0}>
            <form
              onSubmit={handleSubmit}
              style={{ width: "calc(100% - 20px)", padding: "10px" }}
            >
              <Grid item xs={12} my={2}>
                <TextField
                  helperText={formErrors.emailError}
                  id="email-input"
                  name="Username"
                  label="Username"
                  type="text"
                  value={formValues.email}
                  sx={textFieldStyle}
                  onChange={handleEmailInputChange}
                />
              </Grid>
              <Grid item xs={12} my={2}>
                <TextField
                  helperText={formErrors.passwordError}
                  id="password-input"
                  name="password"
                  label="Password"
                  type="password"
                  value={formValues.password}
                  sx={textFieldStyle}
                  onChange={handlePasswordInputChange}
                />
              </Grid>
              <Button
                variant="contained"
                color="primary"
                type="submit"
                sx={styles.button}
              >
                Sign In
              </Button>
            </form>
          </Grid>
        </Paper>
      </Grid>
    </>
  );
};

export default Login;

const styles = {
  paper: {
    borderRadius: "15px",
    padding: "30px",
  },
  button: {
    height: "50px",
    marginTop: "20px",
    marginBottom: "20px",
    backgroundColor: "#00a7e1",
    "&:hover": {
      backgroundColor: "#dadbe2",
      color: "#00a7e1"
    }
  },
};
