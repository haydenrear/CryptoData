package com.hayden.data.models.socket.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.response.ExchangeRequest;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.models.shared.response.Requester;
import com.hayden.data.models.socket.BaseSocketAdapter;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicLong;

@Component("SocketRequestAdapter")
@Scope("prototype")
public class SocketRequestAdapter<TO extends SocketRequest & ExchangeResponse, FROM> extends BaseSocketAdapter<TO, FROM> {

    public SocketRequestAdapter(Class<FROM> fromClzz, Class<TO> toClass, Requester builder, ObjectMapper objectMapper)
    {
        super(fromClzz, toClass, builder, objectMapper);
    }

    @Override
    public Publisher<TO> adapt(Url url, ExchangeRequest request)
    {
        return adapt(super.adapt(url, request));
    }

    public Publisher<TO> adapt(Publisher<TO> responseData)
    {
        AtomicLong prevTime = new AtomicLong(-1L);
        return Flux.from(responseData)
                .cast(toClass)
                .filter(orderResp -> orderResp.nonNullData() && orderResp.withinWindow(prevTime));
    }

}
