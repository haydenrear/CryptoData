package com.hayden.data.models.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.models.shared.response.AbstractAdapterFactory;
import com.hayden.data.models.shared.response.Adapter;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.models.shared.response.Requester;
import com.hayden.data.models.socket.request.SocketRequest;
import com.hayden.data.models.socket.request.SocketRequestAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class SocketAdapterFactory extends AbstractAdapterFactory {

    ObjectMapper objectMapper;

    public SocketAdapterFactory(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    @Override
    public <FROM, TO extends ExchangeResponse> Adapter<TO, FROM> createAdapter(Class<FROM> fromClass, Class<TO> toClass, Requester requester)
    {
        if(SocketRequest.class.isAssignableFrom(fromClass)){
            return (Adapter<TO, FROM>) applicationContext.getBean("SocketRequestAdapter", fromClass, toClass, requester, objectMapper);
        }
        return null;
    }

    @Override
    @Autowired
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}
