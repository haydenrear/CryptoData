package com.hayden.data.models.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
@Data
@NoArgsConstructor
public final class CoinGeckoSymbol implements SymbolResponse {

    @JsonProperty("id")
    private String id;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("name")
    private String name;

    public CoinGeckoSymbol(
            @JsonProperty("id") String id,
            @JsonProperty("symbol") String symbol,
            @JsonProperty("name") String name
    )
    {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
    }

    @Override
    public String getSymbol() {
        return this.id;
    }

    @Override
    public <T extends Asset> AssetDTO<T> visit(AssetDTO<T> dto)
    {
        dto.setSymbol(id);
        return dto;
    }
}
