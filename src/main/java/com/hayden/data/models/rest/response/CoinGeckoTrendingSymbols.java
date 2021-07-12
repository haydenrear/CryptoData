package com.hayden.data.models.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hayden.data.models.rest.To;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

@To(CoinGeckoTrendingSymbol.class)
public final class CoinGeckoTrendingSymbols implements IntermediateResponse<CoinGeckoTrendingSymbol> {

    @JsonProperty("coins")
    private final List<CoinGeckoTrendingSymbol> coins;
    @JsonProperty("exchanges")
    private final List<String> exchanges;

    public CoinGeckoTrendingSymbols(@JsonProperty("coins") List<CoinGeckoTrendingSymbol> coins,
                                    @JsonProperty("exchanges") List<String> exchanges) {
        this.coins = coins;
        this.exchanges = exchanges;
    }

    @JsonProperty("coins")
    public List<CoinGeckoTrendingSymbol> coins() {
        return coins;
    }

    @JsonProperty("exchanges")
    public List<String> exchanges() {
        return exchanges;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CoinGeckoTrendingSymbols) obj;
        return Objects.equals(this.coins, that.coins) &&
                Objects.equals(this.exchanges, that.exchanges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coins, exchanges);
    }

    @Override
    public String toString() {
        return "TrendingItems[" +
                "coins=" + coins + ", " +
                "exchanges=" + exchanges + ']';
    }

    @Override
    public Publisher<CoinGeckoTrendingSymbol> responses()
    {
        return Flux.fromIterable(coins);
    }


}
