package com.hayden.data.models.rest.response;

import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import com.hayden.decision.models.shared.Bar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
@Scope("prototype")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bars implements ExchangeResponse {

    private ConcurrentSkipListMap<Date, Bar> bars;

    @Override
    public <T extends Asset> AssetDTO<T> visit(AssetDTO<T> dto)
    {
        dto.setPrices(this.bars);
        return dto;
    }

    @Override
    public boolean notEmpty()
    {
        return !(bars == null || bars.size() == 0);
    }
}
