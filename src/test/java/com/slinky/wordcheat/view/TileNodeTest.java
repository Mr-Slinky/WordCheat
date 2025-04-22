package com.slinky.wordcheat.view;

import javafx.embed.swing.JFXPanel;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TileNodeTest {

    @BeforeAll
    static void initJavaFX() {
        // Initialize JavaFX toolkit for testing
        new JFXPanel();
    }

    @Test
    void testDefaultStyle() {
        TileNode tile = new TileNode();
        assertAll("default style",
            () -> assertEquals(6.0, tile.getBackgroundNode().getArcWidth()),
            () -> assertEquals(6.0, tile.getBackgroundNode().getArcHeight()),
            () -> assertEquals(Color.web("#ECEFF1"), tile.getBackgroundNode().getFill())
        );
    }

    @ParameterizedTest
    @CsvSource({
        "A, 1, A, 1",
        "Z, 10, Z, 10",
        "' ', 0, '', ''"
    })
    void testLetterAndScore(char letter, int score, String expectedLetter, String expectedScore) {
        TileNode tile = new TileNode();
        tile.setLetter(letter);
        tile.setScore(score);
        tile.setWildcard(false);
        tile.refresh();

        assertAll("letter and score",
            () -> assertEquals(expectedLetter, tile.getLetterLabel().getText()),
            () -> assertEquals(expectedScore,  tile.getScoreLabel().getText())
        );
    }

    @ParameterizedTest
    @CsvSource({
        "TL,TL",
        "DW,DW",
        "'', ''"
    })
    void testBonusDisplay(String bonus, String expected) {
        TileNode tile = new TileNode();
        tile.setLetter(' ');
        // Treat empty string as no bonus
        if (bonus == null || bonus.isEmpty()) {
            tile.setBonus(null);
        } else {
            tile.setBonus(bonus);
        }
        tile.refresh();

        assertEquals(expected, tile.getBonusLabel().getText());
    }

    @ParameterizedTest
    @CsvSource({
        "true,#FFE082",
        "false,#ECEFF1"
    })
    void testWildcardFill(boolean wildcard, String expectedHex) {
        TileNode tile = new TileNode();
        tile.setWildcard(wildcard);
        tile.applyStyle();

        assertEquals(Color.web(expectedHex), tile.getBackgroundNode().getFill());
    }

    @ParameterizedTest
    @CsvSource({"0.0", "12.5", "100.0"})
    void testCornerRadius(double radius) {
        TileNode tile = new TileNode();
        tile.setCornerRadius(radius);
        tile.applyStyle();

        assertAll("corner radius",
            () -> assertEquals(radius, tile.getBackgroundNode().getArcWidth()),
            () -> assertEquals(radius, tile.getBackgroundNode().getArcHeight())
        );
    }
    
}