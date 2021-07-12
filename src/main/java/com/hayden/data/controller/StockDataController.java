package com.hayden.data.controller;

import com.hayden.data.models.dto.AssetMessage;
import com.hayden.data.services.rest.APIDataService;
import com.hayden.data.services.rest.AssetDTOFactory;
import com.hayden.decision.models.asset.Stock;
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
public class StockDataController extends AbstractController<AssetDTO<Stock>, Stock> {


    public StockDataController(AssetDTOFactory assetService,
                               RSocketRequester.Builder rBuilder,
                               RSocketStrategies rsocketStrategies,
                               List<APIDataService<Stock>> apis
                               ) {
        super(assetService, rBuilder, rsocketStrategies, apis);
    }

    @MessageMapping("allStock")
    public Flux<AssetMessage> all(AssetReq assetReq) {
        return super.all(assetReq);
    }

    @MessageMapping("oneStock")
    public Publisher<AssetMessage> one(AssetReq assetReq) {
        return super.one(assetReq);
    }

    @MessageMapping("trendingStock")
    public Flux<AssetMessage> trending(AssetReq assetReq) {
//        return super.trending(assetReq);
        return Flux.empty();
    }

}
