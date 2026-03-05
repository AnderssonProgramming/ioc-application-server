package co.edu.escuelaing.reflexionlab.annotations;

import co.edu.escuelaing.reflexionlab.demo.GreetingController;
import co.edu.escuelaing.reflexionlab.demo.HelloController;
import co.edu.escuelaing.reflexionlab.demo.MathController;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class AnnotationsTest {

    // --- @RestController tests ---

    @Test
    public void testRestControllerPresentOnHelloController() {
        assertTrue(HelloController.class.isAnnotationPresent(RestController.class));
    }

    @Test
    public void testRestControllerPresentOnGreetingController() {
        assertTrue(GreetingController.class.isAnnotationPresent(RestController.class));
    }

    @Test
    public void testRestControllerPresentOnMathController() {
        assertTrue(MathController.class.isAnnotationPresent(RestController.class));
    }

    @Test
    public void testRestControllerRetentionIsRuntime() {
        Retention retention = RestController.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    public void testRestControllerTargetIsType() {
        Target target = RestController.class.getAnnotation(Target.class);
        assertNotNull(target);
        assertArrayEquals(new ElementType[]{ElementType.TYPE}, target.value());
    }

    // --- @GetMapping tests ---

    @Test
    public void testGetMappingOnHelloIndex() throws Exception {
        Method method = HelloController.class.getMethod("index");
        GetMapping mapping = method.getAnnotation(GetMapping.class);
        assertNotNull(mapping);
        assertEquals("/", mapping.value());
    }

    @Test
    public void testGetMappingOnGreeting() throws Exception {
        Method method = GreetingController.class.getMethod("greeting", String.class);
        GetMapping mapping = method.getAnnotation(GetMapping.class);
        assertNotNull(mapping);
        assertEquals("/greeting", mapping.value());
    }

    @Test
    public void testGetMappingOnPi() throws Exception {
        Method method = MathController.class.getMethod("pi");
        GetMapping mapping = method.getAnnotation(GetMapping.class);
        assertNotNull(mapping);
        assertEquals("/pi", mapping.value());
    }

    @Test
    public void testGetMappingRetentionIsRuntime() {
        Retention retention = GetMapping.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    public void testGetMappingTargetIsMethod() {
        Target target = GetMapping.class.getAnnotation(Target.class);
        assertNotNull(target);
        assertArrayEquals(new ElementType[]{ElementType.METHOD}, target.value());
    }

    // --- @RequestParam tests ---

    @Test
    public void testRequestParamOnGreetingMethod() throws Exception {
        Method method = GreetingController.class.getMethod("greeting", String.class);
        java.lang.reflect.Parameter param = method.getParameters()[0];
        RequestParam rp = param.getAnnotation(RequestParam.class);
        assertNotNull(rp);
        assertEquals("name", rp.value());
        assertEquals("World", rp.defaultValue());
    }

    @Test
    public void testRequestParamOnSquareMethod() throws Exception {
        Method method = MathController.class.getMethod("square", String.class);
        java.lang.reflect.Parameter param = method.getParameters()[0];
        RequestParam rp = param.getAnnotation(RequestParam.class);
        assertNotNull(rp);
        assertEquals("n", rp.value());
        assertEquals("0", rp.defaultValue());
    }

    @Test
    public void testRequestParamRetentionIsRuntime() {
        Retention retention = RequestParam.class.getAnnotation(Retention.class);
        assertNotNull(retention);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    public void testRequestParamTargetIsParameter() {
        Target target = RequestParam.class.getAnnotation(Target.class);
        assertNotNull(target);
        assertArrayEquals(new ElementType[]{ElementType.PARAMETER}, target.value());
    }

    @Test
    public void testRequestParamDefaultValueIsEmpty() throws Exception {
        Method defaultValueMethod = RequestParam.class.getMethod("defaultValue");
        assertEquals("", defaultValueMethod.getDefaultValue());
    }
}
