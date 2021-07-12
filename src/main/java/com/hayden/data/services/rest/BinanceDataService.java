package com.hayden.data.services.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.models.*;
import com.hayden.data.models.dto.BinanceDTO;
import com.hayden.data.models.rest.response.BinanceBars;
import com.hayden.data.models.rest.response.BinanceSymbols;
import com.hayden.data.models.rest.request.WebClientRequester;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.request.UrlFactory;
import com.hayden.data.models.rest.RestAdapterFactory;
import com.hayden.data.models.socket.response.BinanceOrderResponse;
import com.hayden.data.services.shared.*;
import com.hayden.decision.models.asset.Crypto;
import com.hayden.decision.models.enums.DataType;
import com.hayden.decision.models.enums.ExchangeEnum;
import com.hayden.decision.models.enums.TimeUnitEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Data
@Historical(BinanceBars.class)
@AssetClass(BinanceDTO.class)
@OrderBook(value = BinanceOrderResponse.class)
@Symbol(BinanceSymbols.class)
@Exchange(ExchangeEnum.BINANCE)
@EqualsAndHashCode(callSuper = false)
public class BinanceDataService extends APIDataService<Crypto> {

    public BinanceDataService(
            Exchanges exchanges,
            AssetDTOFactory assetDTOService,
            RestAdapterFactory adapterFactory,
            ObjectMapper objectMapper,
            WebClientRequester builder,
            UrlFactory urlFactory
    )
    {
        super(exchanges, adapterFactory, assetDTOService, objectMapper, builder, urlFactory);
    }

    public record SymbolAsset(String asset, String marginAvailable, String autoAssetExchange) {}

}
