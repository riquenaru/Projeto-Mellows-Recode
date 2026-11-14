package br.com.mellows.api.exchange;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exchange-requests")
public class ExchangeRequestController {
    private final ExchangeRequestService service;

    public ExchangeRequestController(ExchangeRequestService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ExchangeRequestResponse> create(@Valid @RequestBody ExchangeRequestPayload payload) {
        ExchangeRequestResponse created = service.create(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
