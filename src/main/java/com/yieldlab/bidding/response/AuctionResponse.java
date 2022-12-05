package com.yieldlab.bidding.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuctionResponse {
    private Integer id;
    private Integer bid;
    private String content;
}
