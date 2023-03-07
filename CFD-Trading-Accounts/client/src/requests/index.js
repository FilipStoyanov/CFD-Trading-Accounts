import axios from "axios";

const API_USERS_ENDPOINT = "http://localhost:8082/api/v1/users";
const API_LOGIN_ENDPOINT = "http://localhost:8084/api/v1/login";
const API_WEBSOCKET_ACCOUNT_BALANCE = "http://localhost:8085/api/v1/users/";
const API_INSTRUMENTS = "http://localhost:8083/api/v1/instruments-prices";

export const getInstrumentsWithPagination = (page, pageSize) => {
  return axios.get(
    `${API_INSTRUMENTS}?page=${page}&pageSize=${pageSize}`,
    {
      headers: {
        "Content-Type": "application/json",
      }
    }
  )
}

export const getGraphicDataForInstrument = (instrumentId) => {
  return axios.get(
    `${API_INSTRUMENTS}/${instrumentId}`,
    {
      headers: {
        "Content-Type": "application/json"
      }
    }
  )
}

export const openMarketPosition = (position, userId) => {
  return axios.post(
    `${API_USERS_ENDPOINT}/${userId}/positions`,
    {
      instrumentId: position.id,
      quantity: position.quantity,
      type: position.positionType,
      buyPrice: parseFloat(position.buyPrice),
      sellPrice: parseFloat(position.sellPrice)
    },
    {
      headers: {
        "Content-Type": "application/json",
      }
    }
  )
}

export const closeMarketPosition = (position, userId) => {
  const sell = position.type === 'SHORT' ? position.price : position.currentPrice;
  const buy = position.type === 'LONG' ? position.currentPrice : position.price;
  return axios.put(
    `${API_USERS_ENDPOINT}/${userId}/positions`,
    {
      ticker: position.ticker,
      quantity: position.quantity,
      buyPrice: parseFloat(buy),
      sellPrice: parseFloat(sell),
      positionType: position.type,
    },
    {
      headers: {
        "Content-Type": "application/json",
      }
    }
  )
}

export const getInstrumentsWithOffset = (offset, rows) => {
  return axios.get(
    `${API_INSTRUMENTS}?offset=${offset}&rows=${rows}`,
    {
      headers: {
        "Content-Type": "application/json",
      }
    }
  )
}

export const fetchAccountBalance = (userId) => {
  return axios.get(`${API_WEBSOCKET_ACCOUNT_BALANCE}${userId}/balance`, {
    headers: {
      "Content-Type": "application/json",
    }
  });
}

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
