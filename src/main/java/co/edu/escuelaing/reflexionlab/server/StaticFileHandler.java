package co.edu.escuelaing.reflexionlab.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles serving static files from the classpath.
 * Supports HTML, CSS, JS, images (PNG, JPG, GIF, SVG, ICO), and JSON.
 */
public class StaticFileHandler {

    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("html", "text/html");
        MIME_TYPES.put("htm", "text/html");
        MIME_TYPES.put("css", "text/css");
        MIME_TYPES.put("js", "application/javascript");
        MIME_TYPES.put("json", "application/json");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("gif", "image/gif");
        MIME_TYPES.put("svg", "image/svg+xml");
        MIME_TYPES.put("ico", "image/x-icon");
        MIME_TYPES.put("txt", "text/plain");
    }

    private final String baseDir;

    public StaticFileHandler(String baseDir) {
        this.baseDir = baseDir.startsWith("/") ? baseDir : "/" + baseDir;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public byte[] getFileBytes(String filePath) throws IOException {
        String resourcePath = baseDir + (filePath.startsWith("/") ? filePath : "/" + filePath);
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                return null;
            }
            return is.readAllBytes();
        }
    }

    public boolean fileExists(String filePath) {
        String resourcePath = baseDir + (filePath.startsWith("/") ? filePath : "/" + filePath);
        return getClass().getResource(resourcePath) != null;
    }

    public static String getContentType(String fileName) {
        int dotIdx = fileName.lastIndexOf('.');
        if (dotIdx >= 0) {
            String ext = fileName.substring(dotIdx + 1).toLowerCase();
            return MIME_TYPES.getOrDefault(ext, "application/octet-stream");
        }
        return "application/octet-stream";
    }
}
