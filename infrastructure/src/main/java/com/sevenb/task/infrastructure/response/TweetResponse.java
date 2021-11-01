package com.sevenb.task.infrastructure.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TweetResponse {
    @EqualsAndHashCode.Include
    private String tweetId;
    private String tweetBody;
    private Set<String> hashTags;
    private String createdBy;
    private String createdAt;
}
