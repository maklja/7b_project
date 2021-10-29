package com.sevenb.task.api.request;

import lombok.Data;

import java.util.Set;

@Data
public class CreateTweetRequest {
    private String tweetBody;
    private Set<String> hashTags;
}
