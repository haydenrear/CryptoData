package com.hayden.data.models.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hayden.data.models.rest.To;
import com.hayden.data.services.rest.BinanceDataService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
@NoArgsConstructor
@To(BinanceSymbol.class)
@Data
public class BinanceSymbols implements IntermediateResponse<BinanceSymbol> {

    @JsonProperty("exchangeFilters")
    private List<String> exchangeFilters;
    @JsonProperty("rateLimits")
    private List<Map<String, String>> rateLimits;
    @JsonProperty("serverTime")
    private Long serverTime;
    @JsonProperty("assets")
    private List<BinanceDataService.SymbolAsset> assets;
    @JsonProperty("symbols")
    private List<BinanceSymbol> symbols;
    @JsonProperty("timeZone")
    private String timeZone;

    @Override
    public Publisher<BinanceSymbol> responses()
    {
        return Flux.fromIterable(symbols);
    }

}
