package com.hayden.data.controller;

import com.hayden.data.services.rest.APIDataService;
import com.hayden.data.services.rest.APIError;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import com.hayden.decision.models.shared.AssetReq;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface DataController<V extends AssetDTO<ASSET>, ASSET extends Asset> {

    Publisher<AssetDTO<ASSET>> allAssets(APIDataService<ASSET> dataService, AssetReq assetReq) throws APIError;
    Publisher<AssetDTO<ASSET>> oneAsset(APIDataService<ASSET> dataService, AssetReq assetReq);

}
