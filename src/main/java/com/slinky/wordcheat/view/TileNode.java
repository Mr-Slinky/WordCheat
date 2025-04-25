package com.slinky.wordcheat.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;

/**
 * Represents a single tile within the word‑cheat application, used both on the
 * game board and in the player’s rack. Each TileNode maintains both logical
 * state (letter, score, bonus text, wildcard flag) and visual style state
 * (colours, corner radius, fonts).
 *
 * <p>
 * This class is fully programmatic and avoids any reliance on external CSS or
 * FXML. It composes a {@link Rectangle} as a background and three {@link Label}
 * children: one for the letter, one for the score, and one for a bonus
 * indicator. All visual aspects—such as 
 * {@linkplain #setBackgroundFill(Color) background fill},
 * {@linkplain #setWildcardFill(Color) wildcard fill}, {@linkplain #setCornerRadius(double)
 * corner radius}, and fonts for the {@linkplain #getLetterLabel() letter} and
 * {@linkplain #getScoreLabel() score} labels—are exposed via setters. Logical
 * state methods ({@linkplain #setLetter(char)}, {@linkplain #setScore(int)},
 * {@linkplain #setBonus(String)}, {@linkplain #setWildcard(boolean)}) determine
 * which text or bonus symbol appears. After changing either style or logical
 * state, clients must invoke {@linkplain #applyStyle()} to reapply visual
 * parameters or {@linkplain #refresh()} to update displayed text and wildcard
 * fill.
 * </p>
 *
 * <p>
 * Since this is a JavaFX node, all modifications must occur on the JavaFX
 * Application Thread. The default size of each tile is governed by
 * {@value #DEFAULT_SIZE}, but clients may supply a custom size via the
 * {@link #TileNode(double)} constructor. Internally, children are stacked with
 * the bonus label and letter label centred and the score label aligned to the
 * top‑right corner.
 * </p>
 */
public final class TileNode extends StackPane {

    // =============================[ Static ]============================== \\
    private static final double DEFAULT_SIZE = 34;

    // =============================[ Fields ]============================== \\
    // --- child nodes ---
    private final Rectangle background;
    private final Label     letterLbl;
    private final Label     scoreLbl;
    private final Label     bonusLbl;
    private final Label     countLbl;

    // --- logical state ---
    private char    letter   = ' ';
    private int     score    = 0;
    private String  bonus    = null;
    private boolean wildcard = false;
    private int     count    = -1;

    // --- style state with defaults WS(externally mutable) ---
    private Color  backgroundFill = Color.web("#ECEFF1");
    private Color  wildcardFill   = Color.web("#FFE082");
    private double cornerRadius   = 6;
    private Font   letterFont     = Font.font("Inter", 18);
    private Font   bonusFont      = Font.font("Inter", 16);
    private Font   smallFont      = Font.font("Inter", 10);
    private Color  textFill       = Color.BLACK;
    
    // ==========================[ Constructors ]=========================== \\
    /**
     * Create a tile with the default size.
     */
    public TileNode() {
        this(DEFAULT_SIZE);
    }

    /**
     * Create a tile with the given side length.
     *
     * @param size the width and height of the tile in pixels
     */
    public TileNode(double size) {
        setMinSize(size, size);
        setMaxSize(size, size);

        background = new Rectangle(size, size);
        background.setStroke(Color.web("#CFD8DC"));
        background.setStrokeWidth(1);

        letterLbl = new Label();
        scoreLbl  = new Label();
        bonusLbl  = new Label();
        countLbl  = new Label();
        
        StackPane.setAlignment(scoreLbl, Pos.TOP_RIGHT);
        StackPane.setAlignment(countLbl, Pos.TOP_LEFT);

        getChildren().addAll(background, bonusLbl, letterLbl, scoreLbl, countLbl);

        applyStyle();
        refresh();
    }

    // ========================[ Accessor Methods ]========================= \\
    /**
     * Get the letter displayed on this tile.
     *
     * @return the letter, or a space if none is set
     */
    public char getLetter() { return letter; }

    /**
     * Get the score value of this tile.
     *
     * @return the score, or 0 if none is set
     */
    public int getScore() { return score; }

    /**
     * Get the bonus text shown when no letter is present.
     *
     * @return the bonus text, or null if none is set
     */
    public String getBonus() { return bonus; }
    
