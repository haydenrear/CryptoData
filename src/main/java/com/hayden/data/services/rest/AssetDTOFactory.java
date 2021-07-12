package com.hayden.data.services.rest;

import com.hayden.data.models.*;
import com.hayden.data.models.dto.AssetMessage;
import com.hayden.data.models.rest.response.Bars;
import com.hayden.data.models.shared.request.RequestBuilder;
import com.hayden.data.models.shared.response.ExchangeRequest;
import com.hayden.data.models.shared.response.Response;
import com.hayden.data.models.rest.response.StringResponse;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.enums.ExchangeEnum;
import com.hayden.decision.models.markets.OrderBook;
import com.hayden.decision.models.sectors.model.Correlation;
import com.hayden.decision.models.sectors.model.GetCorrelate;
import com.hayden.decision.models.shared.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class AssetDTOFactory implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Override
    @Autowired
    public void setApplicationContext(@NonNull ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    public GetCorrelateReq newGetCorrelateReq(GetCorrelate getCorrelate)
    {
        Correlation correlation = newGetCorrelation(getCorrelate);
        correlation.getGetCorrelate();
        return new GetCorrelateReq(correlation);
    }

    public <T extends Asset, K extends AssetDTO<T>> K newAssetDTO(
            ConcurrentSkipListMap<Date, Bar> prices,
            String symbol,
            Class<K> clzz
    )
    {
        var asset = applicationContext.getBean(clzz, symbol);
        asset.setPrices(prices);
        return asset;
    }

    public <T extends Asset> AssetMessage newAssetMessage(GetPrices assetDTO)
    {
        return this.applicationContext.getBean(AssetMessage.class, assetDTO);
    }

    public EconDataWSDTO getEconDataWSDTO(
            EconDataWSDTO.MetricsRecord parseData,
            Correlation correlation
    )
    {
        EconDataWSDTO econData = newEconDataWSDTO();
        econData.setDataMetrics(parseData);
        econData.setGetCorrelate(correlation);
        return econData;
    }

    public Correlation newGetCorrelation(GetCorrelate correlate)
    {
        Correlation correlation = applicationContext.getBean(Correlation.class);
        correlation.setGetCorrelate(correlate);
        correlation.setCorrelateType(correlate.getClass());
        return correlation;
    }


    public EconDataWSDTO newEconDataWSDTO()
    {
        return applicationContext.getBean(EconDataWSDTO.class);
    }

    public DataMetrics newDataMetrics()
    {
        return applicationContext.getBean(DataMetrics.class);
    }


    public GetPrices newPrices(
            ConcurrentSkipListMap<Date, Bar> prices,
            Map<ExchangeEnum, Map<String, Map<Long, OrderBook>>> orderBook,
            String key,
            Map<String, ConcurrentSkipListMap<Date, Double>> num,
            Map<String, ConcurrentSkipListMap<Date, String>> txt
    )
    {
        return applicationContext.getBean(GetPrices.class, key, prices, orderBook, num, txt);
    }

    public <T extends AssetDTO<? extends Asset>> Flux<GetPrices> buildTransfer(Map<String, Collection<T>> toTransform)
    {
        return Flux.fromIterable(toTransform.entrySet())
                .map(this::construct);
    }

    private <T extends AssetDTO<? extends Asset>> GetPrices construct(Map.Entry<String, Collection<T>> assets)
    {
        var prices = prices(assets.getValue());
        var orders = orders(assets.getValue());
        var numeric = getMap(assets, AssetDTO::getNumeric);
        var text = getMap(assets, AssetDTO::getText);
        return newPrices(prices, orders, assets.getKey(), numeric, text);
    }

    private <T extends AssetDTO<? extends Asset>, U> Map<String, ConcurrentSkipListMap<Date, U>> getMap(Map.Entry<String, Collection<T>> assets, Function<T, Map<String, ConcurrentSkipListMap<Date, U>>> getText)
    {
        return collectMaps(assets.getValue()
                .stream()
                .map(getText)
                .collect(Collectors.toList()));
    }

    private <T extends AssetDTO<? extends Asset>> ConcurrentSkipListMap<Date, Bar> prices(Collection<T> assets)
    {
        return assets.stream()
                .filter(assetFound -> assetFound.getPrices() != null)
                .flatMap(assetFound -> assetFound.getPrices().entrySet().stream())
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue, (var1, var2) -> var1, ConcurrentSkipListMap::new));
    }

    public <U> Map<String, ConcurrentSkipListMap<Date, U>> collectMaps(List<Map<String, ConcurrentSkipListMap<Date, U>>> values)
    {
        return values.stream()
                .reduce(new HashMap<>(), (a, b) -> {
                    if(b != null) {
                        b.forEach((key, val) -> {
                            a.compute(key, (mapKey, mapVal) -> {
                                if (mapVal == null) {
                                    mapVal = new ConcurrentSkipListMap<>();
                                }
                                mapVal.putAll(val);
                                return mapVal;
                            });
                        });
                    }
                    return a;
                });
    }

    private <T extends AssetDTO<? extends Asset>> Map<ExchangeEnum, Map<String, Map<Long, OrderBook>>> orders(Collection<T> asset)
    {
        return asset.stream().collect(Collectors.toMap(assetFound -> Exchanges.EXCHANGE.get(assetFound.getClass()), AssetDTO::getBidAsks));
    }

    public <U extends AssetDTO<T>, T extends Asset> U newAssetDTO(Class<U> clzz)
    {
        return applicationContext.getBean(clzz);
    }

    public Bars newBars(ConcurrentSkipListMap<Date, Bar> priceInstances)
    {
        return applicationContext.getBean(Bars.class, priceInstances);
    }

    public Bar newBar(Date time, Double open, Double high, Double low, Double close, Double volume)
    {
        return applicationContext.getBean(Bar.class, time, open, high, low, close, volume);
    }

    public <V extends AssetDTO<L>, T extends ExchangeRequest, L extends Asset, U extends Response> RequestBuilder<T,U,V,L> newRequestBuilder(
            Class<V> clzz,
            Class<U> clzzType,
            Class<T> request
    )
    {
        return applicationContext.getBean(RequestBuilder.class, clzz, clzzType, request);
    }

    public <T extends StringResponse> T newStringResponse(Class<T> stringResponseType, String data)
    {
        if(StringResponse.class.isAssignableFrom(stringResponseType))
            return applicationContext.getBean(stringResponseType, data);
        throw new UnsupportedOperationException();
    }
}
