package com.t212.instruments.instruments.manager.repositories;

import com.t212.instruments.instruments.manager.core.models.InstrumentWithPrice;
import com.t212.instruments.instruments.manager.repositories.models.InstrumentDAO;
import com.t212.instruments.instruments.manager.repositories.models.InstrumentWithPricesDAO;

import java.math.BigDecimal;
import java.util.List;

public interface InstrumentRepository {
    InstrumentDAO addInstrument(String name, String fullName, String ticker, BigDecimal minQuantity, BigDecimal leverage, long typeId, String marketName);

    InstrumentDAO getInstrument(long id);

    List<InstrumentDAO> listInstruments(Integer page, Integer pageSize);

    boolean deleteInstrument(long id);

    InstrumentDAO getInstrumentByName(String name);

    List<InstrumentDAO> listAllInstruments();

    List<InstrumentWithPricesDAO> getAllInstrumentsWithInitialPrice();

    InstrumentWithPricesDAO getInstrumentWithInitialPrice(long id);

    List<InstrumentWithPricesDAO> getTop10Instruments();

    List<InstrumentWithPricesDAO> getInstrumentsPricesWithOffset(Integer offset, Integer numberOfRows);

    List<InstrumentWithPricesDAO> getPaginatedInstrumentsWithPrices(Integer page, Integer pageSize);

    long getTypeId(String type);
}
