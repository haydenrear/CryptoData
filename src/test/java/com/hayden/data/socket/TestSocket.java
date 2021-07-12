package com.hayden.data.socket;

import com.hayden.decision.models.enums.DataType;
import com.hayden.data.models.socket.request.BinanceDataRequest;
import com.hayden.data.models.socket.response.BinanceOrderStream;
import com.hayden.data.models.socket.response.BinanceTickerResponse;
import com.hayden.data.services.socket.BinanceSocket;
import com.hayden.decision.models.enums.TimeUnitEnum;
import com.hayden.decision.models.shared.AssetDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestSocket {

    @Autowired
    BinanceSocket binanceSocket;

    @SafeVarargs
    public final <T> void stepVerifier(ParameterizedTypeReference<T> t, Publisher<T> publisher, Consumer<T>... asserts)
    {
        var sv = StepVerifier
                .create(Flux.from(publisher))
                .expectSubscription();
        Arrays.stream(asserts).forEach(sv::assertNext);
        sv.thenCancel().verify();
    }

    @Test
    public void testAll()
    {
        stepVerifier(
                new ParameterizedTypeReference<>() {},
                binanceSocket.all(
                        TimeUnitEnum.SECOND,
                        Date.from(Instant.now().minus(45, ChronoUnit.DAYS)),
                        Date.from(Instant.now().minus(4, ChronoUnit.DAYS)),
                        22,
                        DataType.ASSET_METADATA,
                        ""
                ),dto -> {
                    assertThat(dto).isInstanceOf(AssetDTO.class);
                    assertThat(dto).isInstanceOf(AssetDTO.class);
                    assertThat(dto.getSymbol()).isNotBlank();
                    assertThat(dto.getPrices().size()).isNotZero();
                }
        );
    }

    @Test
    public void testOne()
    {
        stepVerifier(
                new ParameterizedTypeReference<>() {},
                binanceSocket.one(
                        "btcusdt",
                        TimeUnitEnum.SECOND,
                        Date.from(Instant.now().minus(45, ChronoUnit.DAYS)),
                        Date.from(Instant.now().minus(4, ChronoUnit.DAYS)),
                        10,
                        ""
                ), dto -> {
                    assertThat(dto).isInstanceOf(AssetDTO.class);
                    assertThat(dto).isInstanceOf(AssetDTO.class);
                    assertThat(dto.getSymbol()).isNotBlank();
                    assertThat(dto.getPrices().size()).isNotZero();
                    System.out.println(dto);
                    System.out.println(dto.getSymbol());
                    System.out.println(dto.getPrices());
                    System.out.println(dto.getBidAsks());
                }
        );
    }



    @Test
    public void testHistoricalData()
    {
        stepVerifier(
                new ParameterizedTypeReference<>() {},
                (Publisher<BinanceTickerResponse>) binanceSocket.requestSingle(
                        "btcusdt", TimeUnitEnum.SECOND, DataType.HISTORICAL_WSS, null, null, null, null),
                socket -> {
                    assertThat(socket).isInstanceOf(BinanceTickerResponse.class);
                    System.out.println(socket);
                    System.out.println(socket);
                },
                socket -> {
                    assertThat(socket).isInstanceOf(BinanceTickerResponse.class);
                    System.out.println(socket);
                    System.out.println(socket);
                }
        );
    }

    @Test
    public void testMarketData()
    {
        stepVerifier(
                new ParameterizedTypeReference<>() {
                },
                     (Publisher<BinanceOrderStream>) binanceSocket.requestSingle(
                             "btcusdt",
                             null,
                             DataType.ORDER_BOOK_WSS,
                             null,
                             null,
                             null,
                             null
                     ), socket -> {
                    assertThat(socket).isInstanceOf(BinanceOrderStream.class);
                    System.out.println(socket.a);
                    System.out.println(socket.b);
                }, socket -> {
                    assertThat(socket).isInstanceOf(BinanceOrderStream.class);
                    System.out.println(socket.a);
                    System.out.println(socket.b);
                }
        );
    }

    @Test
    public void testAbstract()
    {
        var requestVal = binanceSocket.binanceRequestValue(TimeUnitEnum.DAY, DataType.ORDER_BOOK_WSS, "btcusdt");
        assertThat(requestVal).hasSameElementsAs(List.of("btcusdt@depth@100ms"));
        var rb = binanceSocket.requestBuilder(DataType.ORDER_BOOK_WSS);
        var bdr = rb.buildRequest(1, requestVal);
        System.out.println(bdr);
        var assertVal = new BinanceDataRequest(1, requestVal);
        assertThat(bdr).isEqualTo(assertVal);
    }


}