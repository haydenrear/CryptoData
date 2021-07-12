package com.hayden.data.models.socket.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.response.AbstractSpec;
import com.hayden.data.models.shared.response.ExchangeRequest;
import com.hayden.data.models.shared.response.Requester;
import com.hayden.data.models.socket.SocketSpec;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class SocketRequester implements ApplicationContextAware, Requester {

    private final ObjectMapper objectMapper;
    private ApplicationContext ctx;

    public SocketRequester(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    @Override
    public AbstractSpec retrieve(Url url, ExchangeRequest request)
    {
        return ctx.getBean(SocketSpec.class, request, url, objectMapper);
    }

    @Override
    public AbstractSpec retrieve(Url url)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException
    {
        this.ctx = applicationContext;
    }
}
