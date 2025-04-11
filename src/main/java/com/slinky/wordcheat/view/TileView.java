package com.slinky.wordcheat.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Represents a game tile that displays its visual type and can show two
 * numbers: one for the score (displayed in the top-right corner) and one for
 * the tile count (displayed in the bottom-right corner).
 *
 * <p>
 * The score represents the value of the letter on the tile and is only shown if
 * greater than 0. The count represents the number of tiles of that type
 * available and is only shown if greater than 0.
 * </p>
 *
 * <p>
 * The tile is constructed with only a {@link CellType}. The score and count can
 * be updated later via the {@code setScore(int)} and {@code setCount(int)}
 * methods.
 * </p>
 *
 * <p>
 * <strong>Note:</strong> This class is not designed for multi-threaded use.
 * </p>
 *
 * @author Kheagen Haskins
 */
public class TileView extends StackPane {
    
    public static final Color DEFAULT_LETTER_COLOR = Color.rgb(254, 198, 1);

    public static final double MAX_SIZE = 40;

    private static final double CENTER_FONT_SIZE = 15.0;

    private static final double AUXILIARY_FONT_SIZE = 8.0;

    /**
     * The default corner radius for all tiles.
     */
    private static final double DEFAULT_CORNER_RADIUS = 5.0;
    /**
     * The type of the tile, determining its visual style.
     */
    private final CellType type;

    /**
     * The score value of the tile (applies only if the tile holds a letter).
     */
    private int score;

    /**
     * The count of the tile type (how many of that tile are available).
     */
    private int count;

    /**
     * The centre text node, intended for displaying the letter.
     */
    private final Text centerText;

    /**
     * The text node for displaying the score in the top-right corner.
     */
    private final Text scoreText;

    /**
     * The text node for displaying the count in the bottom-right corner.
     */
    private final Text countText;
    
    /**
     * Flag indicating whether this tile currently holds a letter.
     */
    private boolean isLetter;

    /**
     * The inner shadow effect used for default (empty) tiles.
     */
    private final InnerShadow innerShadowEffect;

    /**
     * Constructs a new {@code Tile} with the specified {@link CellType}.
     * <p>
     * The tile is initialised with default values (score and count of 0, and no centre text).
     * If the tile is of type {@code NORMAL}, it is given an inner shadow effect.
     * </p>
     *
     * @param type the {@code CellType} determining the tile's visual style.
     */
    public TileView(CellType type) {
        this.type     = type;
        this.score    = 0;
        this.count    = 0;
        this.isLetter = false;
        
        setPrefSize(MAX_SIZE, MAX_SIZE);
        setMaxSize (MAX_SIZE, MAX_SIZE);
        
        innerShadowEffect = new InnerShadow();
        innerShadowEffect.setColor(Color.gray(0.5));
        innerShadowEffect.setRadius(5);
        innerShadowEffect.setOffsetY(2);
        this.setEffect(innerShadowEffect);
        
        centerText = new Text(type.getText());
        centerText.setFill(Color.BLACK);
        StackPane.setAlignment(centerText, Pos.CENTER);
        
        scoreText = new Text("");
        scoreText.setTranslateX(-2);
        scoreText.setTranslateY(2);
        StackPane.setAlignment(scoreText, Pos.TOP_RIGHT);
        
        countText = new Text("");
        countText.setTranslateX(-2);
        countText.setTranslateY(-2);
        StackPane.setAlignment(countText, Pos.BOTTOM_RIGHT);
        
        this.getChildren().addAll(centerText, scoreText, countText);
    }
    
    /**
     * Returns whether this tile currently holds a letter.
     *
     * @return true if the centre text represents a single letter, false
     * otherwise.
     */
    public boolean isLetter() {
        return isLetter;
    }
    
    /**
     * Returns the {@code CellType} of this tile.
     *
     * @return the tile's {@code CellType}.
     */
    public CellType getType() {
        return type;
    }

    /**
     * Returns the current score of this tile.
     *
     * @return the score value.
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Returns the current count of this tile.
     *
     * @return the count value.
     */
    public int getCount() {
        return count;
    }
    
    /**
     * Returns the centre text displayed on this tile.
     *
     * @return the centre text.
     */
    public String getCenterText() {
        return centerText.getText();
    }
    
    /**
     * Package-private setter for the score.
     * <p>
     * The score is displayed in the top-right corner only if its value is greater than 0.
     * </p>
     *
     * @param score the new score value.
     */
    public void setScore(int score) {
        this.score = score;
        if (score > 0) {
            scoreText.setText(String.valueOf(score));
        } else {
            scoreText.setText("");
        }
    }

