package com.t212.instruments.instruments.manager.core;

import com.t212.instruments.instruments.manager.core.models.Instrument;
import com.t212.instruments.instruments.manager.core.models.InstrumentWithPrice;
import com.t212.instruments.instruments.manager.repositories.InstrumentRepository;
import com.t212.instruments.instruments.manager.repositories.models.InstrumentDAO;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstrumentService {
    private final InstrumentRepository instrumentRepository;

    public InstrumentService(InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
    }

    public Instrument addInstrument(String name, String fullName, String ticker, String type, BigDecimal quantity, BigDecimal leverage, String marketName) throws EmptyResultDataAccessException {
        long typeId = instrumentRepository.getTypeId(type);
        InstrumentDAO instrument = instrumentRepository.addInstrument(name, fullName, ticker, quantity, leverage, typeId, marketName);
        return Mappers.fromResultSetToInstrument(instrument);
    }

    public List<Instrument> listAllInstruments() throws EmptyResultDataAccessException {
        return instrumentRepository.listAllInstruments().stream().map(current -> Mappers.fromResultSetToInstrument(current)).collect(Collectors.toList());
    }


    public Instrument getById(long instrumentId) throws EmptyResultDataAccessException {
        InstrumentDAO instrument = instrumentRepository.getInstrument(instrumentId);
        return Mappers.fromResultSetToInstrument(instrument);
    }

    public List<Instrument> listInstruments(Integer page, Integer pageSize) {
        return instrumentRepository.listInstruments(page, pageSize).stream().map(current -> Mappers.fromResultSetToInstrument(current)).collect(Collectors.toList());
    }

    public List<InstrumentWithPrice> getInstrumentsWithPrices() throws EmptyResultDataAccessException {
        return instrumentRepository.getAllInstrumentsWithInitialPrice().stream().map(current -> Mappers.fromResultSetToInstrumentWithPrice(current)).collect(Collectors.toList());
    }

    public List<InstrumentWithPrice> getPaginatedInstrumentsWithPrices(Integer page, Integer pageSize) throws EmptyResultDataAccessException {
        return instrumentRepository.getPaginatedInstrumentsWithPrices(page, pageSize).stream().map(current -> Mappers.fromResultSetToInstrumentWithPrice(current)).collect(Collectors.toList());
    }

    public List<InstrumentWithPrice> getInstrumentsPricesWithOffset(Integer offset, Integer numberOfRows) throws EmptyResultDataAccessException {
        return instrumentRepository.getInstrumentsPricesWithOffset(offset, numberOfRows).stream().map(current -> Mappers.fromResultSetToInstrumentWithPrice(current)).collect(Collectors.toList());
    }

    public InstrumentWithPrice getInstrumentWithPrice(long id) throws EmptyResultDataAccessException {
        return Mappers.fromResultSetToInstrumentWithPrice(instrumentRepository.getInstrumentWithInitialPrice(id));
    }

    public boolean removeInstrument(long instrumentId) throws DataAccessException {
        return instrumentRepository.deleteInstrument(instrumentId);
    }

    public List<InstrumentWithPrice> getTop10Instruments() throws EmptyResultDataAccessException {
        return instrumentRepository.getTop10Instruments().stream().map(current -> Mappers.fromResultSetToInstrumentWithPrice(current)).collect(Collectors.toList());
    }

}