    /**
     * Get the count that will display on the TileSetView.
     * 
     * @return the count
     */
    public int getCount() { return count; }

    /**
     * Check if this tile is a wildcard.
     *
     * @return true if wildcard, false otherwise
     */
    public boolean isWildcard() { return wildcard; }

    /**
     * Get the Rectangle node used as the background.
     *
     * @return the background Rectangle node
     */
    public Rectangle getBackgroundNode() { return background; }

    /**
     * Get the Label node used for the letter.
     *
     * @return the letter Label node
     */
    public Label getLetterLabel() { return letterLbl; }

    /**
     * Get the Label node used for the score.
     *
     * @return the score Label node
     */
    public Label getScoreLabel() { return scoreLbl; }

    /**
     * Get the Label node used for the bonus text.
     *
     * @return the bonus Label node
     */
    public Label getBonusLabel() { return bonusLbl; }

    // ========================[ Mutator Methods ]========================== \\
    /**
     * Set the letter for this tile.
     *
     * @param l the letter to display, or ' ' to clear
     */
    public void setLetter(char l) {
        this.letter = l;
    }

    /**
     * Set the score for this tile.
     *
     * @param s the score value
     */
    public void setScore(int s) {
        this.score = s;
    }

    /**
     * Set the bonus text for this tile when empty.
     *
     * @param b the bonus text, or null to clear
     */
    public void setBonus(String b) {
        this.bonus = b;
    }

    /**
     * Set this tile as a wildcard or standard.
     *
     * @param w true to mark as wildcard, false otherwise
     */
    public void setWildcard(boolean w) {
        this.wildcard = w;
    }

    /**
     * Set the fill colour used for the background when not a wildcard.
     *
     * @param c the background fill colour
     */
    public void setBackgroundFill(Color c) {
        this.backgroundFill = c;
    }

    /**
     * Set the fill colour used for the background when a wildcard.
     *
     * @param c the wildcard fill colour
     */
    public void setWildcardFill(Color c) {
        this.wildcardFill = c;
    }

    /**
     * Set the corner radius for the tile background.
     *
     * @param r the corner radius in pixels
     */
    public void setCornerRadius(double r) {
        this.cornerRadius = r;
    }

    /**
     * Set the font used for the letter and bonus labels.
     *
     * @param f the Font to use
     */
    public void setLetterFont(Font f) {
        this.letterFont = f;
    }

    /**
     * Set the font used for the score label.
     *
     * @param f the Font to use
     */
    public void setSmallFont(Font f) {
        this.smallFont = f;
    }
    
    /**
     * Set the font used for the bonus label.
     *
     * @param f the Font to use
     */
    public void setBonusFont(Font f) {
        this.bonusFont = f;
    }
    
    /**
     * Set the foreground (text) color for letter, score, and bonus labels.
     */
    public void setTextFill(Color c) {
        this.textFill = c;
    }
    
    /**
     *  Set the count that will display on the TileSetView.
     * 
     * @param count the new count
     */
    public void setCount(int count) {
        this.count = count;
    }
    
    // ==========================[ API Methods ]============================ \\
    /**
     * Apply visual styles based on current style state.
     *
     * Must be called after changing style properties to take effect.
     */
    public void applyStyle() {
        background.setArcWidth(cornerRadius);
        background.setArcHeight(cornerRadius);
        background.setFill(wildcard ? wildcardFill : backgroundFill);

        letterLbl.setFont(letterFont);
        bonusLbl .setFont(bonusFont);
        scoreLbl .setFont(smallFont);
        countLbl .setFont(smallFont);
        
        letterLbl.setTextFill(textFill);
        bonusLbl .setTextFill(textFill);
        scoreLbl .setTextFill(textFill);
        countLbl .setTextFill(textFill);
    }

    /**
     * Refresh the displayed text and background fill based on current logical
     * state.
     *
     * Must be called after setLetter, setScore, setBonus or setWildcard.
     */
    public void refresh() {
        boolean hasLetter = !(letter < 'A' || letter > 'Z');

        letterLbl.setText(hasLetter ? String.valueOf(letter) : "");
        scoreLbl .setText(hasLetter && !wildcard && score > 0 ? String.valueOf(score) : "");
        bonusLbl .setText(hasLetter || bonus == null ? "" : bonus);
        countLbl .setText(count >= 0 ? String.valueOf(count) : "");

        background.setFill(wildcard ? wildcardFill : backgroundFill);
    }

}