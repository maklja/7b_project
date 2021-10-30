package com.sevenb.task.api.test;

import com.sevenb.task.api.config.WebMvcConfig;
import com.sevenb.task.api.domain.Tweet;
import com.sevenb.task.api.exceptions.ErrorCodes;
import com.sevenb.task.api.response.ErrorResponse;
import com.sevenb.task.api.response.TweetResponse;
import com.sevenb.task.api.service.CreateTweetService;
import com.sevenb.task.api.test.fixtures.TweetRequestFixture;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeleteTweetTest extends BaseTest {

    private static final String DELETE_TWEET_ROUTE = WebMvcConfig.V1_ROUTES_PREFIX + "/tweets/";

    @Autowired
    CreateTweetService createTweetService;

    private TweetResponse createdTweet;

    @BeforeEach
    void setup() {
        mongoOps.dropCollection(Tweet.class);

        createSecurityContextMock();
        createdTweet = createTweetService.createNewTweet(TweetRequestFixture.createValidTweetRequest());
    }

    @AfterEach
    void cleanup() {
        clearSecurityContextMock();
    }

    @Test
    @DisplayName("Delete existing tweet with owner user will success and return status code 200")
    void deleteTweet_DeleteExistingTweet_Status200() throws Exception {
        final var testRequest = delete(DELETE_TWEET_ROUTE + createdTweet.getTweetId())
                .header(securityHeaderName, createdTweet.getCreatedBy())
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var responseTweet = mapper.readValue(response.getContentAsString(), TweetResponse.class);
        assertEquals(createdTweet.getTweetId(), responseTweet.getTweetId());
        assertEquals(createdTweet.getTweetBody(), responseTweet.getTweetBody());
        assertEquals(createdTweet.getCreatedBy(), responseTweet.getCreatedBy());
        assertTrue(createdTweet.getHashTags().containsAll(responseTweet.getHashTags()));
        assertTrue(compareInstantWithoutMillis(createdTweet.getCreatedAt(), responseTweet.getCreatedAt()));
    }

    @Test
    @DisplayName("Delete non existing tweet will fail and return status code 404")
    void deleteTweet_DeleteNonExistingTweet_Status404() throws Exception {
        final var nonExistingTweetId = ObjectId.get().toString();
        final var testRequest = delete(DELETE_TWEET_ROUTE + nonExistingTweetId)
                .header(securityHeaderName, TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var errorResponse = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getHttpCode());
        assertEquals(ErrorCodes.TWEET_NOT_FOUND.getCode(), errorResponse.getErrorCode());
        assertNotNull(errorResponse.getMessage());
    }

    @Test
    @DisplayName("Delete existing tweet without authorization header will fail with status code 401")
    void deleteTweet_UnauthorizedRequest_Status401() throws Exception {
        final var testRequest = delete(DELETE_TWEET_ROUTE + createdTweet.getTweetId())
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
    @DisplayName("Delete existing tweet with user that is not owner will fail with status code 403")
    void deleteTweet_NoPermissions_Status403() throws Exception {
        final var otherUsername = fixture.create(String.class);
        final var testRequest = delete(DELETE_TWEET_ROUTE + createdTweet.getTweetId())
                .header(securityHeaderName, otherUsername)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        final var errorResponse = mapper.readValue(response.getContentAsString(), ErrorResponse.class);
        assertEquals(HttpStatus.FORBIDDEN.value(), errorResponse.getHttpCode());
        assertEquals(ErrorCodes.FORBIDDEN.getCode(), errorResponse.getErrorCode());
        assertNotNull(errorResponse.getMessage());
    }

    @Test
    @DisplayName("Delete tweet and expect response in XML format will fail with status code 406")
    void deleteTweet_UnsupportedResponseType_Status406() throws Exception {
        final var testRequest = delete(DELETE_TWEET_ROUTE + createdTweet.getTweetId())
                .header(securityHeaderName, createdTweet.getCreatedBy())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML); // unsupported response type

        mvc.perform(testRequest)
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }
}
