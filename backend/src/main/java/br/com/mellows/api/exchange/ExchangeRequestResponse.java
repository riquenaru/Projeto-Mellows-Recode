package br.com.mellows.api.exchange;

import java.time.Instant;

public record ExchangeRequestResponse(Long id, Long bookId, String status, Instant createdAt) {
    static ExchangeRequestResponse from(ExchangeRequest request) {
        return new ExchangeRequestResponse(request.getId(), request.getBook().getId(),
                request.getStatus(), request.getCreatedAt());
    }
}
