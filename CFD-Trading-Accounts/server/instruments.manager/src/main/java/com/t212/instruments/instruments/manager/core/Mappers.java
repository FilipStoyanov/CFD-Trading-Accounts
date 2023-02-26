package com.t212.instruments.instruments.manager.core;

import com.t212.instruments.instruments.manager.core.models.Instrument;
import com.t212.instruments.instruments.manager.core.models.InstrumentWithPrice;
import com.t212.instruments.instruments.manager.repositories.models.InstrumentDAO;
import com.t212.instruments.instruments.manager.repositories.models.InstrumentWithPricesDAO;

public class Mappers {
    public static Instrument fromResultSetToInstrument(InstrumentDAO instrument) {
        return new Instrument(instrument.id(), instrument.name(), instrument.ticker(), instrument.fullname(), instrument.quantity(), instrument.leverage(), instrument.marketName(),
                instrument.createdAt(), instrument.updatedAt());
    }

    public static InstrumentWithPrice fromResultSetToInstrumentWithPrice(InstrumentWithPricesDAO instrument) {
        return new InstrumentWithPrice(instrument.id(), instrument.name(), instrument.ticker(), instrument.fullname(), instrument.quantity(), instrument.leverage(), instrument.marketName(),
                instrument.buy(), instrument.sell(), instrument.createdAt(), instrument.updatedAt());
    }
}
