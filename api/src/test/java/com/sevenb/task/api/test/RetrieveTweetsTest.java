package com.sevenb.task.api.test;

import com.sevenb.task.api.config.WebMvcConfig;
import com.sevenb.task.api.controllers.utils.UrlUtils;
import com.sevenb.task.api.domain.Tweet;
import com.sevenb.task.api.exceptions.ErrorCodes;
import com.sevenb.task.api.response.ErrorResponse;
import com.sevenb.task.api.response.TweetPaginationResponse;
import com.sevenb.task.api.response.TweetResponse;
import com.sevenb.task.api.service.CreateTweetService;
import com.sevenb.task.api.test.fixtures.TweetRequestFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RetrieveTweetsTest extends BaseTest {

    private static final String RETRIEVE_TWEETS_ROUTE = WebMvcConfig.V1_ROUTES_PREFIX + "/tweets";
    private static final String NEXT_PAGE_BASE_URL = "http://localhost" + RETRIEVE_TWEETS_ROUTE;
    private static final String FUN_USERNAME = fixture.create(String.class);
    private static final String DOGS_CATS_USERNAME = fixture.create(String.class);

    @Autowired
    CreateTweetService createTweetService;

    private TweetResponse dogsFunTweet;
    private TweetResponse catsFunTweet;
    private TweetResponse catsDogsTweet;

    @BeforeEach
    void setup() {
        mongoOps.dropCollection(Tweet.class);

        createSecurityContextMockWithUsername(FUN_USERNAME);
        dogsFunTweet = createTweetService.createNewTweet(TweetRequestFixture.createTweetRequest(
                new HashSet<>(Arrays.asList("#dogs", "#fun"))
        ));

        createSecurityContextMockWithUsername(FUN_USERNAME);
        catsFunTweet = createTweetService.createNewTweet(TweetRequestFixture.createTweetRequest(
                new HashSet<>(Arrays.asList("#cats", "#fun"))
        ));

        createSecurityContextMockWithUsername(DOGS_CATS_USERNAME);
        catsDogsTweet = createTweetService.createNewTweet(TweetRequestFixture.createTweetRequest(
                new HashSet<>(Arrays.asList("#cats", "#dogs"))
        ));
    }

    @AfterEach
    void cleanup() {
        clearSecurityContextMock();
    }

    private void assertTweets(final TweetResponse expectedTweet, final TweetResponse actualTweet) {
        assertNotNull(actualTweet);
        assertEquals(expectedTweet.getTweetId(), actualTweet.getTweetId());
        assertEquals(expectedTweet.getTweetBody(), actualTweet.getTweetBody());
        assertEquals(expectedTweet.getCreatedBy(), actualTweet.getCreatedBy());
        assertTrue(expectedTweet.getHashTags().containsAll(actualTweet.getHashTags()));
        assertTrue(compareInstantWithoutMillis(expectedTweet.getCreatedAt(), actualTweet.getCreatedAt()));
    }

    private void assertNextPageUrl(final String actualNextPageUrl,
                                   final Map<String, Collection<String>> queryParams) {
        final var queryParamsStr = UrlUtils.createQueryParamsUrl(queryParams);
        final var expectedNextPageUrl = NEXT_PAGE_BASE_URL + "?" + queryParamsStr;
        assertEquals(expectedNextPageUrl, actualNextPageUrl);
    }

    @Test
    @DisplayName("Retrieve all tweets without any filter will success and return status 200")
    void retrieveTweets_RetrieveAllTweets_Status200() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var tweetPaginationResponse = mapper.readValue(response.getContentAsString(), TweetPaginationResponse.class);
        assertNextPageUrl(tweetPaginationResponse.getNextPage(), new HashMap<>() {{
            put("offset", Collections.singleton("3"));
            put("limit", Collections.singleton(Integer.toString(defaultLimit)));
        }});
        final var tweets = tweetPaginationResponse.getTweets();
        assertEquals(3, tweets.size());
        assertTweets(dogsFunTweet, tweets.stream()
                .filter(tweet -> tweet.equals(dogsFunTweet))
                .findFirst()
                .orElse(null));

        assertTweets(catsDogsTweet, tweets.stream()
                .filter(tweet -> tweet.equals(catsDogsTweet))
                .findFirst()
                .orElse(null));

        assertTweets(catsFunTweet, tweets.stream()
                .filter(tweet -> tweet.equals(catsFunTweet))
                .findFirst()
                .orElse(null));
    }


    @Test
    @DisplayName("Retrieve all tweets from specific username will success and return status 200")
    void retrieveTweets_FilterByUsername_Status200() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .queryParam("username", FUN_USERNAME)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var tweetPaginationResponse = mapper.readValue(response.getContentAsString(), TweetPaginationResponse.class);
        assertNextPageUrl(tweetPaginationResponse.getNextPage(), new HashMap<>() {{
            put("username", Collections.singleton(FUN_USERNAME));
            put("offset", Collections.singleton("2"));
            put("limit", Collections.singleton(Integer.toString(defaultLimit)));
        }});
        final var tweets = tweetPaginationResponse.getTweets();
        assertEquals(2, tweets.size());
        assertTweets(dogsFunTweet, tweets.stream()
                .filter(tweet -> tweet.equals(dogsFunTweet))
                .findFirst()
                .orElse(null));

        assertTweets(catsFunTweet, tweets.stream()
                .filter(tweet -> tweet.equals(catsFunTweet))
                .findFirst()
                .orElse(null));
    }

    @Test
    @DisplayName("Retrieve all tweets by specific hash tag will success and return status 200")
    void retrieveTweets_FilterByHashTag_Status200() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .queryParam("hashTag", "#dogs")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var tweetPaginationResponse = mapper.readValue(response.getContentAsString(), TweetPaginationResponse.class);
        assertNextPageUrl(tweetPaginationResponse.getNextPage(), new HashMap<>() {{
            put("hashTag", Collections.singleton("#dogs"));
            put("offset", Collections.singleton("2"));
            put("limit", Collections.singleton(Integer.toString(defaultLimit)));
        }});
        final var tweets = tweetPaginationResponse.getTweets();
        assertEquals(2, tweets.size());
        assertTweets(dogsFunTweet, tweets.stream()
                .filter(tweet -> tweet.equals(dogsFunTweet))
                .findFirst()
                .orElse(null));

        assertTweets(catsDogsTweet, tweets.stream()
                .filter(tweet -> tweet.equals(catsDogsTweet))
                .findFirst()
                .orElse(null));
    }

    @Test
    @DisplayName("Retrieve all tweets by username and hash tags will success and return status 200")
    void retrieveTweets_FilterByUsernameAndHashTag_Status200() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .queryParam("username", FUN_USERNAME)
                .queryParam("hashTag", "#dogs")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var tweetPaginationResponse = mapper.readValue(response.getContentAsString(), TweetPaginationResponse.class);
        assertNextPageUrl(tweetPaginationResponse.getNextPage(), new HashMap<>() {{
            put("hashTag", Collections.singleton("#dogs"));
            put("username", Collections.singleton(FUN_USERNAME));
            put("offset", Collections.singleton("1"));
            put("limit", Collections.singleton(Integer.toString(defaultLimit)));
        }});
        final var tweets = tweetPaginationResponse.getTweets();
        assertEquals(1, tweets.size());
        assertTweets(dogsFunTweet, tweets.stream()
                .filter(tweet -> tweet.equals(dogsFunTweet))
                .findFirst()
                .orElse(null));
    }

    @Test
    @DisplayName("Retrieve all tweets by multiple usernames and hash tags will success and return status 200")
    void retrieveTweets_FilterByMultipleUsernameAndHashTag_Status200() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .queryParam("username", FUN_USERNAME)
                .queryParam("username", DOGS_CATS_USERNAME)
                .queryParam("hashTag", "#dogs")
                .queryParam("hashTag", "#cats")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var tweetPaginationResponse = mapper.readValue(response.getContentAsString(), TweetPaginationResponse.class);
        assertNextPageUrl(tweetPaginationResponse.getNextPage(), new HashMap<>() {{
            put("hashTag", Arrays.asList("#dogs", "#cats"));
            put("username", Arrays.asList(FUN_USERNAME, DOGS_CATS_USERNAME));
            put("offset", Collections.singleton("3"));
            put("limit", Collections.singleton(Integer.toString(defaultLimit)));
        }});
        final var tweets = tweetPaginationResponse.getTweets();
        assertEquals(3, tweets.size());
        assertTweets(dogsFunTweet, tweets.stream()
                .filter(tweet -> tweet.equals(dogsFunTweet))
                .findFirst()
                .orElse(null));

        assertTweets(catsDogsTweet, tweets.stream()
                .filter(tweet -> tweet.equals(catsDogsTweet))
                .findFirst()
                .orElse(null));

        assertTweets(catsFunTweet, tweets.stream()
                .filter(tweet -> tweet.equals(catsFunTweet))
                .findFirst()
                .orElse(null));
    }

    @Test
    @DisplayName("Retrieve all tweets with offset 0 and limit 2 will success and return status 200")
    void retrieveTweets_SetOffset0AndLimit2_Status200() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .queryParam("offset", Integer.toString(0))
                .queryParam("limit", Integer.toString(2))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var tweetPaginationResponse = mapper.readValue(response.getContentAsString(), TweetPaginationResponse.class);
        assertNextPageUrl(tweetPaginationResponse.getNextPage(), new HashMap<>() {{
            put("offset", Collections.singleton("2"));
            put("limit", Collections.singleton("2"));
        }});
        final var tweets = tweetPaginationResponse.getTweets();
        assertEquals(2, tweets.size());
    }

    @Test
    @DisplayName("Retrieve all tweets with offset 2 and limit 10 will success and return status 200")
    void retrieveTweets_SetOffset2AndLimit10_Status200() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .queryParam("offset", Integer.toString(2))
                .queryParam("limit", Integer.toString(10))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var tweetPaginationResponse = mapper.readValue(response.getContentAsString(), TweetPaginationResponse.class);
        assertNextPageUrl(tweetPaginationResponse.getNextPage(), new HashMap<>() {{
            put("offset", Collections.singleton("3"));
            put("limit", Collections.singleton("10"));
        }});
        final var tweets = tweetPaginationResponse.getTweets();
        assertEquals(1, tweets.size());
    }

    @Test
    @DisplayName("Retrieve all tweets with offset 10 and limit 10 will success and return status 200")
    void retrieveTweets_SetOffset10AndLimit10_Status200() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .queryParam("offset", Integer.toString(10))
                .queryParam("limit", Integer.toString(10))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var tweetPaginationResponse = mapper.readValue(response.getContentAsString(), TweetPaginationResponse.class);
        assertNextPageUrl(tweetPaginationResponse.getNextPage(), new HashMap<>() {{
            put("offset", Collections.singleton("10"));
            put("limit", Collections.singleton("10"));
        }});
        final var tweets = tweetPaginationResponse.getTweets();
        assertEquals(0, tweets.size());
    }

    @Test
    @DisplayName("Retrieve all tweets with invalid offset and limit values will fail with status code 400")
    void retrieveTweets_InvalidOffsetAndLimit_Status400() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .queryParam("offset", "invalid")
                .queryParam("limit", Integer.toString(-10))
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var errorResponse = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getHttpCode());
        assertEquals(ErrorCodes.INVALID_INPUT.getCode(), errorResponse.getErrorCode());
        assertNotNull(errorResponse.getMessage());
    }

    @Test
    @DisplayName("Retrieve all tweets with invalid hash tag value will fail with status code 400")
    void retrieveTweets_InvalidHashTagValue_Status400() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .queryParam("hashTag", "invalid")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var errorResponse = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getHttpCode());
        assertEquals(ErrorCodes.INVALID_INPUT.getCode(), errorResponse.getErrorCode());
        assertNotNull(errorResponse.getMessage());
    }

    @Test
    @DisplayName("Retrieve all tweets with invalid username value will fail with status code 400")
    void retrieveTweets_InvalidUsernameValue_Status400() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .queryParam("username", DOGS_CATS_USERNAME)
                .queryParam("username", "")
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var errorResponse = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getHttpCode());
        assertEquals(ErrorCodes.INVALID_INPUT.getCode(), errorResponse.getErrorCode());
        assertNotNull(errorResponse.getMessage());
    }

    @Test
    @DisplayName("Retrieve all tweets without authentication header will fail with status code 401")
    void retrieveTweets_UnauthorizedRequest_Status401() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var errorResponse = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.getHttpCode());
        assertEquals(ErrorCodes.UNAUTHORIZED.getCode(), errorResponse.getErrorCode());
        assertNotNull(errorResponse.getMessage());
    }

    @Test
    @DisplayName("Retrieve all tweets and expect response in XML format will fail with status code 406")
    void retrieveTweets_UnsupportedResponseType_Status406() throws Exception {
        final var testRequest = get(RETRIEVE_TWEETS_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML);

        mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }
}
