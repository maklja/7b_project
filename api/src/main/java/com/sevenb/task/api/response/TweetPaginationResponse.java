package com.sevenb.task.api.response;

import com.sevenb.task.infrastructure.response.TweetResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetPaginationResponse {
    private Collection<TweetResponse> tweets;
    private String nextPage;
}
