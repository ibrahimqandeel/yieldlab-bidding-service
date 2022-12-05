package com.yieldlab.bidding.service;

import com.yieldlab.bidding.externalapi.BidderApiClient;
import com.yieldlab.bidding.request.BidRequest;
import com.yieldlab.bidding.response.AuctionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceImplTest {

    private BidServiceImpl underTest;

    private String bidUrl = "http://localhost:8081,http://localhost:8082,http://localhost:8083";
    @Mock
    private BidderApiClient apiClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        underTest = new BidServiceImpl(bidUrl, Executors.newSingleThreadExecutor(), apiClient);
    }

    @Test
    public void test_sendBidRequest() {
        //given
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("a", "1000");
        attributes.put("b", "1500");
        attributes.put("c", "2000");
        BidRequest bidRequest = new BidRequest(1, attributes);
        AuctionResponse auctionResponse_1 = new AuctionResponse(1, 1000, "a:$price$");
        AuctionResponse auctionResponse_2 = new AuctionResponse(1, 1500, "b:$price$");
        AuctionResponse auctionResponse_3 = new AuctionResponse(1, 2000, "c:$price$");

        //when
        when(apiClient.sendBidRequest(URI.create("http://localhost:8081"), bidRequest)).thenReturn(auctionResponse_1);
        when(apiClient.sendBidRequest(URI.create("http://localhost:8082"), bidRequest)).thenReturn(auctionResponse_2);
        when(apiClient.sendBidRequest(URI.create("http://localhost:8083"), bidRequest)).thenReturn(auctionResponse_3);

        //expected
        String expected = "c:2000";

        //actual
        String actual = underTest.sendBidRequest(bidRequest).join();

        //then
        verify(apiClient, times(1)).sendBidRequest(URI.create("http://localhost:8081"), bidRequest);
        verify(apiClient, times(1)).sendBidRequest(URI.create("http://localhost:8082"), bidRequest);
        verify(apiClient, times(1)).sendBidRequest(URI.create("http://localhost:8083"), bidRequest);

        assertEquals(expected, actual);
    }

    @Test
    public void test_sendBidRequest_with_exception() {
        //given
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put("a", "1000");
        attributes.put("b", "1500");
        attributes.put("c", "2000");
        BidRequest bidRequest = new BidRequest(1, attributes);
        AuctionResponse auctionResponse_1 = new AuctionResponse(1, 1000, "a:$price$");
        AuctionResponse auctionResponse_2 = new AuctionResponse(1, 1500, "b:$price$");
        AuctionResponse auctionResponse_3 = null;

        //when
        when(apiClient.sendBidRequest(URI.create("http://localhost:8081"), bidRequest)).thenReturn(auctionResponse_1);
        when(apiClient.sendBidRequest(URI.create("http://localhost:8082"), bidRequest)).thenReturn(auctionResponse_2);
        when(apiClient.sendBidRequest(URI.create("http://localhost:8083"), bidRequest)).thenReturn(auctionResponse_3); //return null since the bidder api is not responding

        //expected
        String expected = "b:1500";

        //actual
        String actual = underTest.sendBidRequest(bidRequest).join();

        //then
        verify(apiClient, times(1)).sendBidRequest(URI.create("http://localhost:8081"), bidRequest);
        verify(apiClient, times(1)).sendBidRequest(URI.create("http://localhost:8082"), bidRequest);
        verify(apiClient, times(1)).sendBidRequest(URI.create("http://localhost:8083"), bidRequest);
        assertEquals(expected, actual);
    }

}