package com.hayden.data.models.dto;

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
public class CoinGeckoDTO extends AssetDTO<Crypto> {


    public CoinGeckoDTO(String symbol){
        super(Crypto.class, symbol);
    }


}
