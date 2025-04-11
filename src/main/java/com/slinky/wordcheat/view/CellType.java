package com.slinky.wordcheat.view;

/**
 * Enumerates the different types of board cells in a Words With Friends-like
 * game, each associated with a specific colour code and a short text label.
 *
 * <p>
 * The {@code CellType} enum defines five constants:
 * <ul>
 *   <li>{@link #DW} (Double Word) – Typically doubles the word score.</li>
 *   <li>{@link #DL} (Double Letter) – Typically doubles the letter score.</li>
 *   <li>{@link #TW} (Triple Word) – Typically triples the word score.</li>
 *   <li>{@link #TL} (Triple Letter) – Typically triples the letter score.</li>
 *   <li>{@link #NORMAL} – A standard cell with no bonus.</li>
 * </ul>
 * Each constant is associated with an integer colour code in {@code 0xRRGGBB}
 * format and a short text label.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>
 *     CellType type  = CellType.DW;
 *     int colourCode = type.getColor();
 *     // colourCode is 0x780000 in this case
 * </pre>
 * </p>
 *
 * <p>
 * Additional methods are provided to retrieve individual colour components
 * (red, green, blue), to obtain the text label, and to convert the colour code
 * into a hexadecimal string.
 * </p>
 *
 * @author Kheagen Haskins
 */
public enum CellType {

    /**
     * Double Word cell.
     * <p>
     * Associated colour code is {@code 0x780000} and text label is "DW".
     * </p>
     */
    DW(0xF45B69, "DW"),

    /**
     * Double Letter cell.
     * <p>
     * Associated colour code is {@code 0x003049} and text label is "DL".
     * </p>
     */
    DL(0x23CE6B, "DL"),

    /**
     * Triple Word cell.
     * <p>
     * Associated colour code is {@code 0xc1121f} and text label is "TW".
     * </p>
     */
    TW(0xFF8552, "TW"),

    /**
     * Triple Letter cell.
     * <p>
     * Associated colour code is {@code 0x669bbc} and text label is "TL".
     * </p>
     */
    TL(0x2A90CB, "TL"),

    /**
     * Normal cell with no special bonus.
     * <p>
     * Associated colour code is {@code 0xfdf0d5} and an empty text label.
     * </p>
     */
    NORMAL(0xDDDDDD, "");

    /**
     * The integer colour code (in {@code 0xRRGGBB} format) for this cell type.
     */
    private final int color;
    
    /**
     * The short text label for this cell type.
     */
    private final String text;

    /**
     * Constructs a {@code CellType} with the specified integer colour code and
     * text label.
     *
     * @param color the colour code in {@code 0xRRGGBB} format.
     * @param text the text label to associate with this cell type.
     */
    private CellType(int color, String text) {
        this.color = color;
        this.text  = text;
    }

    /**
     * Returns the integer colour code associated with this cell type.
     * <p>
     * The returned value is in {@code 0xRRGGBB} format.
     * </p>
     *
     * @return the integer colour code.
     */
    public int getColor() {
        return color;
    }
    
    /**
     * Returns the red component of the colour.
     *
     * @return the red component (0-255).
     */
    public int getRed() {
        return (color >> 16) & 0xFF;
    }
    
    /**
     * Returns the blue component of the colour.
     *
     * @return the blue component (0-255).
     */
    public int getBlue() {
        return (color >> 8) & 0xFF;
    }
    
    /**
     * Returns the green component of the colour.
     *
     * @return the green component (0-255).
     */
    public int getGreen() {
        return color & 0xFF;
    }
    
    /**
     * Returns the text label associated with this cell type.
     *
     * @return the text label.
     */
    public String getText() {
        return text;
    }
    
}