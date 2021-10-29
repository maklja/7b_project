package com.sevenb.task.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final int httpCode;
    private final int errorCode;
    private final String message;
}
