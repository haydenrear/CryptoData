package com.hayden.data.models;

import com.hayden.data.models.dto.BinanceDTO;
import com.hayden.data.models.dto.CoinGeckoDTO;
import com.hayden.data.models.dto.MessariDTO;
import com.hayden.data.models.shared.request.UrlFactory;
import com.hayden.data.services.utility.ExchangeProps;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.enums.ExchangeEnum;
import com.hayden.decision.models.enums.TimeUnitEnum;
import com.hayden.decision.models.shared.AssetDTO;
import com.hayden.decision.models.shared.DTO;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Data
public class Exchanges implements ApplicationContextAware {

    ApplicationContext applicationContext;
    UrlFactory urlFactory;
    ExchangeProps exchangeProps;

    @Autowired
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }


    public Exchanges(
            ExchangeProps exchangeProps,
            UrlFactory urlFactory
    )
    {
        this.urlFactory = urlFactory;
        this.exchangeProps = exchangeProps;
        initKeys();
    }

    public final Map<ExchangeEnum, Map<String, String>> CONST_KEYS = new HashMap<>();

    public void initKeys()
    {
        Map<String, String> value = new HashMap<>();
        value.put("x-messari-api-key", exchangeProps.getMessariApiKey());
        CONST_KEYS.put(ExchangeEnum.MESSARI,
                       value
        );
    }

    public ExchangeEnum exchange(Class<? extends AssetDTO<? extends Asset>> clzz){
        return EXCHANGE.get(clzz);
    }

    public String timeUnit(
            TimeUnitEnum timeUnitEnum,
            Class<? extends AssetDTO<? extends Asset>> clzz
    )
    {
        return UrlFactory.TIME_UNIT_CONVERSION.get(exchange(clzz)).get(timeUnitEnum);
    }

    public static final Map<Class<? extends DTO>, ExchangeEnum> EXCHANGE = Map.of(
            CoinGeckoDTO.class,
            ExchangeEnum.COINGECKO,
            BinanceDTO.class,
            ExchangeEnum.BINANCE,
            MessariDTO.class,
            ExchangeEnum.MESSARI
    );

    public HttpHeaders httpHeaders(
            ExchangeEnum exchange,
            UrlFactory.Vals... vals
    )
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        Arrays.stream(vals)
                .forEach(val -> httpHeaders.add(
                        UrlFactory.REST_VALS_MAP.get(exchange).get(val),
                        CONST_KEYS.get(exchange).get(UrlFactory.REST_VALS_MAP.get(exchange).get(val))
                         )
                );
        return httpHeaders;
    }

}
