package com.hayden.data.services.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.models.*;
import com.hayden.data.models.rest.To;
import com.hayden.data.models.rest.response.UrlParams;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.request.UrlBuilder;
import com.hayden.data.models.shared.request.UrlFactory;
import com.hayden.data.models.shared.response.AbstractAdapterFactory;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.models.shared.response.Requester;
import com.hayden.data.models.shared.response.Response;
import com.hayden.data.services.rest.APIDataService;
import com.hayden.data.services.rest.AssetDTOFactory;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.enums.DataType;
import com.hayden.decision.models.enums.TimeUnitEnum;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public abstract class ExternalDataService<T extends Asset> {

    public static final int DEFAULT_LIMIT = 10;
    public static final Date DEFAULT_START_TIME = Date.from(Instant.now().minus(45, ChronoUnit.DAYS));
    public static final Date DEFAULT_END_TIME = Date.from(Instant.now().minus(4, ChronoUnit.DAYS));

    @Value("${api.symbols:10000}")
    protected long NUM_SYMBOLS;

    protected AssetDTOFactory assetDTOService;
    protected Map<DataType, Class<? extends Annotation>> dataTypes;
    protected AbstractAdapterFactory abstractAdapterFactory;
    protected Class<? extends AssetDTO<T>> clzz;
    protected Exchanges exchanges;
    protected ObjectMapper objectMapper;
    protected Requester builder;
    protected UrlFactory urlFactory;

    public ExternalDataService(
            AbstractAdapterFactory abstractAdapterFactory,
            Exchanges exchanges,
            AssetDTOFactory assetDTOService,
            ObjectMapper objectMapper,
            Requester builder,
            UrlFactory urlFactory
    )
    {
        this.assetDTOService = assetDTOService;
        this.abstractAdapterFactory = abstractAdapterFactory;
        this.urlFactory = urlFactory;
        this.exchanges = exchanges;
        this.objectMapper = objectMapper;
        this.builder = builder;
        var annotation = this.getClass().getAnnotation(AssetClass.class);
        this.clzz = annotation != null
                ? (Class<? extends AssetDTO<T>>) annotation.value()
                : null;
        initMap();
    }

    public Flux<AssetDTO<T>> all(
            TimeUnitEnum timeUnitEnum,
            Date startMilli,
            Date endMilli,
            Integer numSymbols,
            DataType symbolType,
            String metric
    )
    {
        return getSymbols(symbolType)
                .filter(symbolFilters())
                .take(NUM_SYMBOLS)
                .parallel()
                .flatMap(symbol -> Mono.from(one(symbol, timeUnitEnum, startMilli, endMilli, numSymbols, metric))
                        .onErrorContinue((th, obj) -> System.out.println(obj))
                )
                .sequential();
    }

    public abstract Flux<String> getSymbols(DataType dataType);

    public abstract void initMap();

    public record ClzzTypes<T extends ExchangeResponse>(Class<?> from, Class<T> to) {}

    @SneakyThrows
    protected <TO extends ExchangeResponse> ClzzTypes<TO> clzzType(DataType dataType)
    {
        var fromClzz = (Class<?>) AnnotationUtils.getValue(this.getClass().getAnnotation(dataTypes.get(dataType)));

        if(fromClzz == null)
            throw new UnsupportedOperationException();

        Class<TO> toClzz;

        if(!ExchangeResponse.class.isAssignableFrom(fromClzz)) {
            To toClzzAnnotation = fromClzz.getAnnotation(To.class);
            if(toClzzAnnotation != null)
                toClzz = (Class<TO>) toClzzAnnotation.value();
            else
                toClzz = (Class<TO>) fromClzz;
        }
        else
            toClzz = (Class<TO>) fromClzz;

        return new ClzzTypes<>(fromClzz, toClzz);
    }

    public Predicate<String> symbolFilters()
    {
        return str -> true;
    }

    public List<DataType> dataTypes()
    {
        return dataTypes.entrySet()
                .stream()
                .filter(annotationEntry -> Arrays.stream(this.getClass().getAnnotations()).anyMatch(fAnnotation -> fAnnotation.annotationType() == annotationEntry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public abstract Predicate<DataType> dataTypeFilter();

    public Publisher<AssetDTO<T>> one(
            String symbol,
            TimeUnitEnum timeUnitEnum
    )
    {
        return one(
                symbol,
                timeUnitEnum,
                DEFAULT_START_TIME,
                DEFAULT_END_TIME,
                DEFAULT_LIMIT,
                ""
        );
    }

    public Publisher<AssetDTO<T>> one(
            String symbol,
            TimeUnitEnum timeUnitEnum,
            Date startMilli,
            Date endMilli,
            Integer limit,
            String metric
    )
    {
        var dto = assetDTOService.newAssetDTO(clzz);
        dto.setSymbol(symbol);
        return Flux.fromIterable(dataTypes())
                .filter(d -> dataTypeFilter().test(d))
                .flatMap(entry -> Flux.from(requestSingle(symbol, timeUnitEnum, entry, startMilli, endMilli, limit, metric))
                        .filter(Response::notEmpty)
                        .retryWhen(APIDataService.rateLimitingException())
                        .map(f -> f.visit(dto))
                );
    }

    public abstract Publisher<? extends ExchangeResponse> requestSingle(
            String symbol,
            TimeUnitEnum timeUnitEnum,
            DataType dataType,
            Date startMilli,
            Date endMilli,
            Integer limit,
            String metric
    );

    public Url createUrl(
            TimeUnitEnum timeUnit,
            DataType dataType,
            String symbol,
            Date startMilli,
            Date endMilli,
            Integer limit,
            String metric,
            String pagination,
            HttpHeaders headers
    )
    {

        Map<UrlFactory.Vals, String> valuesMap = new HashMap<>();
        valuesMap.put(UrlFactory.Vals.END_TIME, String.valueOf(endMilli.getTime()));
        valuesMap.put(UrlFactory.Vals.START_TIME, String.valueOf(startMilli.getTime()));
        valuesMap.put(UrlFactory.Vals.SYMBOL, symbol);
        valuesMap.put(UrlFactory.Vals.INTERVAL, exchanges.timeUnit(timeUnit, clzz));
        valuesMap.put(UrlFactory.Vals.METRIC, metric);
        valuesMap.put(UrlFactory.Vals.PAGINATION_TOKEN, pagination);

        return createUrl(
                valuesMap,
                headers,
                dataType
        );
    }


    private Url createUrl(
            Map<UrlFactory.Vals, String> valuesMap,
            HttpHeaders httpHeaders,
            DataType dataType
    )
    {
        var urlParams = clzzType(dataType).to.getAnnotation(UrlParams.class);
        var values = urlParams != null ? urlParams.value() : null;
        var urlBuilder = this.urlFactory.createBuilder(clzz, dataType, httpHeaders);
        urlBuilder.addHeaders(httpHeaders);
        valuesMap.entrySet()
                .stream()
                .filter(val -> urlParams == null || (val.getValue() != null && Arrays.asList(values).contains(val.getKey())))
                .forEach(val -> urlBuilder.addParams(new UrlBuilder.Param(val.getKey(), val.getValue())));
        return urlBuilder.build();
    }

}
