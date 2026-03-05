package co.edu.escuelaing.reflexionlab.server;

import org.junit.Test;
import static org.junit.Assert.*;

public class HttpResponseTest {

    @Test
    public void testDefaultValues() {
        HttpResponse resp = new HttpResponse();
        assertEquals(200, resp.getStatusCode());
        assertEquals("OK", resp.getStatusText());
        assertEquals("text/html", resp.getContentType());
        assertEquals("", resp.getBody());
        assertFalse(resp.isBinary());
    }

    @Test
    public void testSetStatus() {
        HttpResponse resp = new HttpResponse().setStatus(404);
        assertEquals(404, resp.getStatusCode());
        assertEquals("Not Found", resp.getStatusText());
    }

    @Test
    public void testSetStatusWithText() {
        HttpResponse resp = new HttpResponse().setStatus(201, "Created");
        assertEquals(201, resp.getStatusCode());
        assertEquals("Created", resp.getStatusText());
    }

    @Test
    public void testSetContentType() {
        HttpResponse resp = new HttpResponse().setContentType("application/json");
        assertEquals("application/json", resp.getContentType());
    }

    @Test
    public void testSetBody() {
        HttpResponse resp = new HttpResponse().setBody("Hello World");
        assertEquals("Hello World", resp.getBody());
        assertFalse(resp.isBinary());
    }

    @Test
    public void testSetBodyBytes() {
        byte[] data = new byte[]{1, 2, 3};
        HttpResponse resp = new HttpResponse().setBodyBytes(data);
        assertArrayEquals(data, resp.getBodyBytes());
        assertTrue(resp.isBinary());
        assertNull(resp.getBody());
    }

    @Test
    public void testFluentApi() {
        HttpResponse resp = new HttpResponse()
                .setStatus(200)
                .setContentType("text/plain")
                .setBody("test");
        assertEquals(200, resp.getStatusCode());
        assertEquals("text/plain", resp.getContentType());
        assertEquals("test", resp.getBody());
    }

    @Test
    public void testBuildContainsStatusLine() {
        byte[] result = new HttpResponse().setStatus(200).setBody("OK").build();
        String response = new String(result);
        assertTrue(response.startsWith("HTTP/1.1 200 OK"));
    }

    @Test
    public void testBuildContainsContentType() {
        byte[] result = new HttpResponse().setContentType("text/plain").setBody("test").build();
        String response = new String(result);
        assertTrue(response.contains("Content-Type: text/plain"));
    }

    @Test
    public void testBuildContainsContentLength() {
        byte[] result = new HttpResponse().setBody("Hello").build();
        String response = new String(result);
        assertTrue(response.contains("Content-Length: 5"));
    }

    @Test
    public void testBuildContainsBody() {
        byte[] result = new HttpResponse().setBody("Hello World").build();
        String response = new String(result);
        assertTrue(response.contains("Hello World"));
    }

    @Test
    public void testSetHeader() {
        HttpResponse resp = new HttpResponse().setHeader("X-Custom", "value");
        assertEquals("value", resp.getHeaders().get("X-Custom"));
    }

    @Test
    public void testBuildWithCustomHeader() {
        byte[] result = new HttpResponse()
                .setHeader("X-Custom", "test-value")
                .setBody("")
                .build();
        String response = new String(result);
        assertTrue(response.contains("X-Custom: test-value"));
    }

    @Test
    public void testStatus500() {
        HttpResponse resp = new HttpResponse().setStatus(500);
        assertEquals("Internal Server Error", resp.getStatusText());
    }

    @Test
    public void testStatus400() {
        HttpResponse resp = new HttpResponse().setStatus(400);
        assertEquals("Bad Request", resp.getStatusText());
    }

    @Test
    public void testBinaryResponseBuild() {
        byte[] data = "binary data".getBytes();
        byte[] result = new HttpResponse()
                .setContentType("application/octet-stream")
                .setBodyBytes(data)
                .build();
        String response = new String(result);
        assertTrue(response.contains("Content-Length: " + data.length));
    }

    @Test
    public void testEmptyBody() {
        byte[] result = new HttpResponse().setBody("").build();
        String response = new String(result);
        assertTrue(response.contains("Content-Length: 0"));
    }
}
