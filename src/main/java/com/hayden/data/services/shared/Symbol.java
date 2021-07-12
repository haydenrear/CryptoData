package com.hayden.data.services.shared;

import com.hayden.data.models.shared.response.Response;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface Symbol {
    Class<? extends Response> value();
}