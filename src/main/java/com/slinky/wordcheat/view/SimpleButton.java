package com.slinky.wordcheat.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * A customised button with vertical gradients, hover/focus shadow, and active effect.
 */
public class SimpleButton extends Button {

    // ==============================[ Fields ]============================== \\
    private final Background normalBackground;
    private final Background hoverBackground;
    private final Background activeBackground;
    private final DropShadow shadowEffect;
    // ===========================[ Constructors ]=========================== \\
    /**
     * Constructs a SimpleButton with the given label text.
     *
     * @param text the text to display on the button
     */
    public SimpleButton(String text) {
        super(text);

        // Define colours from constants
        Color base        = ColorConstants.BUTTON_COLOR;
        Color hoverColor  = ColorConstants.BUTTON_HOVER_COLOR;
        Color activeColor = ColorConstants.BUTTON_ACTIVE_COLOR;

        // Rounded corners radius
        CornerRadii radii = new CornerRadii(5);

        // Background gradients
        normalBackground = createGradientBackground(base, radii);
        hoverBackground  = createGradientBackground(hoverColor, radii);
        activeBackground = createGradientBackground(activeColor, radii);

        // Drop shadow for hover and focus
        shadowEffect = new DropShadow(5, base.darker());
        shadowEffect.setOffsetY(2);
        
        init();
    }
    // =========================[ Accessor Methods ]========================= \\

    // =========================[ Mutator Methods ]========================== \\
    // ===========================[ API Methods ]============================ \\
    // ==========================[ Helper Methods ]========================== \\
    /**
     * Initialise default styles and behaviour.
     */
    private void init() {
        setBackground(normalBackground);
        setFont(FontConstants.LABEL_FONT_DEFAULT);
        setPadding(new Insets(5));
        setEffect(null);

        // Hover handling: background + shadow + focus transfer
        setOnMouseEntered(e -> {
            if (!isPressed()) {
                setBackground(hoverBackground);
            }
            setEffect(shadowEffect);
            requestFocus();
        });
        
        setOnMouseExited(e -> {
            if (!isPressed()) {
                setBackground(normalBackground);
            }
            if (!isFocused()) {
                setEffect(null);
            }
        });

        // Mouse click handling: active background + shadow
        setOnMousePressed(e -> {
            setBackground(activeBackground);
            setEffect(shadowEffect);
        });
        
        setOnMouseReleased(e -> {
            if (isHover()) {
                setBackground(hoverBackground);
            } else {
                setBackground(normalBackground);
            }
            
            if (!isFocused() && !isHover()) {
                setEffect(null);
            }
        });

        // Keyboard activation (space or enter)
        addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                setBackground(activeBackground);
                setEffect(shadowEffect);
            }
        });
        
        addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                if (isHover()) {
                    setBackground(hoverBackground);
                } else {
                    setBackground(normalBackground);
                }
                
                if (!isFocused() && !isHover()) {
                    setEffect(null);
                }
            }
        });

        // Focus handling: shadow only
        focusedProperty().addListener((obs, oldF, newF) -> {
            if (newF) {
                setEffect(shadowEffect);
            } else if (!isHover()) {
                setEffect(null);
            }
        });
    }

    /**
     * Helper to create a vertical gradient background from a base color.
     */
    private Background createGradientBackground(Color color, CornerRadii radii) {
        LinearGradient gradient = new LinearGradient(
            0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, color),
            new Stop(1, color.brighter())
        );
        return new Background(new BackgroundFill(gradient, radii, Insets.EMPTY));
    }

}