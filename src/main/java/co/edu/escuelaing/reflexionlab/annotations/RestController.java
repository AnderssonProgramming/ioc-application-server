package co.edu.escuelaing.reflexionlab.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a REST controller component.
 * Classes annotated with @RestController are automatically discovered
 * by the IoC container and their @GetMapping methods are registered as routes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RestController {
}
