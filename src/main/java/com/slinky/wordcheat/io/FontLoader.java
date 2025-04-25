package com.slinky.wordcheat.io;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.text.Font;

/**
 * Provides methods for registering and loading fonts from the internal
 * resources/fonts directory.
 *
 * @author Kheagen Haskins
 */
public class FontLoader {

    // ================================[ Static ]================================ \\
    /**
     * Registers a TrueType/OpenType font with the JVM’s graphics environment,
     * making it available to Font.font("<em>font name</em>", size) and other
     * font APIs.
     *
     * @param fontFileName the filename of the font resource (e.g.
     *                     "Inter-Regular.ttf")
     */
    public static void registerFont(String fontFileName) {
        String resourcePath = "/fonts/" + fontFileName;
        URL fontUrl = FontLoader.class.getResource(resourcePath);
        if (fontUrl == null) {
            System.err.println("Font resource not found: " + resourcePath);
            return;
        }

        try (InputStream is = FontLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Failed to open font resource: " + resourcePath);
                return;
            }
            java.awt.Font awtFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(awtFont);
        } catch (Exception e) {
            System.err.println("Failed to register font '" + fontFileName + "': " + e.getMessage());
        }
    }

    /**
     * Loads a TrueType or OpenType font from the resources/fonts directory.
     *
     * @param fontFileName the filename of the font resource (e.g.
     *                     "Inter-Regular.ttf")
     * @param size the desired point size of the font
     * @return the loaded Font, or the system default font if loading fails
     */
    public static Font loadFont(String fontFileName, double size) {
        String resourcePath = "/fonts/" + fontFileName;
        URL fontUrl = FontLoader.class.getResource(resourcePath);
        if (fontUrl == null) {
            System.err.println("Font resource not found: " + resourcePath);
            return Font.getDefault();
        }

        try {
            Path fontPath = Paths.get(fontUrl.toURI());
            try (InputStream is = Files.newInputStream(fontPath)) {
                Font font = Font.loadFont(is, size);
                if (font == null) {
                    throw new IOException("Font.loadFont returned null for " + fontFileName);
                }
                return font;
            }
        } catch (IOException | URISyntaxException e) {
            System.err.println("Failed to load font '" + fontFileName + "': " + e.getMessage());
            return Font.getDefault();
        }
    }

}