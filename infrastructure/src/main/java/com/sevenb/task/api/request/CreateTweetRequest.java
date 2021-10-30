package com.sevenb.task.api.request;

import com.sevenb.task.api.validation.HashTag;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
public class CreateTweetRequest {
    @NotBlank(message = "Tweet body can't be empty")
    private String tweetBody;

    @NotEmpty(message = "At least one hash tag is required")
    private Set<@HashTag String> hashTags;
}
