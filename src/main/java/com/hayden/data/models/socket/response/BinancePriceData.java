package com.hayden.data.models.socket.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import com.hayden.decision.models.shared.Bar;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;


@Data
@Component
@Scope("prototype")
public class BinancePriceData {
    @JsonProperty("t")
    private  Long startTime;
    @JsonProperty("T")
    private  Long closeTime;
    @JsonProperty("s")
    private  String symbol;
    @JsonProperty("i")
    private  String interval;
    @JsonProperty("f")
    private  Long firstTradeId;
    @JsonProperty("L")
    private  Long lastTradeId;
    @JsonProperty("o")
    private  Double open;
    @JsonProperty("c")
    private  Double close;
    @JsonProperty("h")
    private  Double high;
    @JsonProperty("l")
    private  Double low;
    @JsonProperty("v")
    private  Double volume;
    @JsonProperty("n")
    private  int numberTrades;
    @JsonProperty("x")
    private  boolean closed;
    @JsonProperty("q")
    private  Double quoteVolume;
    @JsonProperty("V")
    private  Double takerBuyAssetVol;
    @JsonProperty("Q")
    private  Double takerBuyQuoteAssetVol;
    @JsonProperty("B")
    private  String nothing;

}
