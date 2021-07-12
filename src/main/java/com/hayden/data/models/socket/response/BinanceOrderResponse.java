package com.hayden.data.models.socket.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hayden.data.models.rest.response.UrlParams;
import com.hayden.data.models.shared.request.UrlFactory;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@Scope("prototype")
@NoArgsConstructor
@UrlParams({UrlFactory.Vals.SYMBOL})
public class BinanceOrderResponse implements ExchangeResponse {

    @JsonProperty("e")
    private String eventType;
    @JsonProperty("E")
    private Long eventTime;
    @JsonProperty("T")
    private Long transactionTime;
    @JsonProperty("s")
    private String symbol;
    @JsonProperty("b")
    private List<List<Double>> bids;
    @JsonProperty("a")
    private List<List<Double>> asks;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("result")
    private String result;

    @Override
    public <T extends Asset> AssetDTO<T> visit(AssetDTO<T> dto)
    {
        dto.addBidAsks(eventTime, asks, bids, symbol);
        return dto;
    }
}
