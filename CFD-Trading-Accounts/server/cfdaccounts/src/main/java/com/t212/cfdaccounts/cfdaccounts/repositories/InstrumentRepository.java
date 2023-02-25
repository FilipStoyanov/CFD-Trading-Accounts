package com.t212.cfdaccounts.cfdaccounts.repositories;

import com.t212.cfdaccounts.cfdaccounts.repositories.models.InstrumentDAO;
import java.math.BigDecimal;
import java.util.List;

public interface InstrumentRepository {
    InstrumentDAO addInstrument(String name, String fullName, BigDecimal minQuantity, BigDecimal leverage, String marketName);

    InstrumentDAO getInstrument(long id);

    List<InstrumentDAO> listInstruments(Integer page, Integer pageSize);

    boolean deleteInstrument(long id);

    InstrumentDAO getInstrumentByName(String name);

    List<InstrumentDAO> listAllInstruments();
}
