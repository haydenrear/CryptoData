package com.hayden.data.models.rest;

import com.hayden.data.models.rest.request.WebClientRequester;
import com.hayden.data.models.rest.response.IntermediateResponse;
import com.hayden.data.models.rest.response.PageableResponse;
import com.hayden.data.models.shared.request.Url;
import com.hayden.data.models.shared.response.Adapter;
import com.hayden.data.models.shared.response.ExchangeResponse;
import com.hayden.data.services.rest.APIDataService;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor
@Component
@Scope("prototype")
public class PageableAdapter<TO extends ExchangeResponse, FROM extends PageableResponse> extends Adapter<TO, FROM> {

    @Value("${api.num.pages}")
    private int MAX_SYMBOLS;

    public PageableAdapter(Class<FROM> toObj, Class<TO> fromClzz, WebClientRequester builder, ApplicationContext ctx)
    {
        super(toObj, fromClzz, builder, ctx);
    }

    public Publisher<TO> adapt(Url url)
    {
        var toReturn = pageableResponse(fromClzz, url).flatMap(response -> {
            if (IntermediateResponse.class.isAssignableFrom(fromClzz)) {
                return combineIntermediate(response);
            } else if (ExchangeResponse.class.isAssignableFrom(fromClzz) && fromClzz.isAssignableFrom(toClass)) {
                return combineExchange(response);
            }  else {
                return combine(response);
            }
        });
        if(!ExchangeResponse.class.isAssignableFrom(fromClzz) && !fromClzz.isAssignableFrom(toClass) && !IntermediateResponse.class.isAssignableFrom(fromClzz))
            return toReturn.last();
        return toReturn;
    }

    public Flux<FROM> pageableResponse(
            Class<FROM> clzz,
            Url url
    )
    {
        return builder.retrieve(url)
                .single(clzz)
                .expand(pageableResponse -> pageableResponse(
                        clzz,
                        pageableUrl(
                                pageableResponse.nextVal(),
                                url
                        ),
                        pageableResponse
                ));

    }

    int num = 0;

    private Flux<FROM> pageableResponse(Class<FROM> clzz, Url pageableUrl, FROM prev)
    {
        ++num;
        if(!prev.notEmpty() || prev.isFinished() || num > MAX_SYMBOLS)
        {
            return Flux.empty();
        }
        else {
            return builder.retrieve(pageableUrl)
                    .single(clzz)
                    .onErrorStop()
                    .expand(pageableResponse -> pageableResponse(
                            clzz,
                            pageableUrl(
                                    pageableResponse.nextVal(),
                                    pageableUrl
                            ),
                            pageableResponse
                    ))
                    .retryWhen(APIDataService.rateLimitingException());
        }
    }

    public Url pageableUrl(
            String nextVal,
            Url prevUrl
    )
    {
        if(!nextVal.equals("null") && nextVal != null && !nextVal.equals(""))
            return prevUrl.updatePage(nextVal);
        return prevUrl;
    }

    protected Publisher<TO> combineIntermediate(FROM pageable)
    {
        return Flux.just(pageable)
                .map(val -> (IntermediateResponse<TO>) val)
                .flatMap(IntermediateResponse::responses);
    }

    protected Publisher<TO> combineExchange(FROM pageable)
    {
        return Flux.just(pageable)
                .map(FROM -> toClass.cast(FROM));
    }

    protected Publisher<TO> combine(FROM pageableResponses)
    {
        pageableResponses.combine(this.TOObject);
        return Mono.just(this.TOObject);
    }
}
