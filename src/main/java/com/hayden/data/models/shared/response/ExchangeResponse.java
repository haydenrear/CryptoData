package com.hayden.data.models.shared.response;

import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;

public interface ExchangeResponse extends Response {

    <T extends Asset> AssetDTO<T> visit(AssetDTO<T> dto);

}