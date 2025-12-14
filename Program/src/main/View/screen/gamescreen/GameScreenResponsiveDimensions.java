package main.View.screen.gamescreen;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Scene;

/**
 * Manages responsive dimensions and scaling for game screen UI elements.
 * This class creates and maintains JavaFX property bindings that automatically
 * adjust UI element sizes based on the current scene dimensions.
 *
 * Provides responsive bindings for:
 * - Panel widths and padding
 * - Button dimensions
 * - Font sizes for different UI elements
 * - Icon and image sizes
 * - Minimap and portrait dimensions
 *
 * All bindings are calculated as ratios of the scene dimensions with
 * configurable minimum and maximum constraints to ensure usability
 * across different screen sizes.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
class GameScreenResponsiveDimensions {

    /** The scene to bind dimensions to */
    private final Scene scene;

    // Binding properties for various UI elements
    private final NumberBinding panelWidthBinding;
    private final NumberBinding panelPaddingBinding;
    private final NumberBinding buttonHeightBinding;
    private final NumberBinding minimapSizeBinding;
    private final NumberBinding statsFontSizeBinding;
    private final NumberBinding titleFontSizeBinding;
    private final NumberBinding messageFontSizeBinding;
    private final NumberBinding buttonFontSizeBinding;
    private final NumberBinding statsIconSizeBinding;
    private final NumberBinding portraitSizeBinding;

    // Configuration constants for responsive calculations

    /** Panel width as ratio of scene width */
    private static final double PANEL_PREF_WIDTH_RATIO = 0.14;
    /** Minimum panel width in pixels */
    private static final double PANEL_MIN_WIDTH_ABSOLUTE = 160.0;
    /** Maximum panel width in pixels */
    private static final double PANEL_MAX_WIDTH_ABSOLUTE = 220.0;

    /** Panel padding as ratio of scene width */
    private static final double PANEL_PADDING_RATIO = 0.008;

    /** Button height as ratio of scene height */
    private static final double BUTTON_HEIGHT_RATIO = 0.045;
    /** Minimum button height in pixels */
    private static final double BUTTON_MIN_HEIGHT = 30.0;
    /** Maximum button height in pixels */
    private static final double BUTTON_MAX_HEIGHT = 45.0;

    /** Minimap size as ratio of smaller scene dimension */
    private static final double MINIMAP_SIZE_RATIO = 0.08;
    /** Minimum minimap size in pixels */
    private static final double MINIMAP_MIN_SIZE = 70.0;
    /** Maximum minimap size in pixels */
    private static final double MINIMAP_MAX_SIZE = 120.0;

    /** Portrait size as ratio of smaller scene dimension */
    private static final double PORTRAIT_SIZE_RATIO = 0.1;
    /** Minimum portrait size in pixels */
    private static final double PORTRAIT_MIN_SIZE = 80.0;
    /** Maximum portrait size in pixels */
    private static final double PORTRAIT_MAX_SIZE = 150.0;

    /** Stats font size as ratio of scene height */
    private static final double STATS_FONT_SIZE_RATIO = 0.012;
    /** Minimum stats font size in pixels */
    private static final double STATS_FONT_MIN_SIZE = 10.0;
    /** Maximum stats font size in pixels */
    private static final double STATS_FONT_MAX_SIZE = 20.0;

    /** Stats icon size as ratio of scene height */
    private static final double STATS_ICON_SIZE_RATIO = 0.009;
    /** Minimum stats icon size in pixels */
    private static final double STATS_ICON_MIN_SIZE = 12.0;
    /** Maximum stats icon size in pixels */
    private static final double STATS_ICON_MAX_SIZE = 20.0;

    /** Title font size as ratio of scene height */
    private static final double TITLE_FONT_SIZE_RATIO = 0.020;

    /** Message font size as ratio of scene height */
    private static final double MESSAGE_FONT_SIZE_RATIO = 0.007;

    /** Button font size as ratio of scene height */
    private static final double BUTTON_FONT_SIZE_RATIO = 0.018;
    /** Minimum button font size in pixels */
    private static final double BUTTON_FONT_MIN_SIZE = 8.0;
    /** Maximum button font size in pixels */
    private static final double BUTTON_FONT_MAX_SIZE = 16.0;

    /**
     * Constructs responsive dimensions manager for the given scene.
     * Creates all necessary property bindings based on scene dimensions.
     *
     * @param scene The scene to create responsive bindings for
     */
    public GameScreenResponsiveDimensions(Scene scene) {
        this.scene = scene;

        // Panel width binding with constraints
        panelWidthBinding = Bindings.createDoubleBinding(() -> {
            double sceneWidth = scene.getWidth();
            double calculated = sceneWidth * PANEL_PREF_WIDTH_RATIO;
            return Math.max(PANEL_MIN_WIDTH_ABSOLUTE, Math.min(PANEL_MAX_WIDTH_ABSOLUTE, calculated));
        }, scene.widthProperty());

        // Panel padding binding with constraints
        panelPaddingBinding = Bindings.createDoubleBinding(() -> {
            double basePadding = scene.getWidth() * PANEL_PADDING_RATIO;
            return Math.max(5.0, Math.min(15.0, basePadding));
        }, scene.widthProperty());

        // Button height binding with constraints
        buttonHeightBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getHeight() * BUTTON_HEIGHT_RATIO;
            return Math.max(BUTTON_MIN_HEIGHT, Math.min(BUTTON_MAX_HEIGHT, calculated));
        }, scene.heightProperty());

        // Minimap size binding (based on smaller dimension)
        minimapSizeBinding = Bindings.createDoubleBinding(() -> {
            double smallerDimension = Math.min(scene.getWidth(), scene.getHeight());
            double calculated = smallerDimension * MINIMAP_SIZE_RATIO;
            return Math.max(MINIMAP_MIN_SIZE, Math.min(MINIMAP_MAX_SIZE, calculated));
        }, scene.widthProperty(), scene.heightProperty());

        // Portrait size binding (based on smaller dimension)
        portraitSizeBinding = Bindings.createDoubleBinding(() -> {
            double smallerDimension = Math.min(scene.getWidth(), scene.getHeight());
            double calculated = smallerDimension * PORTRAIT_SIZE_RATIO;
            return Math.max(PORTRAIT_MIN_SIZE, Math.min(PORTRAIT_MAX_SIZE, calculated));
        }, scene.widthProperty(), scene.heightProperty());

        // Stats font size binding with constraints
        statsFontSizeBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getHeight() * STATS_FONT_SIZE_RATIO;
            return Math.max(STATS_FONT_MIN_SIZE, Math.min(STATS_FONT_MAX_SIZE, calculated));
        }, scene.heightProperty());

        // Stats icon size binding with constraints
        statsIconSizeBinding = Bindings.createDoubleBinding(() -> {
            double calculated = scene.getHeight() * STATS_ICON_SIZE_RATIO;
            return Math.max(STATS_ICON_MIN_SIZE, Math.min(STATS_ICON_MAX_SIZE, calculated));
        }, scene.heightProperty());

        // Title font size binding
        titleFontSizeBinding = Bindings.createDoubleBinding(() ->
                Math.max(20.0, scene.getHeight() * TITLE_FONT_SIZE_RATIO), scene.heightProperty());

        // Message font size binding
        messageFontSizeBinding = Bindings.createDoubleBinding(() ->
                Math.max(9.0, scene.getHeight() * MESSAGE_FONT_SIZE_RATIO), scene.heightProperty());

        // Button font size binding (considers both width and height)
        buttonFontSizeBinding = Bindings.createDoubleBinding(() -> {
            double sceneHeight = scene.getHeight();
            double sceneWidth = scene.getWidth();
            double heightBasedSize = sceneHeight * BUTTON_FONT_SIZE_RATIO;
            double widthBasedSize = sceneWidth * (BUTTON_FONT_SIZE_RATIO * 0.5);
            double calculated = Math.min(heightBasedSize, widthBasedSize);
            return Math.max(BUTTON_FONT_MIN_SIZE, Math.min(BUTTON_FONT_MAX_SIZE, calculated));
        }, scene.heightProperty(), scene.widthProperty());
    }

    // Getter methods for accessing the bindings

    /**
     * Gets the scene this dimensions manager is bound to.
     *
     * @return The scene object
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Gets the panel width binding.
     *
     * @return NumberBinding for panel width
     */
    public NumberBinding getPanelWidthBinding() {
        return panelWidthBinding;
    }

    /**
     * Gets the panel padding binding.
     *
     * @return NumberBinding for panel padding
     */
    public NumberBinding getPanelPaddingBinding() {
        return panelPaddingBinding;
    }

    /**
     * Gets the button height binding.
     *
     * @return NumberBinding for button height
     */
    public NumberBinding getButtonHeightBinding() {
        return buttonHeightBinding;
    }

    /**
     * Gets the minimap size binding.
     *
     * @return NumberBinding for minimap size
     */
    public NumberBinding getMinimapSizeBinding() {
        return minimapSizeBinding;
    }

    /**
     * Gets the portrait size binding.
     *
     * @return NumberBinding for portrait size
     */
    public NumberBinding getPortraitSizeBinding() {
        return portraitSizeBinding;
    }

    /**
     * Gets the stats font size binding.
     *
     * @return NumberBinding for stats font size
     */
    public NumberBinding getStatsFontSizeBinding() {
        return statsFontSizeBinding;
    }

    /**
     * Gets the stats icon size binding.
     *
     * @return NumberBinding for stats icon size
     */
    public NumberBinding getStatsIconSizeBinding() {
        return statsIconSizeBinding;
    }

    /**
     * Gets the title font size binding.
     *
     * @return NumberBinding for title font size
     */
    public NumberBinding getTitleFontSizeBinding() {
        return titleFontSizeBinding;
    }

    /**
     * Gets the message font size binding.
     *
     * @return NumberBinding for message font size
     */
    public NumberBinding getMessageFontSizeBinding() {
        return messageFontSizeBinding;
    }

    /**
     * Gets the button font size binding.
     *
     * @return NumberBinding for button font size
     */
    public NumberBinding getButtonFontSizeBinding() {
        return buttonFontSizeBinding;
    }
}