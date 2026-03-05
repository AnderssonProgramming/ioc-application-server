package co.edu.escuelaing.reflexionlab.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Parses and encapsulates an HTTP request.
 * Extracts method, path, query parameters, headers, and body from raw HTTP input.
 */
public class HttpRequest {

    private final String method;
    private final String path;
    private final String queryString;
    private final Map<String, String> queryParams;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String rawRequest) {
        String[] lines = rawRequest.split("\r\n");
        String requestLine = lines.length > 0 ? lines[0] : "";
        String[] parts = requestLine.split(" ");

        this.method = parts.length > 0 ? parts[0].toUpperCase() : "GET";
        String fullUri = parts.length > 1 ? parts[1] : "/";

        int queryIdx = fullUri.indexOf('?');
        if (queryIdx >= 0) {
            this.path = fullUri.substring(0, queryIdx);
            this.queryString = fullUri.substring(queryIdx + 1);
        } else {
            this.path = fullUri;
            this.queryString = "";
        }

        this.queryParams = parseQueryString(this.queryString);

        Map<String, String> headerMap = new LinkedHashMap<>();
        int i = 1;
        while (i < lines.length && !lines[i].isEmpty()) {
            int colonIdx = lines[i].indexOf(':');
            if (colonIdx > 0) {
                String key = lines[i].substring(0, colonIdx).trim().toLowerCase();
                String value = lines[i].substring(colonIdx + 1).trim();
                headerMap.put(key, value);
            }
            i++;
        }
        this.headers = Collections.unmodifiableMap(headerMap);

        StringBuilder bodyBuilder = new StringBuilder();
        i++;
        while (i < lines.length) {
            bodyBuilder.append(lines[i]);
            if (i < lines.length - 1) bodyBuilder.append("\r\n");
            i++;
        }
        this.body = bodyBuilder.toString();
    }

    static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new LinkedHashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int eqIdx = pair.indexOf('=');
            if (eqIdx > 0) {
                String key = decode(pair.substring(0, eqIdx));
                String value = decode(pair.substring(eqIdx + 1));
                params.put(key, value);
            } else if (!pair.isEmpty()) {
                params.put(decode(pair), "");
            }
        }
        return params;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getQueryParam(String key) {
        return queryParams.getOrDefault(key, "");
    }

    public Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    public String getHeader(String key) {
        return headers.getOrDefault(key.toLowerCase(), "");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
