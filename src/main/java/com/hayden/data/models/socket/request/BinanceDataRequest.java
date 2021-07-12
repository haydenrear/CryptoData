package com.hayden.data.models.socket.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hayden.data.models.shared.response.ExchangeRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Scope("prototype")
@Data
@NoArgsConstructor
public class BinanceDataRequest implements ExchangeRequest {

    @JsonProperty("method")
    private String SUBSCRIBE;
    @JsonProperty("id")
    private int id;
    @JsonProperty("params")
    private List<String> TICKERS;

    public BinanceDataRequest(
            int id,
            List<String> TICKERS
    )
    {
        this.SUBSCRIBE = "SUBSCRIBE";
        this.id = id;
        this.TICKERS = TICKERS;
    }


}
