package com.hayden.data.services.shared;

import com.hayden.data.models.shared.response.ExchangeRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestObject {
    Class<? extends ExchangeRequest> value();
}
