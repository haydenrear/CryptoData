package com.hayden.data.models.shared.request;

import com.hayden.data.models.shared.response.ExchangeRequest;
import com.hayden.data.models.shared.response.Response;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Scope("prototype")
@Data
public class RequestBuilder<T extends ExchangeRequest, U extends Response, V extends AssetDTO<L>, L extends Asset> implements ApplicationContextAware {


    private Class<T> request;
    private Class<U> response;
    private Class<V> assetClzz;

    private ApplicationContext applicationContext;

    public RequestBuilder(
            Class<V> assetClzz,
            Class<U> response,
            Class<T> request
    )
    {
        this.assetClzz = assetClzz;
        this.response = response;
        this.request = request;
    }

    @Override @Autowired
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public T buildRequest(Object... args)
    {
        return applicationContext.getBean(request, args);
    }

    public Class<U> response()
    {
        return response;
    }

}
