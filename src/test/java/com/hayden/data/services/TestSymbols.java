package com.hayden.data.services;

import com.hayden.decision.models.enums.DataType;
import com.hayden.data.services.rest.*;
import com.hayden.decision.models.asset.Asset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestSymbols extends TestAPIDataService {

    @Autowired
    MessariDataService messariDataService;
    @Autowired
    CoinGeckoDataService coinGeckoDataService;
    @Autowired
    BinanceDataService binanceDataService;

    @Test
    public void binance()
    {
        assertSymbolsUrl("https://api.binance.com/api/v3/exchangeInfo", binanceDataService);
        testSymbols(binanceDataService, DataType.ASSET_METADATA);
    }

    @Test
    public void messari(){
        testSymbols(messariDataService, DataType.ASSET_METADATA);
        assertSymbolsUrl("https://data.messari.io/api/v1/markets?page=1", messariDataService);
    }

    @Test
    public void coinGecko()
    {
        assertSymbolsUrl("https://api.coingecko.com/api/v3/coins/list?include_platform=false", coinGeckoDataService);
        testTrendingSymbols(coinGeckoDataService);
        testSymbols(coinGeckoDataService, DataType.ASSET_METADATA);
    }

    private void testTrendingSymbols(CoinGeckoDataService coinGeckoDataService)
    {
        testSymbols(coinGeckoDataService, DataType.TRENDING);
    }

    public void assertSymbolsUrl(String url, APIDataService<? extends Asset> dataService)
    {
        String urlBuilt = dataService.symbolsUrl(DataType.ASSET_METADATA).build();
        System.out.println(urlBuilt);
        assertThat(urlBuilt).isEqualTo(url);
    }

    public void testSymbols(APIDataService<? extends Asset> dataService, DataType assetMetadata)
    {
        StepVerifier.create(dataService.getSymbols(assetMetadata))
                .expectSubscription()
                .assertNext(str -> {
                    System.out.println(str);
                    assertThat(str).isNotNull();
                    assertThat(str.length()).isNotZero();
                })
                .thenConsumeWhile(Objects::nonNull, System.out::println)
                .verifyComplete();
    }

}
