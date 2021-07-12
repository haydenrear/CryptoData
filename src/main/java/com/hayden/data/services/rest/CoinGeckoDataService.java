package com.hayden.data.services.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.data.models.*;
import com.hayden.data.models.dto.CoinGeckoDTO;
import com.hayden.data.models.rest.response.CoinGeckoBars;
import com.hayden.data.models.rest.response.CoinGeckoSymbol;
import com.hayden.data.models.rest.response.CoinGeckoTrendingSymbols;
import com.hayden.data.models.rest.request.WebClientRequester;
import com.hayden.data.models.shared.request.UrlFactory;
import com.hayden.data.models.rest.RestAdapterFactory;
import com.hayden.data.services.shared.*;
import com.hayden.decision.models.asset.Crypto;
import com.hayden.decision.models.enums.ExchangeEnum;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service("coinGecko")
@Historical(CoinGeckoBars.class)
@AssetClass(CoinGeckoDTO.class)
@Symbol(CoinGeckoSymbol.class)
@Exchange(ExchangeEnum.COINGECKO)
@Trending(CoinGeckoTrendingSymbols.class)
public class CoinGeckoDataService extends APIDataService<Crypto> {


    public CoinGeckoDataService(
            Exchanges exchanges,
            RestAdapterFactory adapterFactory,
            AssetDTOFactory assetDTOService,
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

    @Override
    public Predicate<String> symbolFilters()
    {
        return token -> !token.contains("half") && !token.contains("bull") && !token.contains("short") && !token.contains("1x") && !token.contains("3x") && !token.contains("5x");
    }

}
