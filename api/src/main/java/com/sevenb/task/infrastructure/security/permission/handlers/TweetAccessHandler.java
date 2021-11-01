package com.sevenb.task.infrastructure.security.permission.handlers;

import com.sevenb.task.infrastructure.security.permission.ResourceTypes;
import com.sevenb.task.infrastructure.security.principal.UserPrincipal;
import com.sevenb.task.infrastructure.service.RetrieveTweetsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@RequiredArgsConstructor
@Component
public class TweetAccessHandler implements AccessHandler {
    private final RetrieveTweetsService retrieveTweetsService;

    public boolean handle(final Authentication authentication,
                          final Serializable entityId,
                          final String permission) {
        if (!authentication.isAuthenticated()) {
            return false;
        }

        final var principal = (UserPrincipal) authentication.getPrincipal();
        final var tweet = retrieveTweetsService.retrieveTweetById(entityId.toString());

        return principal.getUsername().equals(tweet.getCreatedBy());
    }

    @Override
    public boolean canHandle(final String resourceType) {
        return ResourceTypes.TWEET_RESOURCE.equals(resourceType);
    }
}
