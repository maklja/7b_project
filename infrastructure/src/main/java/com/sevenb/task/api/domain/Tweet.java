package com.sevenb.task.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Data
@Document
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tweet {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String body;
    @Indexed
    private Set<String> hashTags;

    @CreatedBy
    @Indexed
    private String createdBy;

    @CreatedDate
    private Instant createdAt;
}
