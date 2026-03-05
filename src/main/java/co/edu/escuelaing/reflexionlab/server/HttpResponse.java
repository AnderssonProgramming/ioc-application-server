package co.edu.escuelaing.reflexionlab.server;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds and encapsulates an HTTP response.
 * Provides a fluent API for setting status, headers, content type, and body.
 */
public class HttpResponse {

    private int statusCode;
    private String statusText;
    private String contentType;
    private String body;
    private byte[] bodyBytes;
    private final Map<String, String> headers;

    public HttpResponse() {
        this.statusCode = 200;
        this.statusText = "OK";
        this.contentType = "text/html";
        this.body = "";
        this.bodyBytes = null;
        this.headers = new LinkedHashMap<>();
    }

    public HttpResponse setStatus(int code, String text) {
        this.statusCode = code;
        this.statusText = text;
        return this;
    }

    public HttpResponse setStatus(int code) {
        this.statusCode = code;
        this.statusText = resolveStatusText(code);
        return this;
    }

    public HttpResponse setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpResponse setBody(String body) {
        this.body = body;
        this.bodyBytes = null;
        return this;
    }

    public HttpResponse setBodyBytes(byte[] bytes) {
        this.bodyBytes = bytes;
        this.body = null;
        return this;
    }

    public HttpResponse setHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public String getContentType() {
        return contentType;
    }

    public String getBody() {
        return body;
    }

    public byte[] getBodyBytes() {
        return bodyBytes;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public boolean isBinary() {
        return bodyBytes != null;
    }

    public byte[] build() {
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusText).append("\r\n");
        sb.append("Content-Type: ").append(contentType).append("\r\n");

        byte[] content = isBinary() ? bodyBytes : (body != null ? body.getBytes() : new byte[0]);
        sb.append("Content-Length: ").append(content.length).append("\r\n");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        sb.append("\r\n");

        byte[] headerBytes = sb.toString().getBytes();
        byte[] result = new byte[headerBytes.length + content.length];
        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(content, 0, result, headerBytes.length, content.length);
        return result;
    }

    private static String resolveStatusText(int code) {
        return switch (code) {
            case 200 -> "OK";
            case 301 -> "Moved Permanently";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }
}
