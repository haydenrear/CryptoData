package com.hayden.data.services.shared;

import com.hayden.decision.models.enums.ExchangeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface Exchange {
    ExchangeEnum value();
}
