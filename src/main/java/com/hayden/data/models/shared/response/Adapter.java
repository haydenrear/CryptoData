package com.hayden.data.models.shared.response;

import com.hayden.data.models.shared.request.Url;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.context.ApplicationContext;

@NoArgsConstructor
public abstract class Adapter<TO extends ExchangeResponse, FROM> {

    protected Class<FROM> fromClzz;
    protected Class<TO> toClass;

    protected FROM FROMObject;
    protected TO TOObject;

    protected Requester builder;
    protected ApplicationContext ctx;

    public Adapter(Class<FROM> fromClzz, Class<TO> toClass, Requester builder)
    {
        this.toClass = toClass;
        this.fromClzz = fromClzz;
        this.builder = builder;
    }

    public Adapter(Class<FROM> fromClzz, Class<TO> toClass, Requester builder, ApplicationContext ctx)
    {
        this.toClass = toClass;
        this.fromClzz = fromClzz;
        this.builder = builder;
        this.ctx = ctx;
    }

    public Adapter(Class<FROM> fromClzz, Class<TO> toClass, FROM FROMObject, Requester builder)
    {
        this.toClass = toClass;
        this.fromClzz = fromClzz;
        this.FROMObject = FROMObject;
        this.builder = builder;
    }

    public Adapter(Class<FROM> fromClzz, Class<TO> toClass, TO TOObject, FROM FROMObject, Requester builder)
    {
        this.toClass = toClass;
        this.fromClzz = fromClzz;
        this.FROMObject = FROMObject;
        this.TOObject = TOObject;
        this.builder = builder;
    }

    public Publisher<TO> adapt(Url url, ExchangeRequest request)
    {
        return adapt(url);
    }

    public abstract Publisher<TO> adapt(Url url);

}
