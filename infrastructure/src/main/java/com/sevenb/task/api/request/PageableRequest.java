package com.sevenb.task.api.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PageableRequest {
    private final int limit;
    private final int offset;
}
