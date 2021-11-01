package com.sevenb.task.infrastructure.test;

import com.sevenb.task.infrastructure.config.WebMvcConfig;
import com.sevenb.task.infrastructure.domain.Tweet;
import com.sevenb.task.infrastructure.exceptions.ErrorCodes;
import com.sevenb.task.infrastructure.response.ErrorResponse;
import com.sevenb.task.infrastructure.response.TweetResponse;
import com.sevenb.task.infrastructure.test.fixtures.TweetRequestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class CreateNewTweetTest extends BaseTest {

    private static final String CREATE_TWEET_ROUTE = WebMvcConfig.V1_ROUTES_PREFIX + "/tweets";

    @BeforeEach
    void setup() {
        mongoOps.dropCollection(Tweet.class);
    }

    @Test
    @DisplayName("Create new tweet with valid that should return created tweet with status code 201")
    void createTweet_ValidTweetRequest_Status201() throws Exception {
        final var tweetRequestFixture = TweetRequestFixture.createValidTweetRequest();
        final var testRequest = post(CREATE_TWEET_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(tweetRequestFixture));

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var responseTweet = mapper.readValue(response.getContentAsString(), TweetResponse.class);

        assertNotNull(responseTweet.getTweetId());
        assertNotNull(responseTweet.getCreatedAt());
        assertEquals(responseTweet.getTweetBody(), tweetRequestFixture.getTweetBody());
        assertEquals(responseTweet.getCreatedBy(), TEST_USERNAME);
        assertTrue(responseTweet.getHashTags().containsAll(tweetRequestFixture.getHashTags()));
    }

    @Test
    @DisplayName("Create new tweet with invalid tweet body will fail with status code 400")
    void createTweet_MissingTweetBody_Status400() throws Exception {
        final var tweetRequestFixture = TweetRequestFixture.createTweetRequestWithoutBody();
        final var testRequest = post(CREATE_TWEET_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(tweetRequestFixture));

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
    @DisplayName("Create new tweet with invalid hash tag will fail with status code 400")
    void createTweet_InvalidHashTag_Status400() throws Exception {
        final var tweetRequestFixture = TweetRequestFixture.createTweetRequestWithInvalidHashTag();
        final var testRequest = post(CREATE_TWEET_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(tweetRequestFixture));

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
    @DisplayName("Create new tweet without any hash tag will fail with status code 400")
    void createTweet_EmptyHashTags_Status400() throws Exception {
        final var tweetRequestFixture = TweetRequestFixture.createTweetRequestWithEmptyHashTag();
        final var testRequest = post(CREATE_TWEET_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(tweetRequestFixture));

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
    @DisplayName("Create new tweet without authorization header will fail with status code 401")
    void createTweet_UnauthorizedRequest_Status401() throws Exception {
        final var tweetRequestFixture = TweetRequestFixture.createValidTweetRequest();
        final var testRequest = post(CREATE_TWEET_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(tweetRequestFixture));

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
    @DisplayName("Create new tweet and expect response in XML format will fail with status code 406")
    void createTweet_UnsupportedResponseType_Status406() throws Exception {
        final var tweetRequestFixture = TweetRequestFixture.createValidTweetRequest();
        final var testRequest = post(CREATE_TWEET_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML) // unsupported response type
                .content(mapper.writeValueAsBytes(tweetRequestFixture));

        mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("Create new tweet with request payload as XML will fail with status code 415")
    void createTweet_UnsupportedRequestType_Status415() throws Exception {
        final var testRequest = post(CREATE_TWEET_ROUTE)
                .header(securityHeaderName, TEST_USERNAME)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_JSON)
                .content(new byte[0]);

        mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }
}