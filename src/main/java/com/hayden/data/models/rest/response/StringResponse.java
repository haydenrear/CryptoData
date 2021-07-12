package com.hayden.data.models.rest.response;

import com.hayden.data.models.shared.response.ExchangeResponse;

public interface StringResponse<T extends ExchangeResponse> extends IntermediateResponse<T> {
    void setData(String data);
}