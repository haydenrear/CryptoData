package com.hayden.data.services.shared;

import com.hayden.decision.models.asset.Asset;
import com.hayden.decision.models.asset.Crypto;
import com.hayden.decision.models.shared.AssetDTO;
import org.springframework.core.ParameterizedTypeReference;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface AssetClass {
    Class<? extends AssetDTO<? extends Asset>> value();
}
