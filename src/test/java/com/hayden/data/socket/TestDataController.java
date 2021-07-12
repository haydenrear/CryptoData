package com.hayden.data.socket;

import com.hayden.data.controller.CryptoDataController;
import com.hayden.data.models.dto.AssetMessage;
import com.hayden.data.services.rest.APIDataService;
import com.hayden.decision.models.RSocketClientImpl;
import com.hayden.decision.models.asset.Crypto;
import com.hayden.decision.models.enums.DataType;
import com.hayden.decision.models.enums.PriceType;
import com.hayden.decision.models.enums.TimeUnitEnum;
import com.hayden.decision.models.shared.AssetDTO;
import com.hayden.data.services.rest.AssetDTOFactory;
import com.hayden.decision.models.shared.AssetReq;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestDataController {

    @Autowired
    RSocketClientImpl rSocketClient;
    @Autowired
    AssetDTOFactory assetService;
    @Value("${spring.rsocket.server.port}")
    Integer port;
    @Autowired
    CryptoDataController cryptoDataController;
    @Qualifier("coinGecko") @Autowired
    APIDataService<Crypto> api;


    @Test
    public void oneCrypto() throws InterruptedException
    {
        Flux.from(cryptoDataController.one(new AssetReq(TimeUnitEnum.DAY, PriceType.CLOSE, "bitcoin", null, DataType.ASSET_METADATA)))
            .subscribe(dto -> {
                System.out.println(dto.getGetPrices().getOrderBook());
                System.out.println(dto.getGetPrices().getSymbol());
                System.out.println(dto.getGetPrices().getPrices());
            });
        Thread.sleep(10000);
    }

    @Test
    public void allCrypto()
    {
        stepVerifier(
                    cryptoDataController.all(new AssetReq(TimeUnitEnum.DAY, PriceType.CLOSE, null, null, DataType.ASSET_METADATA)),
                    assetMessage -> assertThat(assetMessage.getGetPrices().getSymbol()).isNotBlank()
                );
//        Flux.from(cryptoDataController.all(new AssetReq(TimeUnitEnum.DAY, PriceType.CLOSE, null, null, DataType.ASSET_METADATA)))
//                .subscribe(dto -> {
//                    System.out.println(dto.getGetPrices().getOrderBook());
//                    System.out.println(dto.getGetPrices().getSymbol());
//                    System.out.println(dto.getGetPrices().getPrices());
//                });
//        Thread.sleep(10000);
    }

    @SafeVarargs
    public final void stepVerifier(Publisher<AssetMessage> publisher, Consumer<AssetMessage>... asserts)
    {
        var sv = StepVerifier.create(publisher)
                .expectSubscription();
        Arrays.stream(asserts).forEach(sv::assertNext);
        sv.thenConsumeWhile(assetMessage -> assetMessage.getGetPrices() != null && assetMessage.getGetPrices().getPrices() != null, System.out::println)
                .thenCancel()
                .verify();
    }

}
