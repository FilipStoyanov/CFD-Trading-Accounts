package com.t212.instruments.instruments.manager.repositories;

import com.t212.instruments.instruments.manager.repositories.models.InstrumentDAO;
import com.t212.instruments.instruments.manager.repositories.models.InstrumentWithPricesDAO;

import java.math.BigDecimal;
import java.util.List;

public interface InstrumentRepository {
    InstrumentDAO addInstrument(String name, String fullName, BigDecimal minQuantity, BigDecimal leverage, String marketName);

    InstrumentDAO getInstrument(long id);

    List<InstrumentDAO> listInstruments(Integer page, Integer pageSize);

    boolean deleteInstrument(long id);

    InstrumentDAO getInstrumentByName(String name);

    List<InstrumentDAO> listAllInstruments();

    List<InstrumentWithPricesDAO> getAllInstrumentsWithInitialPrice();

    List<InstrumentWithPricesDAO> getTop10Instruments();
}