package com.hayden.data.services.socket;

import com.hayden.data.models.shared.response.Response;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OrderBookWS {
    Class<? extends Response> value();
}
