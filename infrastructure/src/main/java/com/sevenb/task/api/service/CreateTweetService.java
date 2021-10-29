package com.sevenb.task.api.service;

import com.sevenb.task.api.mapper.TweetMapper;
import com.sevenb.task.api.request.CreateTweetRequest;
import com.sevenb.task.api.response.TweetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTweetService {
    private final MongoOperations mongoOps;
    private final TweetMapper tweetMapper;

    public TweetResponse createNewTweet(final CreateTweetRequest createTweetRequest) {
        final var tweet = tweetMapper.createRequestToEntity(createTweetRequest);
        return tweetMapper.entityToResponse(mongoOps.insert(tweet));
    }
}
