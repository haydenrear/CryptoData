package com.hayden.data.models.rest.response;

import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.shared.AssetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinanceSymbol implements SymbolResponse {

    private String symbol;
    private String pair;
    private String contractType;
    private String deliveryDate;
    private String onboardDate;
    private String status;
    private String maintMarginPercent;
    private String requiredMarginPercent;
    private String baseAsset;
    private String quoteAsset;
    private String marginAsset;
    private String pricePrecision;
    private String quantityPrecision;
    private String baseAssetPrecision;
    private Integer quotePrecision;
    private String underlyingType;
    private List<String> underlyingSubType;
    private Integer settlePlan;
    private Double triggerProtect;
    private List<Map<String, Object>> filters;
    private List<String> orderType;
    private List<String> timeInForce;

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public <T extends Asset> AssetDTO<T> visit(AssetDTO<T> dto) {
        dto.setSymbol(this.symbol);
        return dto;
    }

}
