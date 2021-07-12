package com.hayden.data.controller;

import com.hayden.data.models.dto.AssetMessage;
import com.hayden.data.services.rest.APIDataService;
import com.hayden.data.services.rest.AssetDTOFactory;
import com.hayden.decision.models.asset.Crypto;
import com.hayden.decision.models.shared.AssetDTO;
import com.hayden.decision.models.shared.AssetReq;
import org.reactivestreams.Publisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Controller
public class CryptoDataController extends AbstractController<AssetDTO<Crypto>, Crypto> {

    public CryptoDataController(
            AssetDTOFactory assetService,
            RSocketRequester.Builder rBuilder,
            List<APIDataService<Crypto>> cryptoAPIs,
            RSocketStrategies rsocketStrategies
    )
    {
        super(assetService, rBuilder, rsocketStrategies, cryptoAPIs);
    }

    @MessageMapping("allCrypto")
    public Flux<AssetMessage> all(AssetReq assetReq)
    {
        return super.all(assetReq);
    }

    @MessageMapping("oneCrypto")
    public Publisher<AssetMessage> one(AssetReq assetReq)
    {
        return super.one(assetReq);
    }

}

