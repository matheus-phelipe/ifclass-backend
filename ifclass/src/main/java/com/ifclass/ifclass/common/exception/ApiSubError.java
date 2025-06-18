package com.ifclass.ifclass.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ApiSubError {
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    protected ApiSubError(String object, String message) {
        this.object = object;
        this.message = message;
    }

    protected ApiSubError(String object, String field, Object rejectedValue, String message) {
        this.object = object;
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }
} 