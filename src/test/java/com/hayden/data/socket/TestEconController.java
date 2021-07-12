package com.hayden.data.socket;

import com.hayden.decision.models.shared.EconDataWSDTO;
import com.hayden.decision.models.RSocketClientImpl;
import com.hayden.decision.models.sectors.model.Cyclicality;
import com.hayden.data.services.rest.AssetDTOFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestEconController {

    @Autowired
    RSocketClientImpl rSocketClient;
    @Autowired
    AssetDTOFactory assetService;
    @Value("${spring.rsocket.server.port}")
    Integer port;

//    @BeforeAll
//    public static void setupOnce(@Autowired RSocketRequester.Builder builder, @Value("${spring.rsocket.server.port}") Integer port) {
//        builder.tcp("localhost", port);
//    }

    @Test
    public void testEconController(){
        StepVerifier.create(rSocketClient.init("localhost", port)
                .route("econ")
                .data(assetService.newGetCorrelateReq(Cyclicality.NEUTRAL))
                .retrieveFlux(EconDataWSDTO.class))
                .expectSubscription()
                .consumeNextWith(econDataWSDTO-> {
                    System.out.println(econDataWSDTO.getDataMetrics().metrics().getOriginalPrices().values());
                })
                .assertNext(data -> {
                    System.out.println(data.getDataMetrics().metrics().getOriginalPrices());
                    assertThat(data.getDataMetrics().metrics().getOriginalPrices().size()).isNotZero();
                })
                .thenCancel()
                .verify();
    }

    @Test
    public void testSubscribe() throws InterruptedException {
        Thread.sleep(5000);
        rSocketClient.init("localhost", port)
                .route("econ")
                .data(assetService.newGetCorrelateReq(Cyclicality.NEUTRAL))
                .retrieveFlux(EconDataWSDTO.class)
                .map(econDataWSDTO -> {
                    System.out.println(econDataWSDTO.getDataMetrics().metrics().getOriginalPrices());
                    return econDataWSDTO;
                }).subscribe();
    }

}
