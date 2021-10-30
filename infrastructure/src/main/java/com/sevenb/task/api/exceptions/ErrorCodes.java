package com.sevenb.task.api.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCodes {
    UNAUTHORIZED(10),
    FORBIDDEN(20),
    INVALID_INPUT(30),
    TWEET_NOT_FOUND(100);

    private final int code;
}
