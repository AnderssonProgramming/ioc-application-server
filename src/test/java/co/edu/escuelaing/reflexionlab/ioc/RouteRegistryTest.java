package co.edu.escuelaing.reflexionlab.ioc;

import co.edu.escuelaing.reflexionlab.annotations.GetMapping;
import co.edu.escuelaing.reflexionlab.annotations.RestController;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class RouteRegistryTest {

    private RouteRegistry registry;

    @Before
    public void setUp() {
        registry = new RouteRegistry();
    }

    @Test
    public void testAddAndFindRoute() throws Exception {
        Method method = SampleController.class.getMethod("hello");
        Object instance = new SampleController();
        registry.addRoute("/hello", method, instance);

        RouteRegistry.RouteEntry entry = registry.findRoute("/hello");
        assertNotNull(entry);
        assertEquals(method, entry.getMethod());
        assertEquals(instance, entry.getControllerInstance());
    }

    @Test
    public void testFindRouteNonExistent() {
        assertNull(registry.findRoute("/nonexistent"));
    }

    @Test
    public void testHasRoute() throws Exception {
        Method method = SampleController.class.getMethod("hello");
        registry.addRoute("/hello", method, new SampleController());
        assertTrue(registry.hasRoute("/hello"));
        assertFalse(registry.hasRoute("/goodbye"));
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(0, registry.size());
        Method method = SampleController.class.getMethod("hello");
        registry.addRoute("/a", method, new SampleController());
        registry.addRoute("/b", method, new SampleController());
        assertEquals(2, registry.size());
    }

    @Test
    public void testNormalizePathAddsSlash() {
        assertEquals("/hello", RouteRegistry.normalizePath("hello"));
    }

    @Test
    public void testNormalizePathRemovesTrailingSlash() {
        assertEquals("/hello", RouteRegistry.normalizePath("/hello/"));
    }

    @Test
    public void testNormalizePathRoot() {
        assertEquals("/", RouteRegistry.normalizePath("/"));
    }

    @Test
    public void testNormalizePathEmpty() {
        assertEquals("/", RouteRegistry.normalizePath(""));
    }

    @Test
    public void testNormalizePathNull() {
        assertEquals("/", RouteRegistry.normalizePath(null));
    }

    @Test
    public void testRouteNormalization() throws Exception {
        Method method = SampleController.class.getMethod("hello");
        registry.addRoute("hello", method, new SampleController());
        assertNotNull(registry.findRoute("/hello"));
        assertNotNull(registry.findRoute("/hello/"));
    }

    @Test
    public void testGetAllRoutes() throws Exception {
        Method method = SampleController.class.getMethod("hello");
        registry.addRoute("/a", method, new SampleController());
        registry.addRoute("/b", method, new SampleController());
        assertEquals(2, registry.getAllRoutes().size());
    }

    @Test
    public void testGetAllRoutesIsUnmodifiable() throws Exception {
        Method method = SampleController.class.getMethod("hello");
        registry.addRoute("/a", method, new SampleController());
        try {
            registry.getAllRoutes().put("/c", null);
            fail("Should not modify routes map");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testRouteEntryGetters() throws Exception {
        Method method = SampleController.class.getMethod("hello");
        SampleController instance = new SampleController();
        RouteRegistry.RouteEntry entry = new RouteRegistry.RouteEntry(method, instance);
        assertEquals(method, entry.getMethod());
        assertSame(instance, entry.getControllerInstance());
    }

    @Test
    public void testMultipleRoutesWithDifferentControllers() throws Exception {
        Method helloMethod = SampleController.class.getMethod("hello");
        Method greetMethod = SampleController.class.getMethod("greet");
        SampleController c1 = new SampleController();
        SampleController c2 = new SampleController();
        registry.addRoute("/hello", helloMethod, c1);
        registry.addRoute("/greet", greetMethod, c2);

        assertSame(c1, registry.findRoute("/hello").getControllerInstance());
        assertSame(c2, registry.findRoute("/greet").getControllerInstance());
    }

    // Test helper class
    @RestController
    public static class SampleController {
        @GetMapping("/hello")
        public String hello() { return "Hello"; }

        @GetMapping("/greet")
        public String greet() { return "Greet"; }
    }
}
