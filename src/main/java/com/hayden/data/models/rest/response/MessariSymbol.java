package com.hayden.data.models.rest.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hayden.data.models.rest.To;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Component
@Scope("prototype")
@Data
@NoArgsConstructor
@To(MessariSymbol.data.class)
public class MessariSymbol extends PageableResponse implements Serializable, IntermediateResponse<MessariSymbol.data> {

    public MessariSymbol.data[] data;
    public MessariSymbol.status status;

    @Override
    public String value()
    {
        return String.valueOf(MessariSymbol.nextVal);
    }

    @Override
    public boolean isFinished()
    {
        return !notEmpty() || this.finished;
    }

    @Override
    public <T extends ExchangeResponse> void combine(T exchangeResponse)
    {
        super.combine(exchangeResponse);
    }

    @JsonIgnore
    static volatile int nextVal = 1;


    @Override
    public synchronized String nextVal()
    {
        return String.valueOf(++nextVal);
    }

    @Override
    public synchronized void finish()
    {
        nextVal = 0;
        this.finished = true;
    }

    @Override
    public boolean notEmpty()
    {
        return !(this.data == null || this.data.length == 0);
    }

    @Override
    protected void resetVal()
    {
        nextVal = 0;
        this.finished = true;
    }

    @Override
    public Publisher<MessariSymbol.data> responses()
    {
        return Flux.fromArray(data);
    }

    @Data
    public static class data implements Serializable, SymbolResponse {
        public String price_usd;
        public String quote_asset_id;
        public String trade_end;
        public String quote_asset_symbol;
        public Long vwap_weight;
        public String base_asset_id;
        public Long version;
        public String base_asset_symbol;
        public String pair;
        public String exchange_name;
        public String exchange_id;
        public String trade_start;
        public String last_trade_at;
        public String id;
        public Boolean has_real_volume;
        public String deviation_from_vwap_percent;
        @JsonProperty("class")
        public String clzz;
        public String exchange_slug;
        public Boolean excluded_from_price;
        public String volume_last_24_hours;

        public data() {
        }

        @Override
        public <T extends Asset> AssetDTO<T> visit(AssetDTO<T> dto)
        {
            dto.setSymbol(this.base_asset_symbol.toLowerCase());
            return dto;
        }

        @Override
        public String getSymbol()
        {
            return this.exchange_slug+"-"+this.base_asset_symbol+"-"+this.quote_asset_symbol;
        }
    }

    @Data
    public static class status implements Serializable {
        public Long elapsed;
        public String timestamp;

        public status() {
        }
    }
}
