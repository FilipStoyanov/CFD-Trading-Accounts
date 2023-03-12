import { configureStore } from "@reduxjs/toolkit";
import userReducer from "./slices/userSlice";
import instrumentReducer from "./slices/instrumentSlice";
import { persistReducer, persistStore } from 'redux-persist';
import storage from "redux-persist/lib/storage";
import {combineReducers} from '@reduxjs/toolkit';

import {
    FLUSH,
    REHYDRATE,
    PAUSE,
    PERSIST,
    PURGE,
    REGISTER,
  } from 'redux-persist'

const persistConfig = {
    key: 'root',
    version: 1,
    storage
};

const reducer = combineReducers({
    user: userReducer,
    instrument: instrumentReducer,
});

const persistedReducer = persistReducer(persistConfig, reducer);


export const store = configureStore({
    reducer: {
        user: persistedReducer,
    },
    middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, PAUSE, PERSIST, REGISTER],
      },
    }),
})

export const persistor = persistStore(store);

export type RootState = ReturnType<typeof store.getState>