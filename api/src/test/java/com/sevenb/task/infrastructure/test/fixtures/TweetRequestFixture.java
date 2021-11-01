package com.sevenb.task.infrastructure.test.fixtures;

import com.flextrade.jfixture.JFixture;
import com.sevenb.task.infrastructure.request.CreateTweetRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TweetRequestFixture {
    private static final JFixture fixture = new JFixture();
    private static final Random rnd = new Random();

    public static Set<String> createRandomHashTags() {
        final var n = Math.max(1, rnd.nextInt(10));

        return IntStream.range(0, n)
                .mapToObj(i -> createRandomHashTag())
                .collect(Collectors.toSet());
    }

    public static String createRandomHashTag() {
        return "#" + fixture.create(String.class);
    }

    public static CreateTweetRequest createTweetRequest(final String tweetBody, final Set<String> hashTags) {
        final var createTweetRequest = new CreateTweetRequest();
        createTweetRequest.setTweetBody(tweetBody);
        createTweetRequest.setHashTags(hashTags);

        return createTweetRequest;
    }

    public static CreateTweetRequest createTweetRequest(final Set<String> hashTags) {
        return createTweetRequest(fixture.create(String.class), hashTags);
    }

    public static CreateTweetRequest createValidTweetRequest() {
        return createTweetRequest(fixture.create(String.class), createRandomHashTags());
    }

    public static CreateTweetRequest createTweetRequestWithoutBody() {
        return createTweetRequest(null, createRandomHashTags());
    }

    public static CreateTweetRequest createTweetRequestWithInvalidHashTag() {
        return createTweetRequest(fixture.create(String.class), new HashSet<>(Arrays.asList("#1.", "cool")));
    }

    public static CreateTweetRequest createTweetRequestWithEmptyHashTag() {
        return createTweetRequest(fixture.create(String.class), Collections.emptySet());
    }
}
