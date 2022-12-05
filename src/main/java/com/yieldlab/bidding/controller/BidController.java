package com.yieldlab.bidding.controller;

import com.yieldlab.bidding.exception.InternalServerException;
import com.yieldlab.bidding.exception.NoBiddingException;
import com.yieldlab.bidding.request.BidRequest;
import com.yieldlab.bidding.service.BidServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class BidController {
    private final BidServiceImpl bidService;

    @GetMapping("{adId}")
    public ResponseEntity<String> bid(@PathVariable Integer adId, @RequestParam HashMap<String, String> params) {
        try {
            return ResponseEntity.ok(bidService.sendBidRequest(new BidRequest(adId, params)).get());
        } catch (InterruptedException | ExecutionException e) {
            if (e.getCause().getClass() == NoBiddingException.class) {
                throw new NoBiddingException(e.getMessage());
            }
            throw new InternalServerException(e.getMessage());
        }
    }
}
