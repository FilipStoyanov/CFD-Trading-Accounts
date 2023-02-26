import axios from "axios";

const API_USERS_ENDPOINT = "http://localhost:8080/api/v1/users";
const API_LOGIN_ENDPOINT = "http://localhost:8084/api/v1/login";
const API_SIGNUP_ENDPOINT = "http://localhost:8080/api/v1/signup";
const API_WEBSOCKET_ENDPOINT = "http://localhost:8080/api/v1/websocket-gateway";
const API_WEBSOCKET_ACCOUNT_BALANCE = "http://localhost:8083/api/v1/users/";

const options = {
  headers: {
    "Content-Type": "application/json",
  },
};

const authHeader = {
  headers: {
    "Content-Type": "application/json",
  },
};

export const postUser = (username, email, phone, role, password) => {
  return axios.post(
    API_SIGNUP_ENDPOINT,
    {
      username: username,
      email: email,
      phone: phone,
      role: role,
      password: password,
    },
    options
  );
};

export const fetchAccountBalance = (userId) => {
  return axios.get(`${API_WEBSOCKET_ACCOUNT_BALANCE}${userId}/balance`, {
    headers: {
      "Content-Type": "application/json",
    }
  });
}

export const openWebsocketConnection = () => {
  return axios.get(API_WEBSOCKET_ENDPOINT, {
    headers: {
      "Content-Type": "application/json",
    },
  });
};

export const loginUser = (email, password) => {
  return axios.post(
    API_LOGIN_ENDPOINT,
    {
      username: email,
      password: password,
    },
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
};

export const getUserData = (id, token) => {
  return axios.get(API_USERS_ENDPOINT + `/${id}`, {
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });
};
