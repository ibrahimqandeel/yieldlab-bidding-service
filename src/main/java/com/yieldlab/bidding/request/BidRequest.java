package com.yieldlab.bidding.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Data
@AllArgsConstructor
public class BidRequest {

    @NotNull
    private Integer id;
    private HashMap<String, String> attributes;
}
