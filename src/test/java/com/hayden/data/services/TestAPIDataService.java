package com.hayden.data.services;


import com.hayden.decision.models.enums.DataType;
import com.hayden.data.models.Exchanges;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.services.rest.*;
import com.hayden.data.services.utility.Dynamic;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.enums.TimeUnitEnum;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestAPIDataService {

    @Autowired
    List<APIDataService<? extends Asset>> dataServices;
    @Autowired
    MessariDataService messariDataService;
    @Autowired
    CoinGeckoDataService coinGeckoDataService;
    @Autowired
    BinanceDataService binanceDataService;
    @Autowired
    Dynamic dynamic;
    @Autowired
    Exchanges exchanges;

//    @Test
    public void createBinanceBars(){
        dynamic.writeClassFromString(
                binanceDataService.getHeaders(),
                binanceDataService,
                "BinanceBars",
                binanceDataService.createUrl(
                        TimeUnitEnum.DAY,
                        DataType.HISTORICAL_MARKET,
                        "BTCUSDT",
                        Date.from(Instant.now().minusSeconds(60 * 60 * 24 * 365)),
                        Date.from(Instant.now()),
                        5,
                        "",
                        "",
                        binanceDataService.getHeaders()
                ).build()
        );
    }

    @Test
    public void thereAreFourServices(){
        assertThat(dataServices.size()).isEqualTo(4);
    }

    @Test
    public void coinGecko()
    {
        allStepVerifier(coinGeckoDataService, DataType.TRENDING);
        allStepVerifier(coinGeckoDataService, DataType.ASSET_METADATA);
    }

    @Test
    public void binance()
    {
        allStepVerifier(binanceDataService, DataType.ASSET_METADATA);
    }

    @Test
    public void messari()
    {
        allStepVerifier(messariDataService, DataType.ASSET_METADATA);
    }


    @SneakyThrows
    public void allStepVerifier(APIDataService<? extends Asset> service, DataType symbolType)
    {
        StepVerifier.create(service.all(TimeUnitEnum.DAY, symbolType))
                .expectSubscription()
                .assertNext(asset -> {
                    assertThat(asset.getSymbol()).isNotBlank();
                    assertThat(asset.getPrices().size()).isNotZero();
                    assertThat(asset.getSymbol()).isNotBlank();
                })
                .thenConsumeWhile(val -> val != null && val.getPrices() != null && val.getSymbol() != null, asset -> System.out.println(asset.getSymbol() + "\n\n" + asset.getPrices()))
                .thenCancel()
                .verify();
    }

    public void stepVerifier(APIDataService<? extends Asset> service, String symbol, DataType dataType, Consumer<ExchangeResponse> consume){
        StepVerifier.create(
                Flux.from(service.requestSingle(
                        symbol,
                        TimeUnitEnum.DAY,
                        dataType,
                        Date.from(Instant.now().minus(45, ChronoUnit.DAYS)),
                        Date.from(Instant.now().minus(4, ChronoUnit.DAYS)),
                        10,
                        ""
                )).cast(ExchangeResponse.class))
                .expectSubscription()
                .assertNext(consume)
                .thenConsumeWhile(Objects::nonNull, System.out::println)
                .verifyComplete();
    }

}
