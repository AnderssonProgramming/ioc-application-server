package co.edu.escuelaing.reflexionlab;

import co.edu.escuelaing.reflexionlab.annotations.GetMapping;
import co.edu.escuelaing.reflexionlab.ioc.ComponentScanner;
import co.edu.escuelaing.reflexionlab.ioc.RequestHandler;
import co.edu.escuelaing.reflexionlab.ioc.RouteRegistry;
import co.edu.escuelaing.reflexionlab.server.HttpRequest;
import co.edu.escuelaing.reflexionlab.server.HttpResponse;
import co.edu.escuelaing.reflexionlab.server.StaticFileHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Lightweight IoC application server inspired by Spring Boot.
 * Supports @RestController scanning, @GetMapping routing, and @RequestParam binding.
 * Serves static files (HTML, CSS, JS, PNG, JPG, etc.) from a configurable classpath directory.
 *
 * <p>Usage:
 * <pre>
 *   java -cp target/classes co.edu.escuelaing.reflexionlab.MicroSpringBoot [ControllerClass]
 * </pre>
 * If no controller class is specified, the server scans the default package for @RestController classes.
 */
public class MicroSpringBoot {

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_STATIC_DIR = "/webroot";
    private static final String DEFAULT_SCAN_PACKAGE = "co.edu.escuelaing.reflexionlab";

    private final int port;
    private final RouteRegistry routeRegistry;
    private final RequestHandler requestHandler;
    private final StaticFileHandler staticFileHandler;
    private boolean running;

    public MicroSpringBoot(int port, String staticDir) {
        this.port = port;
        this.routeRegistry = new RouteRegistry();
        this.requestHandler = new RequestHandler(routeRegistry);
        this.staticFileHandler = new StaticFileHandler(staticDir);
        this.running = false;
    }

    public MicroSpringBoot() {
        this(DEFAULT_PORT, DEFAULT_STATIC_DIR);
    }

    /**
     * Registers a single controller class by name (command-line mode).
     */
    public void loadController(String className) throws Exception {
        ComponentScanner scanner = new ComponentScanner();
        Class<?> clazz = scanner.loadController(className);
        registerController(clazz);
    }

    /**
     * Scans a package for all @RestController classes and registers them.
     */
    public void scanComponents(String basePackage) {
        ComponentScanner scanner = new ComponentScanner();
        List<Class<?>> controllers = scanner.scan(basePackage);
        for (Class<?> clazz : controllers) {
            registerController(clazz);
        }
    }

    /**
     * Instantiates a controller and registers its @GetMapping methods.
     */
    void registerController(Class<?> clazz) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping mapping = method.getAnnotation(GetMapping.class);
                    routeRegistry.addRoute(mapping.value(), method, instance);
                    System.out.println("  Registered: GET " + mapping.value()
                            + " -> " + clazz.getSimpleName() + "." + method.getName() + "()");
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to register controller: " + clazz.getName() + " - " + e.getMessage());
        }
    }

    /**
     * Starts the HTTP server loop (blocking).
     */
    public void start() throws IOException {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            printBanner();
            while (running) {
                Socket clientSocket = serverSocket.accept();
                handleConnection(clientSocket);
            }
        }
    }

    void handleConnection(Socket clientSocket) {
        try (clientSocket;
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            StringBuilder rawRequest = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                rawRequest.append(line).append("\r\n");
            }

            if (rawRequest.isEmpty()) return;

            HttpRequest request = new HttpRequest(rawRequest.toString());
            byte[] responseBytes = processRequest(request);
            out.write(responseBytes);
            out.flush();

        } catch (IOException e) {
            System.err.println("Error handling connection: " + e.getMessage());
        }
    }

    byte[] processRequest(HttpRequest request) {
        String path = request.getPath();

        // REST API routes
        if (routeRegistry.hasRoute(path)) {
            String body = requestHandler.handle(request);
            if (body != null) {
                return new HttpResponse()
                        .setStatus(200)
                        .setContentType("text/html; charset=UTF-8")
                        .setBody(body)
                        .build();
            }
        }

        // Static file serving
        String filePath = "/".equals(path) ? "/index.html" : path;
        if (staticFileHandler.fileExists(filePath)) {
            try {
                byte[] fileBytes = staticFileHandler.getFileBytes(filePath);
                String contentType = StaticFileHandler.getContentType(filePath);
                return new HttpResponse()
                        .setStatus(200)
                        .setContentType(contentType)
                        .setBodyBytes(fileBytes)
                        .build();
            } catch (IOException e) {
                return buildErrorResponse(500, "Internal Server Error");
            }
        }

        return buildErrorResponse(404, "Not Found: " + path);
    }

    private byte[] buildErrorResponse(int code, String message) {
        String body = "<html><body><h1>" + code + "</h1><p>" + message + "</p></body></html>";
        return new HttpResponse()
                .setStatus(code)
                .setContentType("text/html; charset=UTF-8")
                .setBody(body)
                .build();
    }

    private void printBanner() {
        System.out.println("===============================================");
        System.out.println("   __  __ _                ____             _   ");
        System.out.println("  |  \\/  (_) ___ _ __ ___ / ___| _ __  _ __(_)_ __   __ _ ");
        System.out.println("  | |\\/| | |/ __| '__/ _ \\\\___ \\| '_ \\| '__| | '_ \\ / _` |");
        System.out.println("  | |  | | | (__| | | (_) |___) | |_) | |  | | | | | (_| |");
        System.out.println("  |_|  |_|_|\\___|_|  \\___/|____/| .__/|_|  |_|_| |_|\\__, |");
        System.out.println("                                 |_|                  |___/ ");
        System.out.println("          MicroSpringBoot v1.0 - IoC Application Server");
        System.out.println("===============================================");
        System.out.println("  Port: " + port);
        System.out.println("  Static files: " + staticFileHandler.getBaseDir());
        System.out.println("  Routes registered: " + routeRegistry.size());
        for (Map.Entry<String, RouteRegistry.RouteEntry> entry : routeRegistry.getAllRoutes().entrySet()) {
            System.out.println("    GET " + entry.getKey());
        }
        System.out.println("-----------------------------------------------");
        System.out.println("  Server started at http://localhost:" + port);
        System.out.println("===============================================");
    }

    public void stop() {
        running = false;
    }

    public RouteRegistry getRouteRegistry() {
        return routeRegistry;
    }

    public static void main(String[] args) {
        MicroSpringBoot server = new MicroSpringBoot();

        try {
            if (args.length > 0) {
                // Command-line mode: load specific controller class
                System.out.println("Loading controller: " + args[0]);
                server.loadController(args[0]);
            } else {
                // Auto-scan mode: discover all @RestController classes
                System.out.println("Scanning for @RestController components in: " + DEFAULT_SCAN_PACKAGE);
                server.scanComponents(DEFAULT_SCAN_PACKAGE);
            }

            server.start();
        } catch (Exception e) {
            System.err.println("Server failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
