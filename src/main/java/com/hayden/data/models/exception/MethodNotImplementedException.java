package com.hayden.data.models.exception;

import com.hayden.decision.models.enums.DataType;

public class MethodNotImplementedException extends Throwable {
    public MethodNotImplementedException(String methodName, DataType dataType)
    {
        System.out.printf("Did not implement method %s to parse %s",methodName,dataType.toString());
    }
}
