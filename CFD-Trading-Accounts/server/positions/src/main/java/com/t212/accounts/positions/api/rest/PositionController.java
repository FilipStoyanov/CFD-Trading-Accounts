package com.t212.accounts.positions.api.rest;

import com.t212.accounts.positions.api.rest.models.ApiResponse;
import com.t212.accounts.positions.api.rest.models.OpenPositionOutput;
import com.t212.accounts.positions.api.rest.models.PositionInput;
import com.t212.accounts.positions.api.rest.models.PositionUpdateInput;
import com.t212.accounts.positions.core.PositionsService;
import com.t212.accounts.positions.core.models.Position;
import com.t212.accounts.positions.core.models.PositionWithPrices;
import com.t212.accounts.positions.gateways.KafkaGateway;
import com.t212.accounts.positions.lib.events.ClosePositionEvent;
import com.t212.accounts.positions.lib.events.OpenPositionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(value = "*")
@RequestMapping(value = "/api/v1/users")
public class PositionController {

    @Autowired
    private KafkaGateway kafkaGateway;
    private final PositionsService positionsService;

    public PositionController(PositionsService positionsService) {
        this.positionsService = positionsService;
    }

    @GetMapping(value = "{id}/positions")
    public ResponseEntity<ApiResponse> getOpenPositions(@PathVariable("id") long id) {
        if (id <= 0) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid parameters"));
        }
        try {
            List<OpenPositionOutput> positions = positionsService.getOpenPositionsById(id);
            return ResponseEntity.status(200).body(new ApiResponse(200, "", positions));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(404).body(new ApiResponse(404, "Not found open positions"));
        }
    }

    @GetMapping(value = "{id}/positions-type")
    public ResponseEntity<ApiResponse> getTypesOfOpenPositions(@PathVariable("id") long userId) {
        if (userId <= 0) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid parameters"));
        }
        try {
            List<String> types = positionsService.getAllTypesOfPositions(userId);
            return ResponseEntity.status(200).body(new ApiResponse(200, "", types));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(404).body(new ApiResponse(404, "Not found open positions"));
        }
    }

    @GetMapping(value = "{id}/open-positions")
    public ResponseEntity<ApiResponse> listOpenPositionsWithCurrentPrices(@PathVariable("id") long id) {
        if (id <= 0) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid parameters"));
        }
        try {
            List<PositionWithPrices> positions = positionsService.getOpenPositionsWithPricesById(id);
            return ResponseEntity.status(200).body(new ApiResponse(200, "", positions));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(404).body(new ApiResponse(404, "Not found open positions"));
        }
    }

    @GetMapping(value = "{id}/close-positions")
    public ResponseEntity<ApiResponse> listClosedPositions(@PathVariable("id") long id) {
        if (id <= 0) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid parameters"));
        }
        try {
            List<Position> positions = positionsService.getClosePositionsById(id);
            return ResponseEntity.status(200).body(new ApiResponse(200, "", positions));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(404).body(new ApiResponse(404, "Not found open positions"));
        }
    }

    @PutMapping(value = "{userId}/positions/{positionId}")
    public ResponseEntity<ApiResponse> updatePosition(@PathVariable long userId, @PathVariable long positionId, @RequestBody PositionUpdateInput positionUpdate) {
        if (userId < 1 || positionId < 1) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid path variable"));
        }
        try {
            Position updatedPosition = positionsService.updatePosition(userId, positionId, positionUpdate.positionType());
            ClosePositionEvent pEvent = new ClosePositionEvent(userId, updatedPosition.ticker, positionUpdate.positionType(), positionUpdate.quantity(), positionUpdate.buyPrice(), positionUpdate.sellPrice(), System.currentTimeMillis());
            kafkaGateway.sendClosePositionEvent(pEvent);
            return ResponseEntity.status(200).body(new ApiResponse(200, "Successfully updated", updatedPosition));
        } catch (DataAccessException e) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Not successfully updated"));
        }
    }

    @PostMapping(value = "{userId}/positions")
    public ResponseEntity<ApiResponse> addPosition(@PathVariable long userId, @RequestBody PositionInput position) {
        if (userId < 1) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid path variable"));
        }
        try {
            Position newPosition = positionsService.addPosition(userId, position.instrumentId(), position.quantity(), position.type(), position.buyPrice(), position.sellPrice());
            OpenPositionEvent pEvent = new OpenPositionEvent(userId, newPosition.ticker, position.type(), position.quantity(), position.buyPrice(), position.sellPrice(), System.currentTimeMillis());
            kafkaGateway.sendOpenPositionEvent(pEvent);
            return ResponseEntity.status(200).body(new ApiResponse(200, "Successfully created", newPosition));
        } catch (DataAccessException e) {
            return ResponseEntity.status(400).body(new ApiResponse(400, "Not successfully created"));
        }
    }
}
