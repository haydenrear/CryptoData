package com.hayden.data.models.socket.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.models.socket.request.SocketRequest;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.hayden.data.services.socket.AbstractSocket.WINDOW;

@Data
@Component
@Scope("prototype")
@NoArgsConstructor
public class BinanceOrderStream implements Serializable, ExchangeResponse, SocketRequest {

    public List<List<Double>> a;
    public List<List<Double>> b;
    public String s;
    public Long T;
    @JsonProperty("e")
    public String eventType;
    @JsonProperty("E")
    public Long eventTime;
    public Long U;
    public Long u;
    public Long pu;
    public int id;
    public String result;

    @Override
    public <T extends Asset> AssetDTO<T> visit(AssetDTO<T> dto)
    {
        dto.addBidAsks(T, a, b, s);
        return dto;
    }

    @Override
    public boolean withinWindow(AtomicLong prevTime)
    {
        return getTime() - prevTime.get() >= WINDOW || prevTime.get() == -1L;
    }

    public boolean nonNullData()
    {
        return s != null && T != null;
    }

    @Override
    public Long getTime()
    {
        return this.T;
    }
}