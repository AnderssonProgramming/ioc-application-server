package co.edu.escuelaing.reflexionlab.ioc;

import co.edu.escuelaing.reflexionlab.annotations.GetMapping;
import co.edu.escuelaing.reflexionlab.annotations.RequestParam;
import co.edu.escuelaing.reflexionlab.server.HttpRequest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Handles incoming HTTP requests by matching them to registered routes,
 * invoking controller methods via reflection, and resolving @RequestParam annotations.
 */
public class RequestHandler {

    private final RouteRegistry registry;

    public RequestHandler(RouteRegistry registry) {
        this.registry = registry;
    }

    /**
     * Processes an HTTP request and returns the response body as a String.
     *
     * @param request the parsed HTTP request
     * @return the response body string, or null if no matching route
     */
    public String handle(HttpRequest request) {
        RouteRegistry.RouteEntry entry = registry.findRoute(request.getPath());
        if (entry == null) {
            return null;
        }
        return invokeMethod(entry, request);
    }

    private String invokeMethod(RouteRegistry.RouteEntry entry, HttpRequest request) {
        Method method = entry.getMethod();
        Object controller = entry.getControllerInstance();
        Parameter[] parameters = method.getParameters();

        try {
            if (parameters.length == 0) {
                return (String) method.invoke(controller);
            }

            Object[] args = resolveParameters(parameters, request);
            return (String) method.invoke(controller, args);
        } catch (Exception e) {
            return "Error invoking handler: " + e.getMessage();
        }
    }

    static Object[] resolveParameters(Parameter[] parameters, HttpRequest request) {
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            RequestParam annotation = parameters[i].getAnnotation(RequestParam.class);
            if (annotation != null) {
                String paramName = annotation.value();
                String defaultValue = annotation.defaultValue();
                String queryValue = request.getQueryParam(paramName);
                args[i] = queryValue.isEmpty() ? defaultValue : queryValue;
            } else {
                args[i] = null;
            }
        }
        return args;
    }
}
