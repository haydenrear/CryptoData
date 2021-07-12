package com.hayden.data.services.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.decision.models.enums.DataType;
import com.hayden.data.models.Exchanges;
import com.hayden.data.models.shared.request.RequestBuilder;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.request.UrlFactory;
import com.hayden.data.models.shared.response.*;
import com.hayden.data.models.socket.SocketAdapterFactory;
import com.hayden.data.models.socket.request.SocketRequester;
import com.hayden.data.services.rest.APIDataService;
import com.hayden.data.services.rest.AssetDTOFactory;
import com.hayden.data.services.shared.ExternalDataService;
import com.hayden.data.services.shared.RequestObject;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.enums.TimeUnitEnum;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.*;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Predicate;

@NoArgsConstructor
public abstract class AbstractSocket<T extends Asset, REST_SERVICE extends APIDataService<ASSET>, ASSET extends Asset> extends ExternalDataService<T> {

    protected Exchanges exchanges;

    public static final Long WINDOW = 3L;
    public static final Duration DATA_GATHER_TIME = Duration.ofSeconds(3);

    @Value("${amount.dto:10000}")
    protected long NUM_RESPONSE;

    REST_SERVICE apiDataService;

    @Override
    public Flux<String> getSymbols(DataType dataType)
    {
        return apiDataService.getSymbols(dataType);
    }

    public AbstractSocket(
            Exchanges exchanges,
            AssetDTOFactory assetDTOService,
            ObjectMapper objectMapper,
            SocketRequester builder,
            UrlFactory urlFactory,
            REST_SERVICE apiDataService,
            SocketAdapterFactory socketAdapterFactory
    )
    {
        super(socketAdapterFactory, exchanges, assetDTOService, objectMapper, builder, urlFactory);
        this.apiDataService = apiDataService;
    }

    @Override
    public void initMap()
    {
        dataTypes = new HashMap<>();
        dataTypes.put(DataType.REQUEST_OBJECT, RequestObject.class);
        dataTypes.put(DataType.HISTORICAL_WSS, HistoricalWS.class);
        dataTypes.put(DataType.ORDER_BOOK_WSS, OrderBookWS.class);
    }

    @Override
    public Predicate<DataType> dataTypeFilter()
    {
        return dataType -> dataType.toString().contains("WSS")
                && dataTypes()
                .stream()
                .anyMatch(dataType::equals);
    }

    @Override
    public Mono<AssetDTO<T>> one(
            String symbol,
            TimeUnitEnum timeUnitEnum,
            Date startMilli,
            Date endMilli,
            Integer limit,
            String metric
    )
    {
        return Flux.from(super.one(
                symbol,
                timeUnitEnum,
                startMilli,
                endMilli,
                limit,
                metric
        )).take(NUM_RESPONSE).last();
    }

    @Override
    public Publisher<? extends ExchangeResponse> requestSingle(
            String symbol,
            TimeUnitEnum timeUnitEnum,
            DataType dataType,
            Date startMilli,
            Date endMilli,
            Integer limit,
            String metric
    )
    {
        var buildRequest = buildRequest(requestBuilder(dataType), dataType, symbol, timeUnitEnum, startMilli, endMilli, metric);
        System.out.println(buildRequest);
        var url = createUrl(timeUnitEnum, dataType, symbol, startMilli, endMilli, limit, "", "", apiDataService.getHeaders());
        var responseType = clzzType(dataType);
        var adapter = this.abstractAdapterFactory.createAdapter(responseType.from(), responseType.to(), builder);
        return adapter.adapt(url, buildRequest);
    }

    public abstract Url createUrl(TimeUnitEnum timeUnitEnum, DataType dataType, String symbol, Date startMilli, Date endMilli, Integer limit, String metric, String pagination, HttpHeaders headers);
    protected abstract ExchangeRequest buildRequest(RequestBuilder<?, ?, ? extends AssetDTO<?>, ?> requestBuilder, DataType dataType, String symbol, TimeUnitEnum timeUnitEnum, Date startMilli, Date endMilli, String metric);

    public <ER extends ExchangeRequest, RE extends ExchangeResponse, V extends AssetDTO<L>, L extends Asset>RequestBuilder<ER, RE, V, L> requestBuilder(
            DataType dataType
    )
    {
        Class<V> clzz = (Class<V>) this.clzz;
        var responseClzz = (Class<RE>) clzzType(dataType).from();
        var requestClzz = (Class<ER>) clzzType(DataType.REQUEST_OBJECT).from();
        return assetDTOService.newRequestBuilder(clzz, responseClzz, requestClzz);
    }

}
