package com.hayden.data.models.shared.response;

public interface Response {
    default boolean notEmpty()
    {
        return true;
    }
}
