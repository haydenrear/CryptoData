package com.hayden.data.models.rest;

import com.hayden.data.models.rest.response.IntermediateResponse;
import com.hayden.data.models.rest.response.PageableResponse;
import com.hayden.data.models.rest.response.StringAdapter;
import com.hayden.data.models.rest.response.StringResponse;
import com.hayden.data.models.shared.response.AbstractAdapterFactory;
import com.hayden.data.models.shared.response.Adapter;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.models.shared.response.Requester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class RestAdapterFactory extends AbstractAdapterFactory implements ApplicationContextAware {

    ApplicationContext applicationContext;

    public <FROM,TO extends ExchangeResponse> Adapter<TO, FROM> createAdapter(Class<FROM> fromClass, Class<TO> toClass, Requester requester)
    {
        if(StringResponse.class.isAssignableFrom(fromClass))
            return applicationContext.getBean(StringAdapter.class, fromClass, toClass, applicationContext.getBean(fromClass), requester);
        else if(PageableResponse.class.isAssignableFrom(fromClass))
            return applicationContext.getBean(PageableAdapter.class, fromClass, toClass, requester, applicationContext);
        else if(IntermediateResponse.class.isAssignableFrom(fromClass))
            return applicationContext.getBean(IntermediateResponseAdapter.class, fromClass, toClass, requester);
        else if(ExchangeResponse.class.isAssignableFrom(fromClass))
            return applicationContext.getBean(ExchangeResponseAdapter.class, fromClass, toClass, requester);
        else throw new UnsupportedOperationException();
    }

    @Override @Autowired
    public void setApplicationContext(@NonNull ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

}
