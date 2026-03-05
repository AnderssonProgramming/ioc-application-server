package co.edu.escuelaing.reflexionlab.ioc;

import co.edu.escuelaing.reflexionlab.annotations.GetMapping;
import co.edu.escuelaing.reflexionlab.annotations.RequestParam;
import co.edu.escuelaing.reflexionlab.annotations.RestController;
import co.edu.escuelaing.reflexionlab.server.HttpRequest;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class RequestHandlerTest {

    private RouteRegistry registry;
    private RequestHandler handler;

    @Before
    public void setUp() {
        registry = new RouteRegistry();
        handler = new RequestHandler(registry);
    }

    @Test
    public void testHandleSimpleRoute() throws Exception {
        Method method = TestController.class.getMethod("hello");
        registry.addRoute("/hello", method, new TestController());

        HttpRequest request = new HttpRequest("GET /hello HTTP/1.1\r\n\r\n");
        String result = handler.handle(request);
        assertEquals("Hello!", result);
    }

    @Test
    public void testHandleRouteWithRequestParam() throws Exception {
        Method method = TestController.class.getMethod("greet", String.class);
        registry.addRoute("/greet", method, new TestController());

        HttpRequest request = new HttpRequest("GET /greet?name=Pedro HTTP/1.1\r\n\r\n");
        String result = handler.handle(request);
        assertEquals("Hello, Pedro!", result);
    }

    @Test
    public void testHandleRouteWithDefaultParam() throws Exception {
        Method method = TestController.class.getMethod("greet", String.class);
        registry.addRoute("/greet", method, new TestController());

        HttpRequest request = new HttpRequest("GET /greet HTTP/1.1\r\n\r\n");
        String result = handler.handle(request);
        assertEquals("Hello, World!", result);
    }

    @Test
    public void testHandleNonExistentRoute() {
        HttpRequest request = new HttpRequest("GET /nonexistent HTTP/1.1\r\n\r\n");
        String result = handler.handle(request);
        assertNull(result);
    }

    @Test
    public void testResolveParametersWithValue() throws Exception {
        Method method = TestController.class.getMethod("greet", String.class);
        java.lang.reflect.Parameter[] params = method.getParameters();
        HttpRequest request = new HttpRequest("GET /greet?name=Ana HTTP/1.1\r\n\r\n");

        Object[] args = RequestHandler.resolveParameters(params, request);
        assertEquals(1, args.length);
        assertEquals("Ana", args[0]);
    }

    @Test
    public void testResolveParametersWithDefault() throws Exception {
        Method method = TestController.class.getMethod("greet", String.class);
        java.lang.reflect.Parameter[] params = method.getParameters();
        HttpRequest request = new HttpRequest("GET /greet HTTP/1.1\r\n\r\n");

        Object[] args = RequestHandler.resolveParameters(params, request);
        assertEquals(1, args.length);
        assertEquals("World", args[0]);
    }

    @Test
    public void testResolveParametersNoAnnotation() throws Exception {
        Method method = TestController.class.getMethod("noAnnotation", String.class);
        java.lang.reflect.Parameter[] params = method.getParameters();
        HttpRequest request = new HttpRequest("GET /test HTTP/1.1\r\n\r\n");

        Object[] args = RequestHandler.resolveParameters(params, request);
        assertNull(args[0]);
    }

    @Test
    public void testHandleMultipleParams() throws Exception {
        Method method = TestController.class.getMethod("multi", String.class, String.class);
        registry.addRoute("/multi", method, new TestController());

        HttpRequest request = new HttpRequest("GET /multi?a=foo&b=bar HTTP/1.1\r\n\r\n");
        String result = handler.handle(request);
        assertEquals("foo-bar", result);
    }

    @Test
    public void testHandleMethodReturnsNull() throws Exception {
        Method method = TestController.class.getMethod("returnsNull");
        registry.addRoute("/null", method, new TestController());

        HttpRequest request = new HttpRequest("GET /null HTTP/1.1\r\n\r\n");
        String result = handler.handle(request);
        assertNull(result);
    }

    @RestController
    public static class TestController {

        @GetMapping("/hello")
        public String hello() {
            return "Hello!";
        }

        @GetMapping("/greet")
        public String greet(@RequestParam(value = "name", defaultValue = "World") String name) {
            return "Hello, " + name + "!";
        }

        public String noAnnotation(String value) {
            return value;
        }

        @GetMapping("/multi")
        public String multi(
                @RequestParam(value = "a", defaultValue = "") String a,
                @RequestParam(value = "b", defaultValue = "") String b) {
            return a + "-" + b;
        }

        @GetMapping("/null")
        public String returnsNull() {
            return null;
        }
    }
}
