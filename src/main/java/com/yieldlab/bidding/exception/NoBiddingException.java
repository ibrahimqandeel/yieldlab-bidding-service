package com.yieldlab.bidding.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoBiddingException extends ResponseStatusException {
    public NoBiddingException(String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }
}
