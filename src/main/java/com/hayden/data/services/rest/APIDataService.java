package com.hayden.data.services.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.models.*;
import com.hayden.data.models.rest.request.WebClientRequester;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.request.UrlFactory;
import com.hayden.data.models.shared.response.Adapter;
import com.hayden.data.models.rest.RestAdapterFactory;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.models.rest.response.SymbolResponse;
import com.hayden.data.services.shared.*;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.enums.DataType;
import com.hayden.decision.models.enums.ExchangeEnum;
import com.hayden.decision.models.shared.AssetDTO;
import com.hayden.decision.models.enums.TimeUnitEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
public abstract class APIDataService<T extends Asset> extends ExternalDataService<T> {

    private static final Integer MAX_PAGE = 10;
    private HttpHeaders headers;

    public APIDataService(
            Exchanges exchanges,
            RestAdapterFactory restAdapterFactory,
            AssetDTOFactory assetDTOService,
            ObjectMapper objectMapper,
            WebClientRequester builder,
            UrlFactory urlFactory
    )
    {
        super(restAdapterFactory, exchanges, assetDTOService, objectMapper, builder, urlFactory);
    }

    @Override
    public void initMap()
    {
        dataTypes = new HashMap<>();
        dataTypes.put(DataType.HISTORICAL_MARKET, Historical.class);
        dataTypes.put(DataType.ASSET_METADATA, Symbol.class);
        dataTypes.put(DataType.TRENDING, Trending.class);
        dataTypes.put(DataType.ORDER_BOOK_REST, OrderBook.class);
    }

    public Predicate<DataType> dataTypeFilter(){
        return dataType -> !dataType.toString().contains("WSS") && !dataType.equals(DataType.TRENDING) && !dataType.equals(DataType.ASSET_METADATA);
    }

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
        var url = createUrl(
                timeUnitEnum,
                dataType,
                symbol,
                startMilli,
                endMilli,
                limit,
                metric,
                "",
                getHeaders()
        );

        return Flux.from(createAdapter(dataType).adapt(url))
                .cast(ExchangeResponse.class);

    }

    public Adapter<? extends ExchangeResponse, ?> createAdapter(DataType dataType)
    {
        ClzzTypes<? extends ExchangeResponse> clzzTypes = clzzType(dataType);
        return abstractAdapterFactory.createAdapter(clzzTypes.from(), clzzTypes.to(), builder);
    }

    public Flux<AssetDTO<T>> all(
            TimeUnitEnum timeUnitEnum,
            DataType symbolType
    )
    {
        return this.all(
                timeUnitEnum,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                DEFAULT_LIMIT,
                symbolType,
                ""
        );
    }

    @Override
    public Publisher<AssetDTO<T>> one (
            String symbol,
            TimeUnitEnum timeUnitEnum,
            Date startMilli,
            Date endMilli,
            Integer limit,
            String metric
    )
    {
        return super.one(
                symbol,
                timeUnitEnum,
                startMilli,
                endMilli,
                limit,
                metric
        );
    }

    public static RetryBackoffSpec rateLimitingException()
    {
        return Retry.fixedDelay(3, Duration.ofSeconds(61))
                .filter(f -> f instanceof WebClientResponseException wcre && wcre.getStatusCode().is4xxClientError());
    }

    public Flux<String> getSymbols(DataType dataType)
    {
        return Flux.from(createAdapter(dataType).adapt(symbolsUrl(dataType)))
                .cast(SymbolResponse.class)
                .map(SymbolResponse::getSymbol);
    }

    private ExchangeEnum exchange()
    {
        return this.getClass().getAnnotation(Exchange.class).value();
    }

    public Url pageableUrl(
            String nextVal,
            Url prevUrl
    )
    {
        if(nextVal != null && !nextVal.equals(""))
            return prevUrl.updatePage(nextVal);
        return prevUrl;
    }

    public Url symbolsUrl(DataType dataType)
    {
        if (!dataType.equals(DataType.ASSET_METADATA) && !dataType.equals(DataType.TRENDING)) {
            throw new UnsupportedOperationException("Cannot get symbols from incorrect data type");
        }
        String initialPage = urlFactory.paramKey(UrlFactory.Vals.PAGINATION_TOKEN, dataType, exchange());
        return pageableUrl(
                initialPage,
                urlFactory.symbolsUrl(
                        dataType,
                        clzz,
                        getHeaders()
                )
        );
    }

    public HttpHeaders getHeaders()
    {
        if(this.headers == null){
            var restVals = UrlFactory.REST_VALS_MAP.get(exchange());
            var api = restVals.keySet()
                    .stream()
                    .filter(s -> s.equals(UrlFactory.Vals.API_ID) || s.equals(UrlFactory.Vals.API_KEY))
                    .collect(Collectors.toList())
                    .toArray(UrlFactory.Vals[]::new);
            if(api.length == 0) {
                this.headers = new HttpHeaders();
                return this.headers;
            }
            else {
                this.headers = exchanges.httpHeaders(exchange(), api);
            }
        }
        return this.headers;
    }

}
