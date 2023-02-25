//package com.t212.cfdaccounts.cfdaccounts.core;
//
//import com.t212.cfdaccounts.cfdaccounts.core.models.Instrument;
//import com.t212.cfdaccounts.cfdaccounts.repositories.InstrumentRepository;
//import com.t212.cfdaccounts.cfdaccounts.repositories.models.InstrumentDAO;
//import org.springframework.dao.DataAccessException;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//@Service
//public class InstrumentService {
//    private final InstrumentRepository instrumentRepository;
//
//    public InstrumentService(InstrumentRepository instrumentRepository) {
//        this.instrumentRepository = instrumentRepository;
//    }
//
//    public Instrument addInstrument(String name, String fullName, BigDecimal quantity, BigDecimal leverage, String marketName) {
//        InstrumentDAO instrument = instrumentRepository.addInstrument(name, fullName, quantity, leverage, marketName);
//        return Mappers.fromResultSetToInstrument(instrument);
//    }
//
//    public Instrument getById(long instrumentId) throws EmptyResultDataAccessException {
//        InstrumentDAO instrument = instrumentRepository.getInstrument(instrumentId);
//        return Mappers.fromResultSetToInstrument(instrument);
//    }
//
//    public List<Instrument> listInstruments(Integer page, Integer pageSize) {
//        return instrumentRepository.listInstruments(page, pageSize).stream().map(current -> Mappers.fromResultSetToInstrument(current)).collect(Collectors.toList());
//    }
//
//    public boolean removeInstrument(long instrumentId) throws DataAccessException {
//        return instrumentRepository.deleteInstrument(instrumentId);
//    }
//
//}
