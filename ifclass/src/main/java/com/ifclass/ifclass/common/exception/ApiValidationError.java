package com.ifclass.ifclass.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiValidationError extends ApiSubError {
    public ApiValidationError(String object, String message) {
        super(object, message);
    }

    public ApiValidationError(String object, String field, Object rejectedValue, String message) {
        super(object, field, rejectedValue, message);
    }
} 