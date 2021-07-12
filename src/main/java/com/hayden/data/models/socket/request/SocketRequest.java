package com.hayden.data.models.socket.request;

import com.hayden.data.models.shared.response.ExchangeResponse;
import org.reactivestreams.Publisher;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import static com.hayden.data.services.socket.AbstractSocket.WINDOW;

public interface SocketRequest {

    default boolean nonNullData()
    {
        return true;
    }

    default boolean withinWindow(AtomicLong prevTime)
    {
        return true;
    }

    default Long getTime()
    {
        return 0L;
    }

}
