package com.sevenb.task.api.response;

import lombok.Data;

import java.util.Set;

@Data
public class TweetResponse {
    private String tweetId;
    private String tweetBody;
    private Set<String> hashTags;
    private String createdBy;
    private String createdAt;
}
