package com.hayden.data.models.dto;

import com.hayden.data.services.rest.APIDataService;
import com.hayden.decision.models.asset.Crypto;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Component
@Scope("prototype")
@Data
@NoArgsConstructor
public class BinanceDTO extends AssetDTO<Crypto> {

    public BinanceDTO(String symbol){
        super(Crypto.class, symbol);
    }

}
