package co.edu.escuelaing.reflexionlab.ioc;

import co.edu.escuelaing.reflexionlab.annotations.RestController;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans the classpath for classes annotated with @RestController.
 * Implements component discovery for the IoC container.
 */
public class ComponentScanner {

    /**
     * Scans a given package for classes annotated with @RestController.
     *
     * @param basePackage the root package to scan (e.g., "co.edu.escuelaing.reflexionlab")
     * @return list of classes annotated with @RestController
     */
    public List<Class<?>> scan(String basePackage) {
        List<Class<?>> controllers = new ArrayList<>();
        String path = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            return controllers;
        }

        File directory = new File(resource.getFile());
        if (!directory.exists()) {
            return controllers;
        }

        scanDirectory(directory, basePackage, controllers);
        return controllers;
    }

    private void scanDirectory(File directory, String packageName, List<Class<?>> controllers) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), controllers);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(RestController.class)) {
                        controllers.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // Skip classes that cannot be loaded
                }
            }
        }
    }

    /**
     * Loads a single class by its fully qualified name.
     *
     * @param className fully qualified class name
     * @return the Class object if it has @RestController, null otherwise
     */
    public Class<?> loadController(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        if (clazz.isAnnotationPresent(RestController.class)) {
            return clazz;
        }
        throw new IllegalArgumentException("Class " + className + " is not annotated with @RestController");
    }
}
