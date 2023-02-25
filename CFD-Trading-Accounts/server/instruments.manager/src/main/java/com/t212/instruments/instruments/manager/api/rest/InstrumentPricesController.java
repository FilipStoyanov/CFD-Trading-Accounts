package com.t212.instruments.instruments.manager.api.rest;

import com.t212.instruments.instruments.manager.api.rest.models.ApiResponse;
import com.t212.instruments.instruments.manager.core.InstrumentService;
import com.t212.instruments.instruments.manager.core.models.Instrument;
import com.t212.instruments.instruments.manager.core.models.InstrumentWithPrice;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(value = "*")
@RequestMapping(value = "/api/v1/instruments-prices")
public class InstrumentPricesController {
    private final InstrumentService instrumentService;

    public InstrumentPricesController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getInstrumentsWithQuotes() {
        try {
            List<InstrumentWithPrice> instruments = instrumentService.getInstrumentsWithPrices();
            return ResponseEntity.status(200).body(new ApiResponse(200, "", instruments));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "An error has occurred"));
        }
    }

}
