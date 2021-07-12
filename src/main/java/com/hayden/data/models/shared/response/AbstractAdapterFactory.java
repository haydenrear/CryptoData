package com.hayden.data.models.shared.response;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractAdapterFactory implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    public abstract <FROM,TO extends ExchangeResponse> Adapter<TO, FROM> createAdapter(Class<FROM> fromClass, Class<TO> toClass, Requester requester);

}