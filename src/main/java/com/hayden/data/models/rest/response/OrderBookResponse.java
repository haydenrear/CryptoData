package com.hayden.data.models.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hayden.data.models.shared.request.UrlFactory;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.markets.OrderBook;
import com.hayden.decision.models.shared.AssetDTO;
import com.hayden.decision.models.shared.DateRecord;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
@Scope("prototype")
@Data
@NoArgsConstructor
public class OrderBookResponse implements ExchangeResponse, ApplicationContextAware {

    private String symbol;
    public Long lastUpdateId;
    public List<List<Double>> asks;
    public List<List<Double>> bids;

    @JsonIgnore
    ApplicationContext applicationContext;

    @Override @Autowired
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    public OrderBookResponse(Long lastUpdateId, List<List<Double>> asks, List<List<Double>> bids, String symbol) {
        this.lastUpdateId = lastUpdateId;
        this.asks = asks;
        this.bids = bids;
        this.symbol = symbol;
    }

    public List<DateRecord> getAskValues() {
        return collectValues(asks);
    }

    public List<DateRecord> getBidValues() {
        return collectValues(bids);
    }

    private List<DateRecord> collectValues(List<List<Double>> valuesValues) {
        List<DateRecord> list = new ArrayList<>();
        Double prev = null;
        for (List<Double> values : valuesValues) {
            if(prev == null){
                continue;
            }
            DateRecord dateRecord = new DateRecord(Date.from(Instant.ofEpochMilli(values.get(0).longValue())), (prev - values.get(1)) / prev);
            list.add(dateRecord);
            prev = values.get(1);
        }
        return list;
    }

    @Override
    public <T extends Asset> AssetDTO<T> visit(AssetDTO<T> dto)
    {
        dto.addBidAsks(lastUpdateId, asks, bids, symbol);
        return dto;
    }
}
