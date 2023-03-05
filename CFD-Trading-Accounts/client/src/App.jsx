import React, { useEffect } from "react";
import {
  Routes,
  Route,
  useLocation,
  useNavigate,
} from "react-router-dom";
import "./App.scss";
import Login from "./screens/login/Login";
import { useDispatch, useSelector } from "react-redux";
import { useCookies } from "react-cookie";
import { getUserData } from "./requests";
import NotFound from "./screens/not-found/NotFound";
import { add } from "./store/slices/userSlice";
import StockMarket from "./screens/stock-market/StockMarket";

function App() {
  const user = useSelector((state) => state.user.user);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [cookies, setCookie, removeCookies] = useCookies();

  const getUser = async () => {
    let userId = localStorage.getItem("user");
    if (userId) {
      try {
        const res = await getUserData(parseInt(userId), cookies.secure);
        dispatch(add(res.data.result));
      } catch (error) {
        if (error.response.status === 401) {
          navigate("/login");
          removeCookies("secure");
          removeCookies("timestamp");
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
        <Route path="login" element={<Login />} />
        <Route path="/home" element={<StockMarket />} />
      </Routes>
    </div>
  );
}

export default App;
