package com.t212.instruments.instruments.manager.api.rest;

import com.t212.instruments.instruments.manager.api.rest.models.ApiResponse;
import com.t212.instruments.instruments.manager.api.rest.models.InstrumentInput;
import com.t212.instruments.instruments.manager.core.InstrumentService;
import com.t212.instruments.instruments.manager.core.models.Instrument;
import com.t212.instruments.instruments.manager.core.models.InstrumentWithPrice;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(value = "*")
@RequestMapping(value = "/api/v1/instruments")
public class InstrumentController {
    private final InstrumentService instrumentService;

    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addInstrument(@RequestBody InstrumentInput instrument) {
        try {
            Instrument createdInstrument = instrumentService.addInstrument(instrument.name(), instrument.fullname(), instrument.ticker(), instrument.type(), instrument.quantity(), instrument.leverage(), instrument.marketName());
            return ResponseEntity.status(201).body(new ApiResponse(201, "", createdInstrument));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid body data"));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> listInstruments(@RequestParam("page") Optional<Integer> page, @RequestParam("pageSize") Optional<Integer> pageSize) {
        if (!page.isPresent() && !pageSize.isPresent()) {
            List<Instrument> instruments = instrumentService.listAllInstruments();
            return ResponseEntity.status(200).body(new ApiResponse(200, "", instruments));
        }
        if ((page.isPresent() && page.get() <= 0) || (page.isPresent() && pageSize.get() <= 0)) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid parameters"));
        }
        if (page.isPresent() && pageSize.isPresent()) {
            List<Instrument> instruments = instrumentService.listInstruments(page.get(), pageSize.get());
            return ResponseEntity.status(200).body(new ApiResponse(200, "", instruments));
        }
        return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid parameters"));
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<ApiResponse> getInstrument(@PathVariable("id") int id) {
        if (id <= 0) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid path variable"));
        }
        try {
            Instrument instrument = instrumentService.getById(id);
            return ResponseEntity.status(200).body(new ApiResponse(200, "", instrument));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(404).body(new ApiResponse(404, "Not found instrument"));
        }
    }

    @GetMapping(value = "/most-used")
    public ResponseEntity<ApiResponse> getMostUsedInstruments() {
        try {
            List<InstrumentWithPrice> instruments = instrumentService.getTop10Instruments();
            return ResponseEntity.status(200).body(new ApiResponse(200, "", instruments));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(404).body(new ApiResponse(404, "Not found instrument"));
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<ApiResponse> removeInstrument(@PathVariable("id") int id) {
        if (id <= 0) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid path variable"));
        }

        try {
            boolean successfullyDeleted = instrumentService.removeInstrument(id);
            if (successfullyDeleted) {
                return ResponseEntity.status(200).body(new ApiResponse(200, "Successfully deleted"));
            }
        } catch (DataAccessException e) {
            return ResponseEntity.status(404).body(new ApiResponse(400, "Not found instrument with this id"));
        }
        return ResponseEntity.status(404).body(new ApiResponse(404, "Not found instrument with this id"));
    }
}
