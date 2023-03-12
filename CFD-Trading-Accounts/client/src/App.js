import React, {useEffect} from 'react';
import {
  Routes,
  Route,
  useNavigate,
  Navigate
} from "react-router-dom";
import { useDispatch } from "react-redux";
import NotFound from "./screens/not-found/NotFound";
import Login from "./screens/login/Login";
import { getUserData } from "./requests";
import { add } from "./store/slices/userSlice";
import StockMarket from "./screens/stock-market/StockMarket";
import './App.css';

function App() {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const getUser = async () => {
    let userId = localStorage.getItem("user");
    if (userId) {
      try {
        const res = await getUserData(parseInt(userId));
        dispatch(add(res.data.result));
      } catch (error) {
        if (error.response.status === 401) {
          navigate("/login");
        }
      }
    }
  };

  useEffect(() => {
      getUser();
  }, []);

  return (
    <div className="App">
      <Routes>
        <Route path="*" element={<NotFound />} />
        <Route path="/" element = {<Navigate to = {"/login"} />}></Route>
        <Route path="login" element={<Login />} />
        <Route path="/home" element={<StockMarket />} />
      </Routes>
    </div>
  );
}

export default App;
