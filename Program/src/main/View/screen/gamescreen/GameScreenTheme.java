package main.View.screen.gamescreen;

import javafx.scene.paint.Color;

/**
 * Provides centralized theme constants and styling for the game screen.
 * This class contains all color definitions, style strings, and resource paths
 * used throughout the game screen UI to maintain visual consistency.
 *
 * The theme follows a medieval/fantasy aesthetic with:
 * - Warm brown and gold color scheme
 * - Drop shadow effects for depth
 * - Textured backgrounds
 * - Consistent border styling
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
class GameScreenTheme {

    // Color constants
    /** Primary tan color used for text and accents */
    private static final Color TAN_COLOR = Color.rgb(222, 184, 135);

    /** Orange color used for highlights and titles */
    private static final Color ORANGE_COLOR = Color.ORANGE;

    // Style strings
    /** CSS style for drop shadow effects */
    private static final String SHADOW_STYLE = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";

    // Resource paths
    /** Path to the pixel font resource */
    private static final String FONT_PATH = "/main/View/fonts/PixelFont.ttf";

    /** Path to the background image resource */
    private static final String BACKGROUND_PATH = "/sprites/backgrounds/brick_wall_background.png";

    // Border and panel colors
    /** Color used for panel borders (golden brown) */
    private static final String PANEL_BORDER_COLOR = "#DAA520";

    /**
     * Gets the primary tan color used for text and accents.
     *
     * @return The tan color
     */
    public Color getTanColor() {
        return TAN_COLOR;
    }

    /**
     * Gets the orange color used for highlights and titles.
     *
     * @return The orange color
     */
    public Color getOrangeColor() {
        return ORANGE_COLOR;
    }

    /**
     * Gets the CSS style string for drop shadow effects.
     *
     * @return The shadow style string
     */
    public String getShadowStyle() {
        return SHADOW_STYLE;
    }

    /**
     * Gets the path to the pixel font resource.
     *
     * @return The font resource path
     */
    public String getFontPath() {
        return FONT_PATH;
    }

    /**
     * Gets the path to the background image resource.
     *
     * @return The background image path
     */
    public String getBackgroundPath() {
        return BACKGROUND_PATH;
    }

    /**
     * Gets the color string used for panel borders.
     *
     * @return The panel border color string
     */
    public String getPanelBorderColor() {
        return PANEL_BORDER_COLOR;
    }

    /**
     * Gets the complete CSS style for the main background.
     * Includes background image with proper sizing and positioning.
     *
     * @return Complete background style string
     */
    public String getBackgroundStyle() {
        return "-fx-background-image: url('" + BACKGROUND_PATH + "'); " +
                "-fx-background-repeat: no-repeat; " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center center;";
    }

    /**
     * Gets the fallback background style when image loading fails.
     *
     * @return Fallback background style string
     */
    public String getFallbackBackgroundStyle() {
        return "-fx-background-color: #202020;";
    }

    /**
     * Gets the style for the main game area container.
     *
     * @return Main game area style string
     */
    public String getMainGameAreaStyle() {
        return "-fx-background-color: rgba(0,0,0,0.3);";
    }

    /**
     * Gets the style for stats display boxes.
     * Includes background, border, padding, and shadow effects.
     *
     * @return Stats box style string
     */
    public String getStatsBoxStyle() {
        return "-fx-background-color: rgba(139, 69, 19, 0.9); " +
                "-fx-border-color: " + PANEL_BORDER_COLOR + "; " +
                "-fx-border-width: 2px; -fx-padding: 8; " +
                "-fx-border-radius: 5px; -fx-background-radius: 5px;" +
                SHADOW_STYLE;
    }

    /**
     * Gets the style for general panel containers.
     * Includes background, border, radius, and shadow effects.
     *
     * @return Panel style string
     */
    public String getPanelStyle() {
        return "-fx-background-color: rgba(139, 69, 19, 0.9); " +
                "-fx-border-color: " + PANEL_BORDER_COLOR + "; " +
                "-fx-border-width: 3px; " +
                "-fx-border-radius: 5px; " +
                "-fx-background-radius: 5px;" +
                SHADOW_STYLE;
    }

    /**
     * Gets the style for center area containers.
     *
     * @return Center area style string
     */
    public String getCenterAreaStyle() {
        return "-fx-background-color: rgba(0,0,0,0.2);";
    }

    /**
     * Gets the base style for buttons in normal state.
     *
     * @return Button base style string
     */
    public String getButtonBaseStyle() {
        return "-fx-background-color: #444444;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";
    }

    /**
     * Gets the style for buttons in hover state.
     *
     * @return Button hover style string
     */
    public String getButtonHoverStyle() {
        return "-fx-background-color: #666666;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";
    }

    /**
     * Gets the style for message container areas.
     *
     * @return Message container style string
     */
    public String getMessageContainerStyle() {
        return "-fx-background-color: rgba(42, 42, 42, 0.9); " +
                "-fx-border-color: " + PANEL_BORDER_COLOR + "; " +
                "-fx-border-width: 3 0 0 0;" +
                SHADOW_STYLE;
    }

    /**
     * Gets the style for message scroll pane areas.
     *
     * @return Message scroll pane style string
     */
    public String getMessageScrollPaneStyle() {
        return "-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;";
    }
}