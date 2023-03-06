package com.t212.instruments.instruments.manager.api.rest;

import com.t212.instruments.instruments.manager.api.rest.models.ApiResponse;
import com.t212.instruments.instruments.manager.core.InstrumentService;
import com.t212.instruments.instruments.manager.core.models.InstrumentWithPrice;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(value = "*")
@RequestMapping(value = "/api/v1/instruments-prices")
public class InstrumentPricesController {
    private final InstrumentService instrumentService;

    public InstrumentPricesController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getInstrumentsWithQuotes(@RequestParam("page") Optional<Integer> page, @RequestParam("pageSize") Optional<Integer> pageSize, @RequestParam("offset") Optional<Integer> offset, @RequestParam("rows") Optional<Integer> rows) {
        if ((page.isPresent() && page.get() < 0) || (page.isPresent() && pageSize.get() < 0)) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid parameters"));
        }
        if (!page.isPresent() && !pageSize.isPresent() && !offset.isPresent() && !rows.isPresent()) {
            List<InstrumentWithPrice> instruments = instrumentService.getInstrumentsWithPrices();
            return ResponseEntity.status(200).body(new ApiResponse(200, "", instruments));
        }
        if (page.isPresent() && pageSize.isPresent()) {
            try {
                List<InstrumentWithPrice> instruments = instrumentService.getPaginatedInstrumentsWithPrices(page.get(), pageSize.get());
                return ResponseEntity.status(200).body(new ApiResponse(200, "", instruments));
            } catch (EmptyResultDataAccessException e) {
                return ResponseEntity.status(400).body(new ApiResponse(400, "An error has occurred"));
            }
        }
        if (offset.isPresent() && rows.isPresent()) {
            try {
                List<InstrumentWithPrice> instruments = instrumentService.getInstrumentsPricesWithOffset(offset.get(), rows.get());
                return ResponseEntity.status(200).body(new ApiResponse(200, "", instruments));
            } catch (EmptyResultDataAccessException e) {
                return ResponseEntity.status(400).body(new ApiResponse(400, "An error has occurred"));
            }
        }
        return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid parameters"));
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<ApiResponse> getInstrumentWithPrice(@PathVariable("id") long id) {
        try {
            InstrumentWithPrice instruments = instrumentService.getInstrumentWithPrice(id);
            return ResponseEntity.status(200).body(new ApiResponse(200, "", instruments));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "An error has occurred"));
        }
    }

}
