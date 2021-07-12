package com.hayden.data.services;

import com.hayden.decision.models.enums.DataType;
import com.hayden.data.services.rest.*;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.enums.TimeUnitEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestHistorical extends TestAPIDataService {

    @Autowired
    MessariDataService messariDataService;
    @Autowired
    CoinGeckoDataService coinGeckoDataService;
    @Autowired
    BinanceDataService binanceDataService;

    @Test
    public void binance()
    {
        assertHistoricalUrl(binanceDataService, "https://api.binance.com/api/v3/klines?symbol=btcusdt&limit=10&interval=1h&startTime=1622479056122&endTime=1625589456122", "btcusdt");
        historicalStepVerifier(
                binanceDataService,
                "BTCUSDT"
        );
    }

    @Test
    public void messari(){
        assertHistoricalUrl(messariDataService, "https://data.messari.io/api/v1/markets/binance-ETH-BTC/metrics/price/time-series?start=1622479056122&limit=10&interval=1h&end=1625589456122", "binance-ETH-BTC");
        historicalStepVerifier(
                messariDataService,
                "binance-btc-usdt"
        );
    }

    @Test
    public void coinGecko()
    {
        assertHistoricalUrl(coinGeckoDataService, "https://api.coingecko.com/api/v3/coins/bitcoin/market_chart?days=10&vs_currency=usd", "bitcoin");
        historicalStepVerifier(
                coinGeckoDataService, "bitcoin"
        );
    }

    private void assertHistoricalUrl(APIDataService<? extends Asset> dataService, String urlCreated, String symbol)
    {
        String urlBuilt = dataService.createUrl(
                TimeUnitEnum.ONEHOUR,
                DataType.HISTORICAL_MARKET,
                symbol,
                Date.from(Instant.ofEpochMilli(1622479056122L)),
                Date.from(Instant.ofEpochMilli(1625589456122L)),
                10,
                "",
                "",
                dataService.getHeaders()
        ).build();
        System.out.println(urlBuilt);
        assertThat(urlBuilt).isEqualTo(urlCreated);
    }

    public void historicalStepVerifier(APIDataService<? extends Asset> service, String symbol){
        stepVerifier(service, symbol, DataType.HISTORICAL_MARKET,
                     val -> {
                        Assertions.assertThat(val).isNotNull();
                        System.out.println(val);
                }
        );
    }



}
