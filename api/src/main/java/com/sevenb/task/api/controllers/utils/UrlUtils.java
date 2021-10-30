package com.sevenb.task.api.controllers.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlUtils {
    private static final String URI_TEMPLATE = "%s://%s%s%s";

    public static String createUrlFromHttpRequest(final HttpServletRequest request,
                                                  final Map<String, Collection<String>> queryParams) {
        final var scheme = request.getScheme();
        final var serverName = request.getServerName();
        final var port = extractPortFromHttpRequest(request);
        final var requestUri = request.getRequestURI();

        final var baseUrl = String.format(URI_TEMPLATE, scheme, serverName, port, requestUri);

        if (queryParams.isEmpty()) {
            return baseUrl;
        }


        return baseUrl + "?" + createQueryParamsUrl(queryParams);
    }

    public static String createQueryParamsUrl(final Map<String, Collection<String>> queryParams) {
        return queryParams.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> {
                    final var queryParamName = entry.getKey();
                    final var queryParamValues = entry.getValue();

                    if (queryParamValues.isEmpty()) {
                        return queryParamName + "=";
                    }

                    return queryParamValues
                            .stream()
                            .map(queryParamValue -> queryParamName + "=" + encodeValue(queryParamValue))
                            .collect(Collectors.joining("&"));
                })
                .collect(Collectors.joining("&"));
    }

    @SneakyThrows
    private static String encodeValue(final String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    private static String extractPortFromHttpRequest(final HttpServletRequest request) {
        return isStandardHttpPort(request) || isStandardHttpsPort(request) ? "" : ":" + request.getServerPort();
    }

    public static boolean isStandardHttpsPort(final HttpServletRequest request) {
        return "https".equals(request.getScheme()) && request.getServerPort() == 443;
    }

    public static boolean isStandardHttpPort(final HttpServletRequest request) {
        return "http".equals(request.getScheme()) && request.getServerPort() == 80;
    }
}
