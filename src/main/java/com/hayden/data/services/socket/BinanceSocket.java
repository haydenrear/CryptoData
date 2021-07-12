package com.hayden.data.services.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hayden.decision.models.enums.DataType;
import com.hayden.data.models.dto.BinanceDTO;
import com.hayden.data.models.shared.request.RequestBuilder;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.request.UrlFactory;
import com.hayden.data.models.shared.response.ExchangeRequest;
import com.hayden.data.models.socket.SocketAdapterFactory;
import com.hayden.data.models.socket.request.BinanceDataRequest;
import com.hayden.data.models.socket.response.BinanceOrderStream;
import com.hayden.data.models.socket.response.BinanceTickerResponse;
import com.hayden.data.models.Exchanges;
import com.hayden.data.models.socket.request.SocketRequester;
import com.hayden.data.services.rest.APIDataService;
import com.hayden.data.services.rest.AssetDTOFactory;
import com.hayden.data.services.rest.BinanceDataService;
import com.hayden.data.services.shared.AssetClass;
import com.hayden.data.services.shared.RequestObject;
import com.hayden.decision.models.asset.Crypto;
import com.hayden.decision.models.enums.ExchangeEnum;
import com.hayden.decision.models.enums.TimeUnitEnum;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Data
@Service
@EqualsAndHashCode(callSuper = false)
@OrderBookWS(BinanceOrderStream.class)
@RequestObject(BinanceDataRequest.class)
@HistoricalWS(BinanceTickerResponse.class)
@AssetClass(BinanceDTO.class)
public class BinanceSocket extends AbstractSocket<Crypto, APIDataService<Crypto>, Crypto> {

    public BinanceSocket (
            Exchanges exchanges,
            AssetDTOFactory assetDTOService,
            ObjectMapper objectMapper,
            SocketRequester builder,
            BinanceDataService binanceDataService,
            UrlFactory urlFactory,
            SocketAdapterFactory socketAdapterFactory
    )
    {
        super(exchanges, assetDTOService, objectMapper, builder, urlFactory, binanceDataService, socketAdapterFactory);
    }

    @Override
    protected ExchangeRequest buildRequest (
            RequestBuilder<?, ?, ? extends AssetDTO<?>, ?> requestBuilder,
            DataType dataType,
            String symbol,
            TimeUnitEnum timeUnitEnum,
            Date startMilli,
            Date endMilli,
            String metric
    )
    {
        return requestBuilder.buildRequest(next(), binanceRequestValue(timeUnitEnum, dataType, symbol.toLowerCase()));
    }

    public List<String> binanceRequestValue(
            TimeUnitEnum timeUnitEnum,
            DataType dataType,
            String symbol
    )
    {
        if(dataType.equals(DataType.HISTORICAL_WSS))
            return List.of(symbol+"@kline_"+ UrlFactory.TimeUnit(ExchangeEnum.BINANCE, timeUnitEnum).orElse("1m"));
        else if (dataType.equals(DataType.ORDER_BOOK_WSS))
            return List.of(symbol+"@depth@100ms");
        else return List.of();
    }

    private int socketId = 1;

    public synchronized int next(){
        return socketId++;
    }

    @Override
    public Url createUrl(TimeUnitEnum timeUnitEnum, DataType dataType, String symbol, Date startMilli, Date endMilli, Integer limit, String metric, String pagination, HttpHeaders headers)
    {
        return urlFactory.createBuilder(clzz, dataType, headers).build();
    }

}
