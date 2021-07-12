package com.hayden.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.models.Exchanges;
import com.hayden.data.models.socket.response.BinancePriceData;
import com.hayden.data.models.socket.response.BinanceTickerResponse;
import com.hayden.data.services.rest.*;
import com.hayden.decision.models.enums.ExchangeEnum;
import com.hayden.decision.models.shared.GetPrices;
import com.hayden.decision.models.RSocketClientImpl;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.markets.OrderBook;
import com.hayden.decision.models.sectors.Sector;
import com.hayden.decision.models.sectors.model.*;
import com.hayden.decision.models.shared.*;
import com.hayden.decision.util.DecisionConstants;
import com.hayden.dynamicparse.parse.DynamicParseJson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;


@Configuration
public class DataConfig implements ApplicationContextAware {

    private ApplicationContext ctx;

    @Bean
    RSocketClientImpl rSocketClient(RSocketRequester.Builder rBuilder,
                                    @Qualifier("rSocketStrategies") RSocketStrategies rsocketStrategies
    )
    {
        return new RSocketClientImpl(rBuilder, rsocketStrategies);
    }

    @Bean
    DynamicParseJson dynamicParseJson(ObjectMapper om){
        return new DynamicParseJson(om);
    }

    @Bean
    @Scope("prototype")
    GetPrices getPrices(
            String symbol,
            ConcurrentSkipListMap<Date, Bar> prices,
            Map<ExchangeEnum, Map<Date, OrderBook>> orderBook,
            Map<String, ConcurrentSkipListMap<Date, Double>> numeric,
            Map<String, ConcurrentSkipListMap<Date, String>> text
    )
    {
        return new GetPrices(symbol, prices, orderBook, numeric, text);
    }

    @Bean
    ObjectMapper objectMapper(){
        var om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return om;
    }

    @Bean
    @Scope("prototype")
    OrderBook orderBook(Long date, List<List<Double>> asks, List<List<Double>> bids, String symbol){
        return new OrderBook(date, asks, bids, symbol);
    }

    @Bean
    @Scope("prototype")
    Bar bar(Date d, double o, double h, double l, double c, double v)
    {
        return new Bar(d, o, h, l, c, v);
    }

    @Bean(value = "assetDTO")
    @Scope("prototype")
    <K extends Asset> AssetDTO<K> assetDTO(Class<K> clzz, String symbol) {
        return new AssetDTO<K>(clzz,symbol);
    }

    @Bean
    @Scope("prototype")
    EconDataWSDTO econDataWSDTO() {
        return new EconDataWSDTO();
    }

    @Bean
    @Scope("prototype")
    DataMetrics dataMetrics() {
        return new DataMetrics();
    }

    @Bean
    DecisionConstants decisionConstants() {
        return new DecisionConstants();
    }

    @Bean
    @Scope("prototype")
    GetCorrelateReq getCorrelateWSDTO() {
        return new GetCorrelateReq();
    }

    @Bean
    @Scope("prototype")
    Correlation getCorrelate() {
        return new Correlation();
    }

    @Bean
    Sector sector(){
        return new Sector();
    }

    @Bean
    List<GetCorrelate> getCorrelates() {
        Sector sector = sector();
        List<GetCorrelate> correlates = new ArrayList<>();
        for (Field f : sector.getClass().getFields()) {
            try {
                GetCorrelate correlate = (GetCorrelate) f.get(sector);
                correlates.add(correlate);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return correlates;
    }

    @Override
    @Autowired
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException
    {
        this.ctx = applicationContext;
    }
}
