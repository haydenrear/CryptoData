package com.hayden.data.models.shared.request;

import com.hayden.decision.models.enums.DataType;
import com.hayden.decision.models.enums.ExchangeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("prototype")
@Data
@NoArgsConstructor
public class UrlBuilder implements ApplicationContextAware {

    private DataType dataType;
    ExchangeEnum exchange;

    String baseUrl;

    Map<String,String> queryParams = new HashMap<>();
    Map<UrlFactory.Vals, String> pathParams = new HashMap<>();

    HttpHeaders httpHeaders;

    ApplicationContext applicationContext;

    public UrlBuilder(
            Map<String, String> constantParams,
            ExchangeEnum exchange,
            String baseUrl,
            HttpHeaders headers,
            DataType dt
    )
    {
        this.exchange = exchange;
        if(constantParams != null)
            this.queryParams.putAll(constantParams);
        this.baseUrl = baseUrl;
        this.httpHeaders = headers;
        this.dataType = dt;
    }

    public UrlBuilder(
            Map<String, String> constantParams,
            ExchangeEnum exchange,
            String baseUrl,
            HttpHeaders headers
    )
    {
        this.exchange = exchange;
        if(constantParams != null)
            this.queryParams.putAll(constantParams);
        this.baseUrl = baseUrl;
        this.httpHeaders = headers;
    }

    @Override
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    public void addHeaders(HttpHeaders httpHeaders)
    {
        this.httpHeaders = httpHeaders;
    }

    public record Param(UrlFactory.Vals val, String value){}

    public UrlBuilder addParams(Param ... values){
        Arrays.stream(values).forEach(entry -> {
            var paramValue = entry.value();
            if(baseUrl.contains(entry.val.toString()))
                pathParams.put(entry.val, entry.value());
            else if(paramValue != null) {
                var restVal = UrlFactory.REST_VALS_MAP.get(exchange).get(entry.val);
                if(restVal != null)
                queryParams.put(
                        restVal,
                        paramValue
                );
            }
        });
        return this;
    }

    public Url build(){
        return applicationContext.getBean(
                Url.class,
                queryParams,
                pathParams,
                baseUrl,
                exchange,
                httpHeaders,
                dataType
        );
    }
}
