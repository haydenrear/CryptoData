package com.hayden.data.models.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;

import java.util.Objects;

public final class CoinGeckoTrendingSymbol implements SymbolResponse {

    @JsonProperty("item")
    private final TrendingToken item;

    public CoinGeckoTrendingSymbol(@JsonProperty("item") TrendingToken item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CoinGeckoTrendingSymbol) obj;
        return Objects.equals(this.item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }

    @Override
    public String toString() {
        return "TrendingItemDTO[" +
                "item=" + item + ']';
    }

    @Override
    public String getSymbol() {
        return this.item.id();
    }

    @Override
    public <T extends Asset> AssetDTO<T> visit(AssetDTO<T> dto)
    {
        dto.setSymbol(this.item.symbol());
        return dto;
    }
}

record TrendingToken(@JsonProperty("id") String id,
                     @JsonProperty("name") String name,
                     @JsonProperty("symbol") String symbol,
                     @JsonProperty("market_cap_rank") int market_cap_rank,
                     @JsonProperty("thumb") String thumb,
                     @JsonProperty("large") String large,
                     @JsonProperty("score") int score) {
}
