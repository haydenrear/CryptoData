package com.hayden.data.models.dto;


import com.hayden.decision.models.asset.Crypto;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@EqualsAndHashCode(callSuper = false)
@Component(value = "messariDTO")
@Scope("prototype")
@Data
@NoArgsConstructor
public class MessariDTO extends AssetDTO<Crypto> {

    public MessariDTO(String symbol){
        super(Crypto.class, symbol);
    }

}
