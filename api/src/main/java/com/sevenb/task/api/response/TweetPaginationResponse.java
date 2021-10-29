package com.sevenb.task.api.response;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class TweetPaginationResponse {
    private final Collection<TweetResponse> tweets;
    private final String nextPage;
}
