package com.t212.tickers.producer.repositories;

import com.t212.tickers.producer.core.models.InstrumentUpdater;

import java.util.List;

public interface InstrumentRepository {
    void batchUpdate(List<InstrumentUpdater> dataList);
}
