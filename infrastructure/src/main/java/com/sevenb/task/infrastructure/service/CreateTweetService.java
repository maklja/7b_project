package com.sevenb.task.infrastructure.service;

import com.sevenb.task.infrastructure.mapper.TweetMapper;
import com.sevenb.task.infrastructure.request.CreateTweetRequest;
import com.sevenb.task.infrastructure.response.TweetResponse;
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
