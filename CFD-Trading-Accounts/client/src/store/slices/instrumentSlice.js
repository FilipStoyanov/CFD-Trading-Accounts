import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export const instrumentsSlice = createSlice({
  name: "instrument",
  initialState: [],
  reducers: {
    update: (state, action) => {
      return action.payload
    },
  },
});

export const { update } = instrumentsSlice.actions;
export default instrumentsSlice.reducer;
