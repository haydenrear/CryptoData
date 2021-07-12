package com.hayden.data.models.rest.response;

import com.hayden.data.models.shared.response.ExchangeResponse;

public interface SymbolResponse extends ExchangeResponse {
    String getSymbol();
}
