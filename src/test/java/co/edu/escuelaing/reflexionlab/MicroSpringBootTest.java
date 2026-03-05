package co.edu.escuelaing.reflexionlab;

import co.edu.escuelaing.reflexionlab.ioc.RouteRegistry;
import co.edu.escuelaing.reflexionlab.server.HttpRequest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MicroSpringBootTest {

    private MicroSpringBoot server;

    @Before
    public void setUp() {
        server = new MicroSpringBoot(9090, "/webroot");
    }

    @Test
    public void testLoadControllerFromClassName() throws Exception {
        server.loadController("co.edu.escuelaing.reflexionlab.demo.HelloController");
        RouteRegistry registry = server.getRouteRegistry();
        assertTrue(registry.hasRoute("/"));
        assertTrue(registry.hasRoute("/hello"));
    }

    @Test
    public void testScanComponentsFindsAll() {
        server.scanComponents("co.edu.escuelaing.reflexionlab.demo");
        RouteRegistry registry = server.getRouteRegistry();
        assertTrue(registry.size() >= 3);
    }

    @Test
    public void testProcessRequestForRestRoute() throws Exception {
        server.loadController("co.edu.escuelaing.reflexionlab.demo.HelloController");
        HttpRequest request = new HttpRequest("GET /hello HTTP/1.1\r\n\r\n");
        byte[] response = server.processRequest(request);
        String responseStr = new String(response);
        assertTrue(responseStr.contains("200 OK"));
        assertTrue(responseStr.contains("Hello World!"));
    }

    @Test
    public void testProcessRequestForGreetingWithParam() throws Exception {
        server.loadController("co.edu.escuelaing.reflexionlab.demo.GreetingController");
        HttpRequest request = new HttpRequest("GET /greeting?name=Pedro HTTP/1.1\r\n\r\n");
        byte[] response = server.processRequest(request);
        String responseStr = new String(response);
        assertTrue(responseStr.contains("200 OK"));
        assertTrue(responseStr.contains("Pedro"));
    }

    @Test
    public void testProcessRequestForGreetingWithDefault() throws Exception {
        server.loadController("co.edu.escuelaing.reflexionlab.demo.GreetingController");
        HttpRequest request = new HttpRequest("GET /greeting HTTP/1.1\r\n\r\n");
        byte[] response = server.processRequest(request);
        String responseStr = new String(response);
        assertTrue(responseStr.contains("World"));
    }

    @Test
    public void testProcessRequestForStaticFile() {
        HttpRequest request = new HttpRequest("GET /index.html HTTP/1.1\r\n\r\n");
        byte[] response = server.processRequest(request);
        String responseStr = new String(response);
        assertTrue(responseStr.contains("200 OK"));
        assertTrue(responseStr.contains("text/html"));
    }

    @Test
    public void testProcessRequestForCss() {
        HttpRequest request = new HttpRequest("GET /style.css HTTP/1.1\r\n\r\n");
        byte[] response = server.processRequest(request);
        String responseStr = new String(response);
        assertTrue(responseStr.contains("200 OK"));
        assertTrue(responseStr.contains("text/css"));
    }

    @Test
    public void testProcessRequestFor404() {
        HttpRequest request = new HttpRequest("GET /nonexistent.html HTTP/1.1\r\n\r\n");
        byte[] response = server.processRequest(request);
        String responseStr = new String(response);
        assertTrue(responseStr.contains("404"));
    }

    @Test
    public void testProcessRequestRootRedirectsToIndex() {
        // Root path "/" without a registered REST route should serve index.html
        HttpRequest request = new HttpRequest("GET / HTTP/1.1\r\n\r\n");
        byte[] response = server.processRequest(request);
        String responseStr = new String(response);
        // Should serve static index.html since no controller loaded
        assertTrue(responseStr.contains("200 OK"));
    }

    @Test
    public void testPiEndpoint() throws Exception {
        server.loadController("co.edu.escuelaing.reflexionlab.demo.MathController");
        HttpRequest request = new HttpRequest("GET /pi HTTP/1.1\r\n\r\n");
        byte[] response = server.processRequest(request);
        String responseStr = new String(response);
        assertTrue(responseStr.contains(String.valueOf(Math.PI)));
    }

    @Test
    public void testSquareEndpoint() throws Exception {
        server.loadController("co.edu.escuelaing.reflexionlab.demo.MathController");
        HttpRequest request = new HttpRequest("GET /square?n=5 HTTP/1.1\r\n\r\n");
        byte[] response = server.processRequest(request);
        String responseStr = new String(response);
        assertTrue(responseStr.contains("25"));
    }

    @Test
    public void testLoadControllerInvalidClassThrows() {
        try {
            server.loadController("com.nonexistent.FakeController");
            fail("Should throw exception");
        } catch (Exception e) {
            // expected
        }
    }

    @Test
    public void testRouteRegistryAfterScan() {
        server.scanComponents("co.edu.escuelaing.reflexionlab.demo");
        RouteRegistry registry = server.getRouteRegistry();
        assertTrue(registry.hasRoute("/"));
        assertTrue(registry.hasRoute("/hello"));
        assertTrue(registry.hasRoute("/greeting"));
        assertTrue(registry.hasRoute("/pi"));
        assertTrue(registry.hasRoute("/square"));
        assertTrue(registry.hasRoute("/time"));
    }
}
