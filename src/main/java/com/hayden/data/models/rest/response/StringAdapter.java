package com.hayden.data.models.rest.response;

import com.hayden.data.models.rest.request.WebClientRequester;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.response.Adapter;
import com.hayden.data.models.shared.response.ExchangeResponse;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class StringAdapter<TO extends ExchangeResponse, FROM extends StringResponse<TO>> extends Adapter<TO, FROM> {

    public StringAdapter(Class<FROM> fromClzz, Class<TO> toClass, FROM fromObject, WebClientRequester builder)
    {
        super(fromClzz, toClass, fromObject, builder);
    }

    @Override
    public Publisher<TO> adapt(Url url)
    {
        return builder.retrieve(url)
                .single(String.class)
                .flatMapMany(str -> {
                    this.FROMObject.setData(str);
                    return this.FROMObject.responses();
                });
    }

}
