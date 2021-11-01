package com.sevenb.task.infrastructure.exceptions;

import lombok.Getter;

@Getter
public class TweetNotFoundException extends EntityNotFoundException {
    private final String tweetId;

    public TweetNotFoundException(final String tweetId) {
        super(String.format("Tweet with id '%s' not found", tweetId), ErrorCodes.TWEET_NOT_FOUND.getCode());

        this.tweetId = tweetId;
    }
}
