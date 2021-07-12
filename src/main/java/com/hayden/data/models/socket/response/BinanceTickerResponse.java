package com.hayden.data.models.socket.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.models.socket.request.SocketRequest;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import com.hayden.decision.models.shared.Bar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Scope("prototype")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BinanceTickerResponse implements ExchangeResponse, SocketRequest {

    @JsonProperty("e")
    private String eventType;
    @JsonProperty("E")
    private Long eventTime;
    @JsonProperty("s")
    private String symbol;
    @JsonProperty("k")
    private BinancePriceData innerRequest;

    @Override
    public boolean nonNullData()
    {
        return innerRequest != null;
    }

    @Override
    public <T extends Asset> AssetDTO<T> visit(AssetDTO<T> dto)
    {
        dto.addBar(
                Date.from(Instant.ofEpochMilli(innerRequest.getCloseTime())),
                innerRequest.getOpen(),
                innerRequest.getHigh(),
                innerRequest.getLow(),
                innerRequest.getClose(),
                innerRequest.getVolume()
        );
        return dto;
    }

}
