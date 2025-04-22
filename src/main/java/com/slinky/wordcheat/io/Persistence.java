package com.slinky.wordcheat.io;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.slinky.wordcheat.language.Dictionary;
import com.slinky.wordcheat.language.OxfordDictionary;

import com.slinky.wordcheat.model.DefaultScoringModule;
import com.slinky.wordcheat.model.GameEngine;
import com.slinky.wordcheat.model.ScoringModule;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for saving and loading {@link GameEngine} instances
 * to/from JSON files in the user’s home directory.
 * 
 * <p>
 * Files are stored under <code>~/.wordcheat/saves/&lt;filename&gt;.json</code>.
 * Jackson is configured to ignore unknown properties on load and
 * to pretty‑print on save.
 * </p>
 */
public final class Persistence {

    private static final Path SAVE_DIR = Paths.get(
        System.getProperty("user.home"), ".wordcheat", "saves"
    );

    private static final ObjectMapper READ_MAPPER = new ObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private static final ObjectMapper WRITE_MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    static {
        try {
            Files.createDirectories(SAVE_DIR);
        } catch (IOException e) {
            throw new UncheckedIOException(
                "Could not create save directory: " + SAVE_DIR, e
            );
        }
    }

    private Persistence() {}
    
    /**
     * Checks whether a save file exists for the given name.
     *
     * @param filename the base name of the save file (without ".json")
     * @return {@code true} if ~/.wordcheat/saves/&lt;filename&gt;.json exists
     */
    public static boolean saveExists(String filename) {
        Path file = SAVE_DIR.resolve(filename + ".json");
        return Files.exists(file);
    }
    
    /**
     * Loads a saved {@link GameEngine} from a JSON file.
     *
     * @param filename the base name of the save file (without ".json")
     * @return a reconstructed GameEngine in the same state as when saved
     * @throws IOException if the file cannot be read or parsed
     */
    public static GameEngine loadGame(String filename) throws IOException {
        Path file = SAVE_DIR.resolve(filename + ".json");
        // Deserialize snapshot
        GameEngineSnapshot snapshot = READ_MAPPER.readValue(
            file.toFile(), GameEngineSnapshot.class
        );

        // Rebuild external dependencies
        Dictionary dict       = new OxfordDictionary();
        ScoringModule scoring = new DefaultScoringModule();

        // Reconstruct and return engine
        return snapshot.toEngine(dict, scoring);
    }

    /**
     * Saves the current state of a {@link GameEngine} to a JSON file.
     *
     * @param engine   the GameEngine to persist
     * @param filename the base name of the save file (without ".json:")
     * @throws IOException if the file cannot be written
     */
    public static void saveGame(GameEngine engine, String filename) throws IOException {
        Path file = SAVE_DIR.resolve(filename + ".json");
        // Create snapshot and write
        GameEngineSnapshot snapshot = GameEngineSnapshot.fromEngine(engine);
        WRITE_MAPPER.writeValue(file.toFile(), snapshot);
    }
    
}