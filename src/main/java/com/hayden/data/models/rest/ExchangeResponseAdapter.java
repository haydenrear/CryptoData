package com.hayden.data.models.rest;

import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.rest.request.WebClientRequester;
import com.hayden.data.models.shared.response.Adapter;
import com.hayden.data.models.shared.response.ExchangeResponse;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ExchangeResponseAdapter<TO extends ExchangeResponse, FROM extends ExchangeResponse> extends Adapter<TO,FROM> {

    public ExchangeResponseAdapter(Class<TO> toClass, Class<FROM> fromClzz, WebClientRequester builder)
    {
        super(fromClzz, toClass, builder);
    }

    @Override
    public Publisher<TO> adapt(Url url)
    {
        return builder.retrieve(url)
                .multiple(toClass);
    }
}
