package com.yieldlab.bidding.externalapi;

import com.yieldlab.bidding.request.BidRequest;
import com.yieldlab.bidding.response.AuctionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;

@FeignClient(name = "${bidders.client.name}", url = "http://localhost:8080")
public interface BidderApiClient {

    @PostMapping
    AuctionResponse sendBidRequest(URI baseUri, BidRequest request);
}
