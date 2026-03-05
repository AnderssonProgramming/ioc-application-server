package co.edu.escuelaing.reflexionlab.server;

import org.junit.Test;
import static org.junit.Assert.*;

public class StaticFileHandlerTest {

    @Test
    public void testContentTypeHtml() {
        assertEquals("text/html", StaticFileHandler.getContentType("index.html"));
    }

    @Test
    public void testContentTypeHtm() {
        assertEquals("text/html", StaticFileHandler.getContentType("page.htm"));
    }

    @Test
    public void testContentTypeCss() {
        assertEquals("text/css", StaticFileHandler.getContentType("style.css"));
    }

    @Test
    public void testContentTypeJs() {
        assertEquals("application/javascript", StaticFileHandler.getContentType("app.js"));
    }

    @Test
    public void testContentTypeJson() {
        assertEquals("application/json", StaticFileHandler.getContentType("data.json"));
    }

    @Test
    public void testContentTypePng() {
        assertEquals("image/png", StaticFileHandler.getContentType("image.png"));
    }

    @Test
    public void testContentTypeJpg() {
        assertEquals("image/jpeg", StaticFileHandler.getContentType("photo.jpg"));
    }

    @Test
    public void testContentTypeJpeg() {
        assertEquals("image/jpeg", StaticFileHandler.getContentType("photo.jpeg"));
    }

    @Test
    public void testContentTypeGif() {
        assertEquals("image/gif", StaticFileHandler.getContentType("animation.gif"));
    }

    @Test
    public void testContentTypeSvg() {
        assertEquals("image/svg+xml", StaticFileHandler.getContentType("icon.svg"));
    }

    @Test
    public void testContentTypeIco() {
        assertEquals("image/x-icon", StaticFileHandler.getContentType("favicon.ico"));
    }

    @Test
    public void testContentTypeTxt() {
        assertEquals("text/plain", StaticFileHandler.getContentType("readme.txt"));
    }

    @Test
    public void testContentTypeUnknown() {
        assertEquals("application/octet-stream", StaticFileHandler.getContentType("data.xyz"));
    }

    @Test
    public void testContentTypeNoExtension() {
        assertEquals("application/octet-stream", StaticFileHandler.getContentType("Makefile"));
    }

    @Test
    public void testContentTypeCaseInsensitive() {
        assertEquals("text/html", StaticFileHandler.getContentType("PAGE.HTML"));
    }

    @Test
    public void testBaseDirWithLeadingSlash() {
        StaticFileHandler handler = new StaticFileHandler("/webroot");
        assertEquals("/webroot", handler.getBaseDir());
    }

    @Test
    public void testBaseDirWithoutLeadingSlash() {
        StaticFileHandler handler = new StaticFileHandler("webroot");
        assertEquals("/webroot", handler.getBaseDir());
    }

    @Test
    public void testFileExistsForExistingFile() {
        StaticFileHandler handler = new StaticFileHandler("/webroot");
        assertTrue(handler.fileExists("/index.html"));
    }

    @Test
    public void testFileExistsForNonExistingFile() {
        StaticFileHandler handler = new StaticFileHandler("/webroot");
        assertFalse(handler.fileExists("/nonexistent.html"));
    }

    @Test
    public void testGetFileBytesForExistingFile() throws Exception {
        StaticFileHandler handler = new StaticFileHandler("/webroot");
        byte[] bytes = handler.getFileBytes("/index.html");
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    public void testGetFileBytesForNonExistingFile() throws Exception {
        StaticFileHandler handler = new StaticFileHandler("/webroot");
        byte[] bytes = handler.getFileBytes("/nonexistent.html");
        assertNull(bytes);
    }

    @Test
    public void testFileExistsForCss() {
        StaticFileHandler handler = new StaticFileHandler("/webroot");
        assertTrue(handler.fileExists("/style.css"));
    }

    @Test
    public void testFileExistsForJs() {
        StaticFileHandler handler = new StaticFileHandler("/webroot");
        assertTrue(handler.fileExists("/app.js"));
    }

    @Test
    public void testContentTypeWithPath() {
        assertEquals("text/html", StaticFileHandler.getContentType("/path/to/index.html"));
    }

    @Test
    public void testContentTypeWithDeepPath() {
        assertEquals("text/css", StaticFileHandler.getContentType("/assets/css/main.css"));
    }

    @Test
    public void testFileExistsWithoutLeadingSlash() {
        StaticFileHandler handler = new StaticFileHandler("/webroot");
        assertTrue(handler.fileExists("index.html"));
    }
}
