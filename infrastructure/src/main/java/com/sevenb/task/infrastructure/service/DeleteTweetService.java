package com.sevenb.task.infrastructure.service;

import com.sevenb.task.infrastructure.domain.Tweet;
import com.sevenb.task.infrastructure.exceptions.TweetNotFoundException;
import com.sevenb.task.infrastructure.mapper.TweetMapper;
import com.sevenb.task.infrastructure.response.TweetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTweetService {
    private final MongoOperations mongoOps;
    private final TweetMapper tweetMapper;

    public TweetResponse deleteById(final String tweetId) {
        final var deleteTweetQuery = Query.query(Criteria.where("id").is(tweetId));
        final var deletedTweet = mongoOps.findAndRemove(deleteTweetQuery, Tweet.class);

        if (deletedTweet == null) {
            throw new TweetNotFoundException(tweetId);
        }

        return tweetMapper.entityToResponse(deletedTweet);
    }
}
