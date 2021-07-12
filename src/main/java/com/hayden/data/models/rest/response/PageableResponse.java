package com.hayden.data.models.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.models.shared.response.Response;
import reactor.core.publisher.Mono;

public abstract class PageableResponse implements Response {

    public abstract String nextVal();

    @JsonIgnore
    protected boolean finished = false;

    public String value() {
        return "";
    }

    public boolean isFinished()
    {
        return this.finished;
    }

    public void finish()
    {
        this.finished = true;
        resetVal();
    }

    protected abstract void resetVal();

    public <T extends ExchangeResponse> void combine(T exchangeResponse) { }

}
