package com.sevenb.task.seed;

import com.sevenb.task.infrastructure.domain.Tweet;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@ComponentScan("com.sevenb.task.*")
@SpringBootApplication
@RequiredArgsConstructor
public class DataSeederConsoleApplication implements CommandLineRunner {
    private static final int BATCH_CHUNK_SIZE = 500;
    private static final int MAX_DATA_SEED = 10_000;
    private static final Random rnd = new Random();
    private static final List<String> usernames = readFile("usernames.txt");
    private static final List<String> hashTags = readFile("hash_tags.txt");
    private static final List<String> tweetBody = readFile("tweet_body.txt");
    private final MongoOperations mongoOps;

    private static Tweet createRandomTweet() {
        final var tweet = new Tweet();
        tweet.setBody(tweetBody.get(rnd.nextInt(tweetBody.size() - 1)));
        tweet.setCreatedBy(usernames.get(rnd.nextInt(usernames.size() - 1)));

        final var maxHashTags = Math.max(1, rnd.nextInt(10));
        tweet.setHashTags(IntStream.range(0, maxHashTags)
                .mapToObj(i -> hashTags.get(rnd.nextInt(hashTags.size() - 1)))
                .collect(Collectors.toSet())
        );

        return tweet;
    }

    @SneakyThrows
    private static List<String> readFile(final String fileName) {
        final var fileStream = DataSeederConsoleApplication.class.getClassLoader().getResourceAsStream(fileName);
        if (fileStream == null) {
            return Collections.emptyList();
        }

        final var reader = new BufferedReader(new InputStreamReader(fileStream));
        ;
        final var lines = new ArrayList<String>(50);
        while (reader.readLine() != null) {
            lines.add(reader.readLine());
        }

        return lines;
    }

    public static void main(final String[] args) {
        SpringApplication.run(DataSeederConsoleApplication.class, args).close();
    }

    @Override
    public void run(final String... args) {
        mongoOps.dropCollection(Tweet.class);

        final var tweets = IntStream.range(0, MAX_DATA_SEED)
                .mapToObj(i -> createRandomTweet())
                .collect(Collectors.toList());

        for (var i = 0; i < MAX_DATA_SEED; i += BATCH_CHUNK_SIZE) {
            final var toIndex = Math.min(tweets.size(), i + BATCH_CHUNK_SIZE);
            final var curChunk = tweets.subList(i, toIndex);
            mongoOps.bulkOps(BulkOperations.BulkMode.UNORDERED, Tweet.class).insert(curChunk).execute();
        }
    }
}
