package com.hayden.data.models.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import com.hayden.decision.models.shared.Bar;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.IntStream;

@Component
@Scope("prototype")
@Data
@NoArgsConstructor
public class CoinGeckoBars implements ExchangeResponse {


    @JsonProperty("prices")
    List<List<Double>> returnPrices;
    @JsonProperty("market_caps")
    List<List<Double>> market_caps;
    @JsonProperty("total_volumes")
    List<List<Double>> total_volumes;

    @Override
    public <T extends Asset> AssetDTO<T> visit(AssetDTO<T> dto)
    {
        var prices = new ConcurrentSkipListMap<Date, Bar>();
        IntStream.range(0, returnPrices.size())
                .forEachOrdered(i -> {
                    var date = Date.from(Instant.ofEpochMilli(returnPrices.get(i).get(0).longValue()));
                    var ohc = returnPrices.get(i).get(1);
                    var vol = total_volumes.get(i).get(1);
                    prices.put(date, new Bar(date, ohc, ohc, ohc, ohc, vol));
                });
        dto.setPrices(prices);
        return dto;
    }
}
