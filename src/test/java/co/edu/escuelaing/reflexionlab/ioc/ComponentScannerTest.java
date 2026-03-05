package co.edu.escuelaing.reflexionlab.ioc;

import co.edu.escuelaing.reflexionlab.annotations.RestController;
import org.junit.Test;

import static org.junit.Assert.*;

public class ComponentScannerTest {

    private final ComponentScanner scanner = new ComponentScanner();

    @Test
    public void testScanFindsControllers() {
        var controllers = scanner.scan("co.edu.escuelaing.reflexionlab.demo");
        assertFalse(controllers.isEmpty());
    }

    @Test
    public void testScanFindsHelloController() {
        var controllers = scanner.scan("co.edu.escuelaing.reflexionlab.demo");
        boolean foundHello = controllers.stream()
                .anyMatch(c -> c.getSimpleName().equals("HelloController"));
        assertTrue("Should find HelloController", foundHello);
    }

    @Test
    public void testScanFindsGreetingController() {
        var controllers = scanner.scan("co.edu.escuelaing.reflexionlab.demo");
        boolean foundGreeting = controllers.stream()
                .anyMatch(c -> c.getSimpleName().equals("GreetingController"));
        assertTrue("Should find GreetingController", foundGreeting);
    }

    @Test
    public void testScanFindsMathController() {
        var controllers = scanner.scan("co.edu.escuelaing.reflexionlab.demo");
        boolean foundMath = controllers.stream()
                .anyMatch(c -> c.getSimpleName().equals("MathController"));
        assertTrue("Should find MathController", foundMath);
    }

    @Test
    public void testScanNonExistentPackage() {
        var controllers = scanner.scan("com.nonexistent.package");
        assertTrue(controllers.isEmpty());
    }

    @Test
    public void testAllDiscoveredClassesHaveAnnotation() {
        var controllers = scanner.scan("co.edu.escuelaing.reflexionlab.demo");
        for (Class<?> c : controllers) {
            assertTrue(c.getSimpleName() + " must have @RestController",
                    c.isAnnotationPresent(RestController.class));
        }
    }

    @Test
    public void testLoadControllerValid() throws Exception {
        Class<?> clazz = scanner.loadController("co.edu.escuelaing.reflexionlab.demo.HelloController");
        assertNotNull(clazz);
        assertTrue(clazz.isAnnotationPresent(RestController.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadControllerWithoutAnnotation() throws Exception {
        scanner.loadController("java.lang.String");
    }

    @Test(expected = ClassNotFoundException.class)
    public void testLoadControllerNonExistentClass() throws Exception {
        scanner.loadController("com.nonexistent.FakeController");
    }

    @Test
    public void testScanReturnsCorrectCount() {
        var controllers = scanner.scan("co.edu.escuelaing.reflexionlab.demo");
        assertEquals(3, controllers.size());
    }
}
