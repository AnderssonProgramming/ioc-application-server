package co.edu.escuelaing.reflexionlab.ioc;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores the mapping between URI paths and their handler methods + controller instances.
 */
public class RouteRegistry {

    private final Map<String, RouteEntry> routes = new LinkedHashMap<>();

    public void addRoute(String path, Method method, Object controllerInstance) {
        String normalizedPath = normalizePath(path);
        routes.put(normalizedPath, new RouteEntry(method, controllerInstance));
    }

    public RouteEntry findRoute(String path) {
        String normalizedPath = normalizePath(path);
        return routes.get(normalizedPath);
    }

    public boolean hasRoute(String path) {
        return routes.containsKey(normalizePath(path));
    }

    public Map<String, RouteEntry> getAllRoutes() {
        return Collections.unmodifiableMap(routes);
    }

    public int size() {
        return routes.size();
    }

    static String normalizePath(String path) {
        if (path == null || path.isEmpty()) return "/";
        if (!path.startsWith("/")) path = "/" + path;
        if (path.length() > 1 && path.endsWith("/")) path = path.substring(0, path.length() - 1);
        return path;
    }

    /**
     * Holds a method reference and the controller instance to invoke it on.
     */
    public static class RouteEntry {
        private final Method method;
        private final Object controllerInstance;

        public RouteEntry(Method method, Object controllerInstance) {
            this.method = method;
            this.controllerInstance = controllerInstance;
        }

        public Method getMethod() {
            return method;
        }

        public Object getControllerInstance() {
            return controllerInstance;
        }
    }
}
