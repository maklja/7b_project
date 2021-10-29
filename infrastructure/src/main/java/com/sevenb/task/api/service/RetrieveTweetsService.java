package com.sevenb.task.api.service;

import com.sevenb.task.api.domain.Tweet;
import com.sevenb.task.api.exceptions.TweetNotFoundException;
import com.sevenb.task.api.mapper.TweetMapper;
import com.sevenb.task.api.request.RetrieveTweetsRequest;
import com.sevenb.task.api.response.TweetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetrieveTweetsService {
    private final MongoOperations mongoOps;
    private final TweetMapper tweetMapper;

    private static Query createQuery(final RetrieveTweetsRequest retrieveTweetsRequest) {
        final var hashTags = retrieveTweetsRequest.getHashTags();
        final var usernames = retrieveTweetsRequest.getUsernames();
        final var query = new Query();

        if (!CollectionUtils.isEmpty(hashTags)) {
            query.addCriteria(Criteria.where("hashTags").in(hashTags));
        }

        if (!CollectionUtils.isEmpty(usernames)) {
            query.addCriteria(Criteria.where("createdBy").in(usernames));
        }

        return query
                .limit(retrieveTweetsRequest.getLimit())
                .skip(retrieveTweetsRequest.getOffset());
    }

    public List<TweetResponse> sliceTweets(final RetrieveTweetsRequest retrieveTweetsRequest) {
        return mongoOps.find(createQuery(retrieveTweetsRequest), Tweet.class)
                .stream()
                .map(tweetMapper::entityToResponse)
                .collect(Collectors.toList());
    }

    public TweetResponse retrieveTweetById(final String id) {
        final var tweet = mongoOps.findById(id, Tweet.class);

        if (tweet == null) {
            throw new TweetNotFoundException(id);
        }

        return tweetMapper.entityToResponse(tweet);
    }
}
