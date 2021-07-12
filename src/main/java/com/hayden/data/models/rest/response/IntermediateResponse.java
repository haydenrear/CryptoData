package com.hayden.data.models.rest.response;

import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.models.shared.response.Response;
import org.reactivestreams.Publisher;

public interface IntermediateResponse<T extends ExchangeResponse> extends Response {

    Publisher<T> responses();

}
