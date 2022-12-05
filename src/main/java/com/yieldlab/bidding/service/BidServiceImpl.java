package com.yieldlab.bidding.service;

import com.yieldlab.bidding.exception.NoBiddingException;
import com.yieldlab.bidding.externalapi.BidderApiClient;
import com.yieldlab.bidding.request.BidRequest;
import com.yieldlab.bidding.response.AuctionResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BidServiceImpl {

    private final String bidUrl;
    private final Executor executor;
    private final BidderApiClient bidderClient;

    public BidServiceImpl(@Value("${bidders.api.url}") String bidUrl, Executor executor, BidderApiClient bidderClient) {
        this.bidUrl = bidUrl;
        this.executor = executor;
        this.bidderClient = bidderClient;
    }

    @Async
    public CompletableFuture<String> sendBidRequest(BidRequest bidRequest) {
        List<String> biddersUrl = Arrays.asList(bidUrl.split(","));
        List<CompletableFuture<AuctionResponse>> futuresList = new ArrayList<>();

        //send http call to bidders
        biddersUrl.forEach(url -> {
            CompletableFuture<AuctionResponse> responseFuture =
                    CompletableFuture.supplyAsync(() -> bidderClient.sendBidRequest(URI.create(url), bidRequest), executor)
                            .exceptionally(ex -> {
                                log.error(ex.getMessage());
                                return null;
                            });
            futuresList.add(responseFuture);
        });

        //collect api responses
        CompletableFuture<List<AuctionResponse>> allCompletableFuture
                = CompletableFuture
                .allOf(futuresList.toArray(new CompletableFuture[futuresList.size()]))
                .thenApply(
                        future -> futuresList.stream()
                                .map(completableFuture -> completableFuture.join())
                                .collect(Collectors.toList()));

        //find winning bid
        String winner = findWinningBid(allCompletableFuture.join()
                .stream()
                .filter(rec -> rec != null && rec.getContent() != null)
                .collect(Collectors.toList()));
        return CompletableFuture.completedFuture(winner);
    }

    private String findWinningBid(List<AuctionResponse> auctions) {
        if (auctions == null || auctions.isEmpty()) {
            throw new NoBiddingException("No bidding response");
        }
        AuctionResponse auction = auctions
                .stream()
                .max(Comparator.comparingInt(AuctionResponse::getBid))
                .get();
        return auction.getContent().replace("$price$", String.valueOf(auction.getBid()));
    }
}
