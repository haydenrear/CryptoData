package com.hayden.data.models.rest.response;

import com.hayden.data.models.shared.request.UrlFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UrlParams {
    UrlFactory.Vals[] value();
}
