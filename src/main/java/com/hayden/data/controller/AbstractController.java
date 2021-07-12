package com.hayden.data.controller;

import com.hayden.decision.models.enums.DataType;
import com.hayden.data.models.dto.AssetMessage;
import com.hayden.decision.models.shared.GetPrices;
import com.hayden.data.services.rest.APIDataService;
import com.hayden.data.services.rest.AssetDTOFactory;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import com.hayden.decision.models.shared.AssetReq;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

@Log4j2
public abstract class AbstractController<DTO extends AssetDTO<DATAITEM>, DATAITEM extends Asset> implements DataController<DTO, DATAITEM>{

    AssetDTOFactory assetService;
    RSocketRequester.Builder rBuilder;
    RSocketStrategies rsocketStrategies;
    List<APIDataService<DATAITEM>> apis;

    public AbstractController(AssetDTOFactory assetService,
                              RSocketRequester.Builder rBuilder,
                              @Qualifier("rSocketStrategies") RSocketStrategies rsocketStrategies,
                              List<APIDataService<DATAITEM>> apis
    ) {
        this.assetService = assetService;
        this.rBuilder = rBuilder;
        this.rsocketStrategies = rsocketStrategies;
        this.apis = apis;
    }

    public Flux<AssetDTO<DATAITEM>> allAssets(APIDataService<DATAITEM> dataService, AssetReq assetReq) {
        return dataService.all(assetReq.timeUnitEnum(), assetReq.dataType());
    }

    public Publisher<AssetDTO<DATAITEM>> oneAsset(APIDataService<DATAITEM> dataService, AssetReq assetReq) {
        return Flux.from(dataService.one(assetReq.symbol(), assetReq.timeUnitEnum()));
    }

    public Flux<AssetMessage> all(AssetReq assetReq)
    {
        return iterateThroughAPIs(api -> allAssets(api, assetReq))
                .buffer(Duration.ofSeconds(5))
                .flatMap(construct())
                .map(asset -> assetService.newAssetMessage(asset));
    }

    public Publisher<AssetMessage> one(AssetReq assetReq)
    {
        return iterateThroughAPIs((api) -> oneAsset(api, assetReq))
                .buffer(Duration.ofSeconds(5))
                .flatMap(construct())
                .map(asset -> assetService.newAssetMessage(asset));
    }

    private Flux<AssetDTO<DATAITEM>> iterateThroughAPIs(Function<APIDataService<DATAITEM>, Publisher<AssetDTO<DATAITEM>>> iterFunc)
    {
        return Flux.fromIterable(apis)
                .flatMap(iterFunc);
    }

    private Function<List<AssetDTO<DATAITEM>>, Flux<GetPrices>> construct()
    {
        return dtos -> Flux.fromIterable(dtos)
                .collectMultimap(AssetDTO::getSymbol)
                .flatMapMany(data -> assetService.buildTransfer(data));
    }

}
