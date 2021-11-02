package com.sevenb.task.api.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flextrade.jfixture.JFixture;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
abstract class BaseTest {
    protected static final String TEST_USERNAME = "username@gmail.com";
    protected static final ObjectMapper mapper = new ObjectMapper();
    protected static final JFixture fixture = new JFixture();

    @Mock
    Authentication authentication;

    @Mock
    SecurityContext securityContext;

    @Autowired
    MockMvc mvc;

    @Autowired
    MongoOperations mongoOps;

    @Value("${security.authentication-header}")
    String securityHeaderName;

    @Value("${pagination.default.offset}")
    int defaultOffset;

    @Value("${pagination.default.limit}")
    int defaultLimit;

    protected static boolean compareInstantWithoutMillis(final String dateTime1, final String dateTime2) {
        final var instant1 = Instant.parse(dateTime1).truncatedTo(ChronoUnit.MILLIS);
        final var instant2 = Instant.parse(dateTime2).truncatedTo(ChronoUnit.MILLIS);

        return instant1.equals(instant2);
    }

    protected void createSecurityContextMock() {
        createSecurityContextMockWithUsername(TEST_USERNAME);
    }

    protected void createSecurityContextMockWithUsername(final String username) {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    protected void clearSecurityContextMock() {
        SecurityContextHolder.clearContext();
    }
}
