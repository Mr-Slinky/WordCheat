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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for saving and loading {@link GameEngine} instances
 * to/from JSON files in the user's home directory.
 *
 * <p>
 * Files are stored under <code>~/.wordcheat/saves/&lt;filename&gt;.json</code>.
 * Jackson is configured to ignore unknown properties on load and
 * to pretty-print on save.
 *
 * <pre>
 *     if (Persistence.saveExists("mom")) {
 *         GameEngine engine = Persistence.loadGame("mom");
 *     }
 * </pre>
 *
 * @author Kheagen Haskins
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

    private Persistence() {}

    /**
     * Checks whether a save file exists for the given name.
     *
     * @param filename the base name of the save file (without ".json")
     * @return {@code true} if ~/.wordcheat/saves/&lt;filename&gt;.json exists
     */
    public static boolean saveExists(String filename) {
        return Files.exists(resolveSave(filename));
    }

    /**
     * Returns the path of the save file for the given name.
     *
     * @param filename the base name of the save file (without ".json")
     * @return the path ~/.wordcheat/saves/&lt;filename&gt;.json
     */
    public static Path resolveSave(String filename) {
        return SAVE_DIR.resolve(filename + ".json");
    }

    /**
     * Loads a saved {@link GameEngine} from a JSON file.
     *
     * @param filename the base name of the save file (without ".json")
     * @return a reconstructed GameEngine in the same state as when saved
     * @throws IOException if the file cannot be read, or holds a game that
     *                     cannot be rebuilt
     */
    public static GameEngine loadGame(String filename) throws IOException {
        return parseGame(Files.readString(resolveSave(filename), StandardCharsets.UTF_8));
    }

    /**
     * Saves the current state of a {@link GameEngine} to a JSON file.
     *
     * <p>
     * The method writes the game to a temporary file first and then moves it
     * over the save, so a failed write leaves the previous save in place.
     *
     * @param engine   the GameEngine to persist
     * @param filename the base name of the save file (without ".json")
     * @throws IOException if the file cannot be written
     */
    public static void saveGame(GameEngine engine, String filename) throws IOException {
        Files.createDirectories(SAVE_DIR);
        Path file = resolveSave(filename);
        Path temp = SAVE_DIR.resolve(filename + ".json.tmp");
        Files.writeString(temp, formatGame(engine), StandardCharsets.UTF_8);
        Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Renames a save file that cannot be loaded, so a new game can take its
     * name without destroying it. The file {@code mom.json} becomes
     * {@code mom.json.bak}, replacing any earlier backup.
     *
     * @param filename the base name of the save file (without ".json")
     * @return the path of the backup
     * @throws IOException if the file cannot be renamed
     */
    public static Path backupSave(String filename) throws IOException {
        Path backup = SAVE_DIR.resolve(filename + ".json.bak");
        Files.move(resolveSave(filename), backup, StandardCopyOption.REPLACE_EXISTING);
        return backup;
    }

    /**
     * Rebuilds a {@link GameEngine} from the JSON text of a save.
     *
     * @param json the contents of a save file
     * @return a reconstructed GameEngine
     * @throws IOException if the text is not a save, or holds a game that
     *                     cannot be rebuilt
     */
    static GameEngine parseGame(String json) throws IOException {
        GameEngineSnapshot snapshot = READ_MAPPER.readValue(json, GameEngineSnapshot.class);

        Dictionary dict       = new OxfordDictionary();
        ScoringModule scoring = new DefaultScoringModule();
        try {
            return snapshot.toEngine(dict, scoring);
        } catch (RuntimeException ex) {
            throw new IOException("The save holds a game that cannot be rebuilt: " + ex.getMessage(), ex);
        }
    }

    /**
     * Writes the state of a {@link GameEngine} as the JSON text of a save.
     *
     * @param engine the GameEngine to persist
     * @return the JSON text
     * @throws IOException if the engine cannot be written as JSON
     */
    static String formatGame(GameEngine engine) throws IOException {
        return WRITE_MAPPER.writeValueAsString(GameEngineSnapshot.fromEngine(engine));
    }

}
