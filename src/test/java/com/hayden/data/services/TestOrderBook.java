package com.hayden.data.services;

import com.hayden.data.services.rest.BinanceDataService;
import com.hayden.data.services.shared.ExternalDataService;
import com.hayden.decision.models.enums.DataType;
import com.hayden.decision.models.enums.TimeUnitEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Date;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestOrderBook {

    @Autowired
    BinanceDataService binanceDataService;

    @Test
    public void testOrderBook()
    {
        System.out.println(binanceDataService.createUrl(
                TimeUnitEnum.ONEHOUR,
                DataType.ORDER_BOOK_REST,
                "btcusdt",
                Date.from(Instant.ofEpochMilli(1622479056122L)),
                Date.from(Instant.ofEpochMilli(1625589456122L)),
                10,
                "",
                "",
                binanceDataService.getHeaders()
        ).build());
//        binanceDataService.getSymbols(DataType.ASSET_METADATA)
//                .take(30)
//                .flatMap(symbol -> {
//                    return binanceDataService.requestSingle(symbol, TimeUnitEnum.SECOND, DataType.ORDER_BOOK_REST, ExternalDataService.DEFAULT_START_TIME, ExternalDataService.DEFAULT_END_TIME, ExternalDataService.DEFAULT_LIMIT, "");
//                })
//                .subscribe(er -> {
//                    System.out.println(er);
//                });
    }

}
