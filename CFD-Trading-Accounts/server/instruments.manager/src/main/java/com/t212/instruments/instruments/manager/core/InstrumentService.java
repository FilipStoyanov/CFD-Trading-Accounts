package com.t212.instruments.instruments.manager.core;

import com.t212.instruments.instruments.manager.core.models.Instrument;
import com.t212.instruments.instruments.manager.core.models.InstrumentUpdater;
import com.t212.instruments.instruments.manager.core.models.InstrumentWithPrice;
import com.t212.instruments.instruments.manager.lib.events.StockPriceUpdateEvents;
import com.t212.instruments.instruments.manager.repositories.InstrumentRepository;
import com.t212.instruments.instruments.manager.repositories.models.InstrumentDAO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class InstrumentService {
    private final InstrumentRepository instrumentRepository;
    private final static int BATCH_SIZE = 100;
    private Map<String, InstrumentUpdater> lastPricesForInstruments;


    public InstrumentService(InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
        this.lastPricesForInstruments = new ConcurrentHashMap<>();
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

    public List<InstrumentWithPrice> getPaginatedInstrumentsWithPrices(Integer page, Integer pageSize, String name) throws EmptyResultDataAccessException {
        return instrumentRepository.getPaginatedInstrumentsWithPrices(page, pageSize, name).stream().map(current -> Mappers.fromResultSetToInstrumentWithPrice(current)).collect(Collectors.toList());
    }

    public List<InstrumentWithPrice> getInstrumentsPricesWithOffset(Integer offset, Integer numberOfRows, String name) throws EmptyResultDataAccessException {
        return instrumentRepository.getInstrumentsPricesWithOffset(offset, numberOfRows, name).stream().map(current -> Mappers.fromResultSetToInstrumentWithPrice(current)).collect(Collectors.toList());
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

    @KafkaListener(
            topics = "quotes.raw.cfd",
            groupId = "cfd_stock_prices",
            containerFactory = "StockPricesUpdatedContainerFactory",
            concurrency = "6"
    )
    void listenForQuotes(ConsumerRecord<String, StockPriceUpdateEvents> data) {
        if (data != null) {
            lastPricesForInstruments.put(data.value().ticker(), new InstrumentUpdater(data.value().ticker(), data.value().bid(), data.value().ask()));
        }
    }
    @Scheduled(fixedRate = 5000)
    public void updateInstrumentPrices() {
        List<InstrumentUpdater> batch = lastPricesForInstruments.values().stream().collect(Collectors.toList());
        for (int i = 0; i < batch.size(); i = i + BATCH_SIZE) {
            if (i + BATCH_SIZE >= batch.size()) {
                instrumentRepository.batchUpdate(batch.subList(i, batch.size()));
                break;
            }
            instrumentRepository.batchUpdate(batch.subList(i, i + BATCH_SIZE));
        }
    }

}