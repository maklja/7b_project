package com.sevenb.task.api.mapper;

import com.sevenb.task.api.domain.Tweet;
import com.sevenb.task.api.request.CreateTweetRequest;
import com.sevenb.task.api.response.TweetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(config = CommonMapperConfig.class)
public interface TweetMapper {

    @Mapping(target = "tweetId", source = "id")
    @Mapping(target = "tweetBody", source = "body")
    TweetResponse entityToResponse(Tweet tweet);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "body", source = "tweetBody", qualifiedByName = "trimText")
    @Mapping(target = "hashTags", qualifiedByName = "trimHashTags")
    Tweet createRequestToEntity(CreateTweetRequest createTweetRequest);

    @Named("trimHashTags")
    default Set<String> trimHashTags(final Set<String> text) {
        return text.stream().map(this::trimText).collect(Collectors.toSet());
    }

    @Named("trimText")
    default String trimText(final String text) {
        return text == null ? null : text.trim();
    }
}
