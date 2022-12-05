package com.yieldlab.bidding.service;

import com.yieldlab.bidding.request.BidRequest;

import java.util.concurrent.CompletableFuture;

public interface BidService {
    CompletableFuture<String> sendBidRequest(BidRequest bidRequest);
}
