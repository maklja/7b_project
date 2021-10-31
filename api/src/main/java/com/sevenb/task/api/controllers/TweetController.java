package com.sevenb.task.api.controllers;

import com.sevenb.task.api.controllers.utils.UrlUtils;
import com.sevenb.task.api.controllers.version.ApiV1;
import com.sevenb.task.api.request.CreateTweetRequest;
import com.sevenb.task.api.request.RetrieveTweetsRequest;
import com.sevenb.task.api.response.ErrorResponse;
import com.sevenb.task.api.response.TweetPaginationResponse;
import com.sevenb.task.api.response.TweetResponse;
import com.sevenb.task.api.service.CreateTweetService;
import com.sevenb.task.api.service.DeleteTweetService;
import com.sevenb.task.api.service.RetrieveTweetsService;
import com.sevenb.task.api.validation.HashTag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@ApiV1
@RequestMapping(value = "tweets", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class TweetController {
    private final RetrieveTweetsService retrieveTweetsService;
    private final CreateTweetService createTweetService;
    private final DeleteTweetService deleteTweetService;

    @Operation(summary = "Retrieve all tweet by filter", description = "Retrieve all tweet by filter. Supported filters are hashTag, username, offset and limit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tweets are successfully filtered and acquired"),
            @ApiResponse(responseCode = "400", description = "Invalid filter input values", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "406", description = "Unsupported response type", content = @Content(schema = @Schema())),
    })
    @GetMapping
    public TweetPaginationResponse retrieveTweets(
            final HttpServletRequest request,
            @RequestParam(value = "hashTag", required = false) final Set<@HashTag String> hashTags,
            @RequestParam(value = "username", required = false) final Set<@NotBlank String> usernames,
            @RequestParam(defaultValue = "${pagination.default.limit}", required = false) @Min(1) @Max(200) final int limit,
            @RequestParam(defaultValue = "${pagination.default.offset}", required = false) @Min(0) final int offset
    ) {
        final var retrieveTweetsRequest = RetrieveTweetsRequest
                .builder()
                .hashTags(hashTags)
                .usernames(usernames)
                .limit(limit)
                .offset(offset)
                .build();
        final var tweets = retrieveTweetsService.sliceTweets(retrieveTweetsRequest);

        final var nextPageQueryParams = new HashMap<String, Collection<String>>();
        nextPageQueryParams.put("hashTag", hashTags);
        nextPageQueryParams.put("username", usernames);
        nextPageQueryParams.put("limit", Collections.singleton(Integer.toString(limit)));
        nextPageQueryParams.put("offset", Collections.singleton(Integer.toString(offset + tweets.size())));
        final var nextPageUrl = UrlUtils.createUrlFromHttpRequest(request, nextPageQueryParams);

        return new TweetPaginationResponse(tweets, nextPageUrl);
    }

    @Operation(summary = "Create a new tweet", description = "Create a new tweet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tweets is successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input values", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "406", description = "Unsupported response type", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "415", description = "Unsupported request type", content = @Content(schema = @Schema())),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponse createTweet(@Valid @RequestBody final CreateTweetRequest createTweetRequest) {
        return createTweetService.createNewTweet(createTweetRequest);
    }


    @Operation(summary = "Delete a tweet", description = "Delete a tweet. Note that only owner can delete a tweet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tweets is successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "User has no permissions to delete a tweet", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Tweet with required id was not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "406", description = "Unsupported response type", content = @Content(schema = @Schema())),
    })
    @DeleteMapping("/{tweetId}")
    @PreAuthorize("hasPermission(#tweetId, 'tweet', 'delete')")
    public TweetResponse deleteTweet(@PathVariable final String tweetId) {
        return deleteTweetService.deleteById(tweetId);
    }
}
