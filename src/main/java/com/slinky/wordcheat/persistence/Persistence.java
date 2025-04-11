package com.slinky.wordcheat.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slinky.wordcheat.model.GameBoard;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles saving and loading the state of the application.
 *
 * @author Kheagen Haskins
 */
public final class Persistence {

    // ================================[ Static ]================================ \\
    private static final Path ROOT;
    private static final ObjectMapper mapper = new ObjectMapper();
    // Dedicated single-thread executor to perform I/O off the main thread.
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    static {
        // Retrieve the %LOCALAPPDATA% environment variable.
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData == null) {
            throw new IllegalStateException("LOCALAPPDATA environment variable not set.");
        }

        ROOT = Paths.get(localAppData, "WordCheat");
        try {
            Files.createDirectories(ROOT);
        } catch (IOException e) {
            throw new RuntimeException("Could not create application directory: " + ROOT, e);
        }
    }

    // =============================[ Constructors ]============================= \\
    private Persistence() {} // Prevent instantiation

    // ===========================[ API Methods ]============================== \\
    /**
     * Reads the game board state from a JSON file.
     *
     * @param filename the name of the JSON file in the application's directory.
     * @return the game board constructed from the JSON data.
     * @throws IOException if there is an error reading the file.
     */
    public static GameBoard fromJson(String filename) throws IOException {
        Path filePath = ROOT.resolve(filename);
        String json   = Files.readString(filePath);
        // For this example we assume the game board is stored as a 2D char array.
        char[][] matrix = mapper.readValue(json, char[][].class);
        return new GameBoard(matrix);
    }

    /**
     * Saves the game board state to a JSON file asynchronously. The write
     * operation is performed on a dedicated thread to avoid blocking the main
     * thread.
     *
     * @param board the current game board state.
     * @param filename the target filename for saving the state.
     */
    public static void toJsonAsync(GameBoard board, String filename) {
        executor.submit(() -> {
            try {
                // Serialise the game board's state. For this example, assume board.getMatrix() returns a 2D char array.
                String json   = mapper.writeValueAsString(board.getMatrix());
                Path filePath = ROOT.resolve(filename);
                // Create a temporary file in the same directory.
                Path tempFile = Files.createTempFile(ROOT, "temp", ".json");
                // Write JSON to the temporary file.
                Files.writeString(tempFile, json);
                // Atomically move the temporary file to the target file location.
                Files.move(tempFile, filePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                // Handle exceptions (e.g. log the error).
                e.printStackTrace();
            }
        });
    }

    // ============================[ Helper Methods ]============================ \\
    /**
     * Shuts down the dedicated executor. Call this when the application is
     * closing.
     */
    public static void shutdown() {
        executor.shutdown();
    }
}
