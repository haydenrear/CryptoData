package com.hayden.data.models.socket.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.response.ExchangeRequest;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.models.shared.response.Requester;
import com.hayden.data.services.socket.AbstractSocket;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


@Component("BufferedSocketRequesterAdapter")
@Scope("prototype")
public class BufferedSocketRequesterAdapter<TO extends SocketRequest & ExchangeResponse, FROM> extends SocketRequestAdapter<TO, FROM> {

    public BufferedSocketRequesterAdapter(Class<FROM> fromClzz, Class<TO> toClass, Requester builder, ObjectMapper objectMapper)
    {
        super(fromClzz, toClass, builder, objectMapper);
    }

    @Override
    public Publisher<TO> adapt(Publisher<TO> responseData)
    {
        return Flux.from(super.adapt(responseData))
                .buffer(AbstractSocket.DATA_GATHER_TIME)
                .flatMap(Flux::fromIterable);
    }

}
