import React, { FC, useState, useEffect } from "react";
import {
  Button,
  Grid,
  TextField,
  MenuItem,
  FormControl,
  InputLabel,
  Select,
  SelectChangeEvent,
  Typography,
  Paper,
} from "@mui/material";
import { registrationStyle } from "./RegistrationStyle";
import { textFieldStyle } from "./RegistrationStyle";
import { postUser } from "../../requests";
import { Link, useNavigate } from "react-router-dom";
import { useCookies } from "react-cookie";
import { useDispatch } from "react-redux";
import { add } from "../../store/slices/userSlice";

const Registration = ({ header = false }) => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [cookies, setCookies] = useCookies();
  const defaultFormValues = {
    username: "",
    email: "",
    password: "",
    phone: "",
    role: "USER",
  };
  const defaultFormErrors = {
    usernameError: "",
    emailError: "",
    passwordError: "",
    phoneError: "",
  };
  const [formValues, setFormValues] = useState(defaultFormValues);
  const [formErrors, setFormErrors] = useState(defaultFormErrors);
  const handleUsernameInputChange = (e) => {
    const newUsernameValue = e.currentTarget.value;
    setFormValues({
      ...formValues,
      username: newUsernameValue,
    });
  };
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
  const handleRoleSelectChange = (e) => {
    const newRoleValue = e.target.value;
    setFormValues({
      ...formValues,
      role: newRoleValue,
    });
  };
  const handlePhoneInputChange = (e) => {
    const newPhoneValue = e.currentTarget.value;
    setFormValues({
      ...formValues,
      phone: newPhoneValue,
    });
  };
  const handleSubmit = async (event) => {
    let isValidUsername = formValues.username.length >= 3;
    let isValidPassword = formValues.password.length >= 6;
    event.preventDefault();

    if (isValidUsername && isValidPassword) {
      const res = await postUser(
        formValues.username,
        formValues.email,
        formValues.phone,
        formValues.role,
        formValues.password
      );
      if (res.data && res.status === 201) {
        if (res.data.result.success) {
          setCookies("secure", res.data.result.token, {
            expires: new Date(Date.now() + res.data.result.expires),
          });
          setCookies("timestamp", res.data.result.expiresIn);
          dispatch(add(res.data.result.user));
          localStorage.setItem("user", res.data.result.user.id);
          navigate("/");
        }
      } else if (res.data && res.status === 200) {
        if (!res.data.result.success) {
          setFormErrors({
            ...formErrors,
            emailError: res.data.result.errors.fields[0].message,
          });
        }
      }
    }
  };

  useEffect(() => {
    localStorage.clear();
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
        sx={registrationStyle}
      >
        <Paper elevation={3} sx={styles.paper}>
          <h1>Sign Up</h1>
          <Grid container item justifyContent="center" xs={12} rowSpacing={0}>
            <form
              onSubmit={handleSubmit}
              style={{ width: "calc(100% - 20px)", padding: "10px" }}
            >
              <Grid item xs={12} my={2}>
                <TextField
                  helperText={formErrors.usernameError}
                  id="username-input"
                  name="username"
                  label="Username"
                  type="text"
                  value={formValues.username}
                  sx={textFieldStyle}
                  onChange={handleUsernameInputChange}
                />
              </Grid>
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
                  helperText={formErrors.phoneError}
                  id="phone-input"
                  name="phone"
                  label="Phone"
                  type="tel"
                  value={formValues.phone}
                  sx={textFieldStyle}
                  onChange={handlePhoneInputChange}
                />
              </Grid>
              <Grid item xs={12} my={2}>
                <FormControl fullWidth>
                  <InputLabel id="role-select-label">Role</InputLabel>
                  <Select
                    labelId="role-select-label"
                    id="role-select"
                    value={formValues.role}
                    label="Role"
                    onChange={handleRoleSelectChange}
                  >
                    <MenuItem value={"USER"} selected>
                      User
                    </MenuItem>
                    <MenuItem value={"MERCHANT"}>Merchant</MenuItem>
                  </Select>
                </FormControl>
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
                Submit
              </Button>
              <Typography mt={2} mb={2}>
                Already have an account? <Link to="/login">Log in</Link>
              </Typography>
            </form>
          </Grid>
        </Paper>
      </Grid>
    </>
  );
};

export default Registration;

const styles = {
  paper: {
    borderRadius: "15px",
    padding: "30px",
  },
  button: {
    height: "50px",
    marginTop: "20px",
  },
};
