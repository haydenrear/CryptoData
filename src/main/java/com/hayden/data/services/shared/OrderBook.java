package com.hayden.data.services.shared;

import com.hayden.data.models.shared.request.UrlFactory;
import com.hayden.data.models.shared.response.Response;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface OrderBook {
    Class<? extends Response> value();
    UrlFactory.Vals[] params() default {};
}
