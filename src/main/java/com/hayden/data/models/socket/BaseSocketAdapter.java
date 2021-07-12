package com.hayden.data.models.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.response.Adapter;
import com.hayden.data.models.shared.response.ExchangeRequest;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.models.shared.response.Requester;
import org.reactivestreams.Publisher;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

public abstract class BaseSocketAdapter<TO extends ExchangeResponse, FROM> extends Adapter<TO, FROM> {

    ObjectMapper objectMapper;

    public BaseSocketAdapter(Class<FROM> fromClzz, Class<TO> toClass, Requester builder, ObjectMapper objectMapper)
    {
        super(fromClzz, toClass, builder);
        this.objectMapper = objectMapper;
    }

    @Override
    public Publisher<TO> adapt(Url url, ExchangeRequest request)
    {
        return builder.retrieve(url, request)
                .multiple(toClass);
    }

    @Override
    public Publisher<TO> adapt(Url url)
    {
        throw new UnsupportedOperationException("Must have initial message to set up websocket connection");
    }
}
