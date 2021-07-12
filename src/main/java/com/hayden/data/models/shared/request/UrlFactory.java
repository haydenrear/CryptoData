package com.hayden.data.models.shared.request;

import com.hayden.decision.models.enums.DataType;
import com.hayden.data.models.Exchanges;
import com.hayden.data.services.utility.ExchangeProps;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.enums.ExchangeEnum;
import com.hayden.decision.models.enums.TimeUnitEnum;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Data
public class UrlFactory implements ApplicationContextAware {

    ExchangeProps exchangeProps;
    ApplicationContext ctx;

    public String paramKey(
            Vals value,
            DataType dataType,
            ExchangeEnum exchangeEnum
    ){
        var tokenFound = EXCHANGE_TO_PARAMS.get(exchangeEnum).get(dataType);
        Map<Vals, String> exchangeQueryParams = REST_VALS_MAP.get(exchangeEnum);
        if(exchangeQueryParams != null) {
            String queryParamValue = exchangeQueryParams.get(value);
            if(queryParamValue != null && tokenFound != null)
                return tokenFound.get(queryParamValue);
        }
        return "";
    }

    public enum Vals{
        SYMBOL,INTERVAL,START_TIME,END_TIME,NUM_RETURNED,PAGINATION_TOKEN,BASE_CURRENCY,PRICE_TYPES,API_KEY,API_ID,METRIC;
    }

    public UrlFactory(ExchangeProps exchangeProps)
    {
        this.exchangeProps = exchangeProps;
        initMap();
        initParams();
    }

    public static Optional<String> TimeUnit(
            ExchangeEnum exchange,
            TimeUnitEnum timeUnitEnum
    )
    {
        var conversion = TIME_UNIT_CONVERSION.get(exchange);
        if(conversion != null)
            return Optional.of(conversion.get(timeUnitEnum));
        return Optional.empty();
    }

    @Override @Autowired
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException
    {
        this.ctx = applicationContext;
    }

    public <T extends Asset> Url symbolsUrl(
            DataType dataType,
            Class<? extends AssetDTO<T>> clzz,
            HttpHeaders headers
    )
    {
        return createBuilder(clzz, dataType, headers).build();
    }

    public UrlBuilder createBuilder(
            Class<? extends AssetDTO<? extends Asset>> assetClzz,
            DataType dataType,
            HttpHeaders headers
    )
    {
        ExchangeEnum exchange = Exchanges.EXCHANGE.get(assetClzz);
        return ctx.getBean(
                UrlBuilder.class,
                EXCHANGE_TO_PARAMS.get(exchange).get(dataType),
                exchange,
                EXCHANGE_TO_BASE.get(exchange).get(dataType),
                headers,
                dataType
        );
    }

    public static final Map<ExchangeEnum, DateTimeFormatter> EXCHANGE_DATES = Map.of(
            ExchangeEnum.ALPACA, DateTimeFormatter.ISO_INSTANT
    );

    public static final Map<ExchangeEnum, Map<TimeUnitEnum, String>> TIME_UNIT_CONVERSION = Map.of(
            ExchangeEnum.BINANCE, Map.of(
                    TimeUnitEnum.SECOND, "1m",
                    TimeUnitEnum.ONEMINUTE, "1m",
                    TimeUnitEnum.ONEHOUR, "1h",
                    TimeUnitEnum.DAY, "1d"
            ),
            ExchangeEnum.COINGECKO, Map.of(
                    TimeUnitEnum.DAY, "daily",
                    TimeUnitEnum.SECOND, "max",
                    TimeUnitEnum.ONEMINUTE, "max"
            ),
            ExchangeEnum.MESSARI, Map.of(
                    TimeUnitEnum.SECOND, "1s",
                    TimeUnitEnum.FIVEMINUTE, "5m",
                    TimeUnitEnum.FIFTEENMINUTE, "15m",
                    TimeUnitEnum.ONEHOUR, "1h",
                    TimeUnitEnum.DAY, "1d",
                    TimeUnitEnum.WEEK, "1w"
            )
    );

    public static final Map<ExchangeEnum, Map<Vals, String>> REST_VALS_MAP = Map.of(
            ExchangeEnum.BINANCE, Map.of(
                    Vals.INTERVAL, "interval",
                    Vals.SYMBOL, "symbol",
                    Vals.START_TIME, "startTime",
                    Vals.END_TIME, "endTime",
                    Vals.NUM_RETURNED, "limit",
                    Vals.PAGINATION_TOKEN, ""
            ),
            ExchangeEnum.COINGECKO, Map.of(
                    Vals.INTERVAL, "interval",
                    Vals.BASE_CURRENCY, "vs_currency",
                    Vals.NUM_RETURNED, "days"
            ),
            ExchangeEnum.MESSARI, Map.of(
                    Vals.INTERVAL, "interval",
                    Vals.START_TIME, "start",
                    Vals.END_TIME, "end",
                    Vals.NUM_RETURNED, "limit",
                    Vals.API_KEY, "x-messari-api-key",
                    Vals.PAGINATION_TOKEN, "page"
            )
    );

    public Map<ExchangeEnum, Map<DataType, Map<String,String>>> EXCHANGE_TO_PARAMS;

    public void initParams()
    {
        EXCHANGE_TO_PARAMS = new HashMap<>();
        EXCHANGE_TO_PARAMS.put(ExchangeEnum.BINANCE, Map.of(
        ));
        EXCHANGE_TO_PARAMS.put(ExchangeEnum.COINGECKO, Map.of(
                DataType.HISTORICAL_MARKET, Map.of("vs_currency", "usd")
        ));
        EXCHANGE_TO_PARAMS.put(ExchangeEnum.MESSARI, Map.of(
                DataType.ASSET_METADATA, Map.of("page", "1")
        ));
    }

    public Map<ExchangeEnum, Map<DataType, String>> EXCHANGE_TO_BASE;

    public void initMap()
    {
        EXCHANGE_TO_BASE = new HashMap<>();
        EXCHANGE_TO_BASE.put(ExchangeEnum.BINANCE, Map.of(
                DataType.HISTORICAL_MARKET, exchangeProps.getBinanceRestUrl() + "/klines",
                DataType.ORDER_BOOK_REST, exchangeProps.getBinanceRestUrl() + "/depth",
                DataType.ORDER_BOOK_WSS, exchangeProps.getBinanceSocketUrl(),
                DataType.ASSET_METADATA, exchangeProps.getBinanceRestUrl() + "/exchangeInfo",
                DataType.HISTORICAL_WSS, exchangeProps.getBinanceSocketUrl()
        ));
        EXCHANGE_TO_BASE.put(ExchangeEnum.COINGECKO, Map.of(
                DataType.HISTORICAL_MARKET, exchangeProps.getCoinGeckoUrl() + "/coins/SYMBOL/market_chart",
                DataType.TRENDING, exchangeProps.getCoinGeckoUrl() + "/search/trending",
                DataType.ASSET_METADATA, exchangeProps.getCoinGeckoUrl() + "/coins/list?include_platform=false"
        ));
        EXCHANGE_TO_BASE.put(ExchangeEnum.MESSARI, Map.of(
                DataType.HISTORICAL_MARKET, exchangeProps.getMessariUrl() + "/markets/SYMBOL/metrics/price/time-series",
                DataType.ASSET_METADATA, exchangeProps.getMessariUrl() + "/markets",
                DataType.METRICS, exchangeProps.getMessariUrl() + "/markets/SYMBOL/metrics/METRIC/time-series"
        ));
    }

}
