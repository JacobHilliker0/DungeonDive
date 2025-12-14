package main.View.screen.gamescreen;

/**
 * Manages canvas dimensions and game element sizing for the game screen.
 * This class provides a centralized way to handle responsive sizing of the game canvas
 * and all related UI elements, ensuring consistent scaling across different screen sizes.
 *
 * The class calculates sizes for:
 * - Canvas dimensions
 * - Tile sizes for the game grid
 * - Hero sprite size
 * - Wall thickness and door widths
 * - Boundary positions for player movement
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
public class GameScreenCanvasDimensions {

    /** Current canvas size (width and height are equal for square canvas) */
    private double size;

    /** Size of each individual tile in the game grid */
    private double tileSize;

    /** Size of the hero sprite */
    private double heroSize;

    /** Thickness of walls around the game area */
    private double wallThickness;

    /** Width of doors in walls */
    private double doorWidth;

    // Configuration constants for sizing calculations
    /** Minimum allowed canvas size in pixels */
    private static final double CANVAS_MIN_SIZE = 300.0;

    /** Maximum allowed canvas size in pixels */
    private static final double CANVAS_MAX_SIZE = 800.0;

    /** Number of tiles that fit across the canvas */
    private static final double TILES_PER_CANVAS = 30.0;

    /** Multiplier for hero size relative to tile size */
    private static final double HERO_SIZE_MULTIPLIER = 2.0;

    /** Multiplier for door width relative to wall thickness */
    private static final double DOOR_WIDTH_MULTIPLIER = 2.0;

    /**
     * Constructs a new GameScreenCanvasDimensions with default size.
     * Initializes the canvas to a default size of 480 pixels.
     */
    public GameScreenCanvasDimensions() {
        updateSize(480.0); // Default size
    }

    /**
     * Updates all dimensions based on the new canvas size.
     * This method recalculates all related dimensions when the canvas size changes,
     * ensuring proper scaling of all game elements.
     *
     * @param canvasSize The new canvas size in pixels (will be clamped to min/max bounds)
     */
    public void updateSize(double canvasSize) {
        this.size = Math.max(CANVAS_MIN_SIZE, Math.min(CANVAS_MAX_SIZE, canvasSize));
        this.tileSize = this.size / TILES_PER_CANVAS;
        this.heroSize = tileSize * HERO_SIZE_MULTIPLIER;
        this.wallThickness = tileSize;
        this.doorWidth = wallThickness * DOOR_WIDTH_MULTIPLIER;
    }

    /**
     * Gets the current canvas size.
     *
     * @return The canvas size in pixels (square canvas, so width equals height)
     */
    public double getSize() {
        return size;
    }

    /**
     * Gets the size of each tile in the game grid.
     *
     * @return The tile size in pixels
     */
    public double getTileSize() {
        return tileSize;
    }

    /**
     * Gets the size of the hero sprite.
     *
     * @return The hero size in pixels
     */
    public double getHeroSize() {
        return heroSize;
    }

    /**
     * Gets the thickness of walls around the game area.
     *
     * @return The wall thickness in pixels
     */
    public double getWallThickness() {
        return wallThickness;
    }

    /**
     * Gets the width of doors in walls.
     *
     * @return The door width in pixels
     */
    public double getDoorWidth() {
        return doorWidth;
    }

    /**
     * Gets the top boundary position for player movement.
     * This represents the Y coordinate where the player can start moving vertically.
     *
     * @return The top boundary Y coordinate in pixels
     */
    public double getBoundaryTop() {
        return wallThickness;
    }

    /**
     * Gets the bottom boundary position for player movement.
     * This represents the maximum Y coordinate where the player can move.
     *
     * @return The bottom boundary Y coordinate in pixels
     */
    public double getBoundaryBottom() {
        return size - wallThickness - heroSize;
    }

    /**
     * Gets the left boundary position for player movement.
     * This represents the X coordinate where the player can start moving horizontally.
     *
     * @return The left boundary X coordinate in pixels
     */
    public double getBoundaryLeft() {
        return wallThickness;
    }

    /**
     * Gets the right boundary position for player movement.
     * This represents the maximum X coordinate where the player can move.
     *
     * @return The right boundary X coordinate in pixels
     */
    public double getBoundaryRight() {
        return size - wallThickness - heroSize;
    }
}