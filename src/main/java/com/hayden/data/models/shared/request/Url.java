package com.hayden.data.models.shared.request;

import com.hayden.decision.models.enums.DataType;
import com.hayden.decision.models.enums.ExchangeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.function.Predicate;

@Component
@Scope("prototype")
@Data
@NoArgsConstructor
@Log4j2
public class Url {

    private DataType dataType;
    private HttpHeaders headers;
    private ExchangeEnum exchange;
    volatile Map<String,String> queryParams;
    Map<UrlFactory.Vals, String> pathParams;
    String base;

    public Predicate<String> isDate(){
        var endTimeKey = UrlFactory.REST_VALS_MAP.get(exchange).get(UrlFactory.Vals.END_TIME);
        var startTimeKey = UrlFactory.REST_VALS_MAP.get(exchange).get(UrlFactory.Vals.START_TIME);
        return key -> key.equals(endTimeKey) || key.equals(startTimeKey);
    }

    public Url(
            Map<String, String> queryParams,
            Map<UrlFactory.Vals, String> pathParams,
            String base,
            ExchangeEnum exchange,
            HttpHeaders headers,
            DataType dataType
    )
    {
        this.dataType = dataType;
        this.queryParams = queryParams;
        this.pathParams = pathParams;
        this.base = base;
        this.exchange = exchange;
        this.headers = headers;
    }

    public Url(
            Map<String, String> queryParams,
            Map<UrlFactory.Vals, String> pathParams,
            String base,
            ExchangeEnum exchange,
            HttpHeaders headers
    )
    {
        this.queryParams = queryParams;
        this.pathParams = pathParams;
        this.base = base;
        this.exchange = exchange;
        this.headers = headers;
    }

    public synchronized Url updatePage(String newVal){
        queryParams.replace(UrlFactory.REST_VALS_MAP.get(exchange).get(UrlFactory.Vals.PAGINATION_TOKEN), newVal);
        return this;
    }

    public String build(){
        var toReturn = this.base;
        toReturn = queryParams(toReturn);
        toReturn = pathParams(toReturn);
        log.info("Returning url {}", toReturn);
        return toReturn;
    }

    public String queryParams(String toReturn){
        return queryParams.size() != 0 ? queryParams
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().length() != 0)
                .map(queryAndVal -> {
                    var dateTimeFormatter = UrlFactory.EXCHANGE_DATES.get(exchange);
                    if(isDate().test(queryAndVal.getKey()) && dateTimeFormatter != null)
                    {
                        return Map.entry(queryAndVal.getKey(), dateTimeFormatter.format(Instant.ofEpochMilli(Long.parseLong(queryAndVal.getValue()))));
                    }
                    return queryAndVal;
                })
                .reduce(base+"?", (prev, next) -> prev.equals(base + "?") ? prev + next : (prev += "&" + next), String::concat) : toReturn;
    }

    public String pathParams(String toReturn){
        for (Map.Entry<UrlFactory.Vals, String> entry : pathParams.entrySet()) {
            UrlFactory.Vals key = entry.getKey();
            String value = entry.getValue();
            toReturn = toReturn.replace(key.toString(), value);
        }
        return toReturn;
    }
}
