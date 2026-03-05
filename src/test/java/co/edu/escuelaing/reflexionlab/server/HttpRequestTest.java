package co.edu.escuelaing.reflexionlab.server;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;

public class HttpRequestTest {

    @Test
    public void testParseSimpleGetRequest() {
        String raw = "GET /hello HTTP/1.1\r\nHost: localhost\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("GET", req.getMethod());
        assertEquals("/hello", req.getPath());
    }

    @Test
    public void testParseQueryParameters() {
        String raw = "GET /greeting?name=Pedro&age=25 HTTP/1.1\r\nHost: localhost\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("/greeting", req.getPath());
        assertEquals("Pedro", req.getQueryParam("name"));
        assertEquals("25", req.getQueryParam("age"));
    }

    @Test
    public void testParseQueryStringWithEncoding() {
        String raw = "GET /search?q=hello+world HTTP/1.1\r\nHost: localhost\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("hello world", req.getQueryParam("q"));
    }

    @Test
    public void testParsePercentEncodedQuery() {
        String raw = "GET /search?email=user%40example.com HTTP/1.1\r\nHost: localhost\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("user@example.com", req.getQueryParam("email"));
    }

    @Test
    public void testMissingQueryParamReturnsEmpty() {
        String raw = "GET /hello HTTP/1.1\r\nHost: localhost\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("", req.getQueryParam("nonexistent"));
    }

    @Test
    public void testEmptyQueryString() {
        String raw = "GET /hello HTTP/1.1\r\nHost: localhost\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("", req.getQueryString());
        assertTrue(req.getQueryParams().isEmpty());
    }

    @Test
    public void testParseHeaders() {
        String raw = "GET / HTTP/1.1\r\nHost: localhost\r\nAccept: text/html\r\nUser-Agent: TestBot\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("localhost", req.getHeader("Host"));
        assertEquals("text/html", req.getHeader("Accept"));
        assertEquals("TestBot", req.getHeader("User-Agent"));
    }

    @Test
    public void testHeadersCaseInsensitive() {
        String raw = "GET / HTTP/1.1\r\nContent-Type: application/json\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("application/json", req.getHeader("content-type"));
        assertEquals("application/json", req.getHeader("CONTENT-TYPE"));
    }

    @Test
    public void testMissingHeaderReturnsEmpty() {
        String raw = "GET / HTTP/1.1\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("", req.getHeader("X-Custom"));
    }

    @Test
    public void testRootPath() {
        String raw = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("/", req.getPath());
    }

    @Test
    public void testQueryParamsAreImmutable() {
        String raw = "GET /test?a=1 HTTP/1.1\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        Map<String, String> params = req.getQueryParams();
        try {
            params.put("b", "2");
            fail("Should not be able to modify query params");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testParseQueryStringStatic() {
        Map<String, String> params = HttpRequest.parseQueryString("name=John&city=Bogota");
        assertEquals("John", params.get("name"));
        assertEquals("Bogota", params.get("city"));
    }

    @Test
    public void testParseEmptyQueryStringStatic() {
        Map<String, String> params = HttpRequest.parseQueryString("");
        assertTrue(params.isEmpty());
    }

    @Test
    public void testParseNullQueryStringStatic() {
        Map<String, String> params = HttpRequest.parseQueryString(null);
        assertTrue(params.isEmpty());
    }

    @Test
    public void testMultipleQueryParams() {
        String raw = "GET /search?q=java&page=1&limit=10&sort=asc HTTP/1.1\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("java", req.getQueryParam("q"));
        assertEquals("1", req.getQueryParam("page"));
        assertEquals("10", req.getQueryParam("limit"));
        assertEquals("asc", req.getQueryParam("sort"));
    }

    @Test
    public void testPostMethod() {
        String raw = "POST /submit HTTP/1.1\r\nHost: localhost\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("POST", req.getMethod());
    }

    @Test
    public void testBodyParsing() {
        String raw = "POST /submit HTTP/1.1\r\nHost: localhost\r\n\r\n{\"name\":\"test\"}";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("{\"name\":\"test\"}", req.getBody());
    }

    @Test
    public void testQueryParamKeyWithoutValue() {
        Map<String, String> params = HttpRequest.parseQueryString("flag");
        assertEquals("", params.get("flag"));
    }

    @Test
    public void testPathWithDeepNesting() {
        String raw = "GET /api/v1/users/profile HTTP/1.1\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("/api/v1/users/profile", req.getPath());
    }

    @Test
    public void testQueryStringPreserved() {
        String raw = "GET /test?a=1&b=2 HTTP/1.1\r\n\r\n";
        HttpRequest req = new HttpRequest(raw);
        assertEquals("a=1&b=2", req.getQueryString());
    }
}
