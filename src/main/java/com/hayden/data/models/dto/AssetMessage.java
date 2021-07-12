package com.hayden.data.models.dto;

import com.hayden.decision.models.shared.DTO;
import com.hayden.decision.models.shared.GetPrices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssetMessage implements DTO, Message<GetPrices> {

    GetPrices getPrices;

    @Override
    public GetPrices getPayload() {
        return this.getPrices;
    }

    @Override
    public MessageHeaders getHeaders() {
        return null;
    }
}
