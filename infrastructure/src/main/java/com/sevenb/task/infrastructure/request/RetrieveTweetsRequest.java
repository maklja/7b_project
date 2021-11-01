package com.sevenb.task.infrastructure.request;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Getter
@SuperBuilder
public class RetrieveTweetsRequest extends PageableRequest {
    private final Set<String> hashTags;
    private final Set<String> usernames;
}
