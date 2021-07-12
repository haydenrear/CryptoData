package com.hayden.data.services.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.models.*;
import com.hayden.data.models.dto.MessariDTO;
import com.hayden.data.models.rest.response.MessariBars;
import com.hayden.data.models.rest.response.MessariSymbol;
import com.hayden.data.models.rest.request.WebClientRequester;
import com.hayden.data.models.shared.request.UrlFactory;
import com.hayden.data.models.rest.RestAdapterFactory;
import com.hayden.data.services.shared.AssetClass;
import com.hayden.data.services.shared.Exchange;
import com.hayden.data.services.shared.Historical;
import com.hayden.data.services.shared.Symbol;
import com.hayden.decision.models.asset.Crypto;
import com.hayden.decision.models.enums.ExchangeEnum;
import lombok.Getter;

import org.springframework.stereotype.Service;

@Service(value = "messari")
@Getter
@Historical(MessariBars.class)
@AssetClass(MessariDTO.class)
@Symbol(MessariSymbol.class)
@Exchange(ExchangeEnum.MESSARI)
public class MessariDataService extends APIDataService<Crypto> {

    public MessariDataService(
            Exchanges exchanges,
            AssetDTOFactory assetDTOService,
            RestAdapterFactory adapterFactory,
            ObjectMapper objectMapper,
            WebClientRequester builder,
            UrlFactory urlFactory
    )
    {
        super(
                exchanges,
                adapterFactory,
                assetDTOService,
                objectMapper,
                builder,
                urlFactory
        );
    }

}