    /**
     * Package-private setter for the count.
     * <p>
     * The count is displayed in the bottom-right corner only if its value is greater than 0.
     * </p>
     *
     * @param count the new count value.
     */
    public void setCount(int count) {
        this.count = count;
        if (count > 0) {
            countText.setText(String.valueOf(count));
        } else {
            countText.setText("");
        }
    }

    /**
     * Method to set the centre text for this tile.
     * <p>
     * This method is used to "place" a letter on the tile. If the provided text is a single
     * character, it is considered a letter, and the inner shadow effect (if present) is removed.
     * Otherwise, if the text length is not one (e.g. bonus labels like "DW"), the inner shadow
     * is re-applied for default tiles.
     * </p>
     *
     * @param text the text to display in the centre.
     */
    public void setCenterText(String text) {
        if (text != null && text.length() == 1) {
            setLetter(text.charAt(0), DEFAULT_LETTER_COLOR);
        } else {
            isLetter = false;
            centerText.setText(text);
        }
//            this.setEffect(innerShadowEffect);
    }
    
    /**
     * Method to set this tile to display a single letter.
     * <p>
     * This method sets the centre text to the given letter, updates its color
     * to the specified color, and removes any inner shadow effect.
     * </p>
     *
     * @param letter the letter to display on the tile.
     * @param color the color to apply to the letter.
     */
    public void setLetter(char letter, Color color) {
        centerText.setText(String.valueOf(letter));
        setTextColor(Color.BLACK);
        setBackgroundColor(color);
        innerShadowEffect.setColor(color.deriveColor(0, 1, 0.7, 1));
        
        isLetter = true;
    }

    
    /**
     * Method to set a custom font for this tile.
     * <p>
     * This method applies the provided font to the centre text and uses a
     * scaled version (with size 12) for the score and count texts.
     * </p>
     *
     * @param font the custom font to apply; must not be null.
     * @throws IllegalArgumentException if font is null.
     */
    public void setFont(Font font) {
        if (font == null) {
            throw new IllegalArgumentException("Font cannot be null.");
        }
        
        Font auxiliaryFont = Font.font(font.getFamily(), AUXILIARY_FONT_SIZE);
        
        centerText.setFont(Font.font(font.getFamily(), CENTER_FONT_SIZE));
        scoreText .setFont(auxiliaryFont);
        countText .setFont(auxiliaryFont);
    }
    
    /**
     * Method to set the text color for all text nodes of this tile.
     *
     * @param c the color to apply.
     */
    public void setTextColor(Color c) {
        centerText.setFill(c);
        scoreText.setFill(c);
        countText.setFill(c);
    }
    
    /**
     * Method to update the background color of this tile
     * while preserving its round corners.
     * <p>
     * This method updates the tile's background to use the specified color,
     * with a constant corner radius.
     * </p>
     *
     * @param color the new background color.
     * @throws IllegalArgumentException if color is null.
     */
    public void setBackgroundColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Color cannot be null.");
        }
        
        this.setBackground(new Background(
                new BackgroundFill(
                        color, new CornerRadii(DEFAULT_CORNER_RADIUS), Insets.EMPTY)));
        if (!isLetter) {
            this.setEffect(innerShadowEffect);
        }
    }
    
    @Override
    protected double computeMinWidth(double height) {
        // Can shrink to zero if the parent insists
        // but will remain square in layoutChildren().
        return 0;
    }

    @Override
    protected double computeMinHeight(double width) {
        return 0;
    }

    @Override
    protected double computePrefWidth(double height) {
        // By default, we prefer to be as large as our MAX_SIZE.
        return MAX_SIZE;
    }

    @Override
    protected double computePrefHeight(double width) {
        return MAX_SIZE;
    }

    @Override
    protected double computeMaxWidth(double height) {
        // We never want to exceed our max size.
        return MAX_SIZE;
    }

    @Override
    protected double computeMaxHeight(double width) {
        return MAX_SIZE;
    }
    
    /**
     * Returns a string representation of this tile, including its type, score, count, and whether it holds a letter.
     *
     * @return a string representation of the tile.
     */
    @Override
    public String toString() {
        return "Tile{" +
                "type=" + type +
                ", score=" + score +
                ", count=" + count +
                ", centerText='" + centerText.getText() + '\'' +
                ", isLetter=" + isLetter +
                '}';
    }
    
}