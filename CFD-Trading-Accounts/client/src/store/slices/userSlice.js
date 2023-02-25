import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export const userSlice = createSlice({
  name: "user",
  initialState: {
    id: 0,
    username: "",
    email: "",
    imageUrl: "",
    phone: "",
    role: "",
    updatedAt: "",
    createdAt: "",
    value: 0,
    balance: 0,
  },
  reducers: {
    add: (state, action) => {
      state.id = action.payload.id;
      state.username = action.payload.username;
      state.imageUrl = action.payload.imageUrl;
      state.email = action.payload.email;
      state.phone = action.payload.phone;
      state.role = action.payload.role;
      state.balance = action.payload.balance;
      state.createdAt = action.payload.createdAt;
      state.updatedAt = action.payload.updatedAt;
    },
  },
});

export const { add } = userSlice.actions;

export default userSlice.reducer;
