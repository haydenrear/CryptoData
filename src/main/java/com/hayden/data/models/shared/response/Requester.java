package com.hayden.data.models.shared.response;

import com.hayden.data.models.shared.request.Url;
import org.springframework.http.HttpHeaders;

public interface Requester {
    AbstractSpec retrieve(Url url, ExchangeRequest request);
    AbstractSpec retrieve(Url url);
}
