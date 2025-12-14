package main.View.screen.gamescreen;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import main.Controller.Controller;
import main.Model.character.Hero;
import main.Model.dungeon.Room;
import main.Model.element.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handles all rendering operations for the game screen's main canvas.
 *
 * <p>This class is responsible for drawing the dungeon rooms, including floors, walls, doors,
 * pillars, chests, and the player character. It manages sprite loading, animations, and
 * collectible interactions while providing a responsive rendering system that adapts to
 * different canvas sizes.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Responsive canvas rendering with automatic scaling</li>
 *   <li>Sprite-based pillar and chest rendering with fallback graphics</li>
 *   <li>Animated glow effects for interactive objects</li>
 *   <li>Collision detection for collectible items</li>
 *   <li>Checkerboard floor pattern generation</li>
 *   <li>Dynamic wall and door rendering based on room connectivity</li>
 * </ul>
 *
 * <p>The renderer works in conjunction with {@link GameScreenCanvasDimensions} to ensure
 * all graphical elements scale appropriately with the canvas size.</p>
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 *
 * @see GameScreenCanvasDimensions
 * @see GameScreenGameLoop
 * @see GameScreenUIManager
 */
public class GameScreenRenderer {

    // ====== CORE DEPENDENCIES ======

    /**
     * The main application controller for accessing game state
     */
    private final Controller controller;

    /**
     * Canvas dimension calculator for responsive rendering
     */
    private final GameScreenCanvasDimensions canvasDimensions;

    // ====== CANVAS AND GRAPHICS CONTEXT ======

    /**
     * The main rendering canvas for the game room
     */
    private Canvas roomCanvas;

    /**
     * Graphics context for drawing operations on the room canvas
     */
    private GraphicsContext graphicsContext;

    /**
     * Canvas for rendering the minimap (currently unused in this class)
     */
    private Canvas minimapCanvas;

    /**
     * Graphics context for minimap rendering (currently unused in this class)
     */
    private GraphicsContext minimapGraphics;

    /**
     * Canvas for rendering character portrait (currently unused in this class)
     */
    private Canvas portraitCanvas;

    /**
     * Graphics context for portrait rendering (currently unused in this class)
     */
    private GraphicsContext portraitGraphics;

    // ====== SPRITES AND VISUAL ASSETS ======

    /**
     * Sprite image for pillar objects
     */
    private Image pillarSprite;

    /**
     * Sprite image for treasure chest objects
     */
    private Image chestSprite;

    // ====== ANIMATION STATE ======

    /**
     * Current animation phase for pillar glow effect (0 to 2π)
     */
    private double pillarGlowAnimation = 0.0;

    /**
     * Current animation phase for chest glow effect (0 to 2π)
     */
    private double chestGlowAnimation = 0.0;

    // ====== COLLECTION STATE TRACKING ======

    /**
     * Prevents multiple chest collection attempts in the same frame
     */
    private boolean hasTriedChestCollection = false;

    /**
     * Tracks if the insufficient gold warning has been shown for current chest
     */
    private boolean hasShownGoldWarning = false;

    // ====== RENDERING CONSTANTS ======

    /**
     * Speed multiplier for pillar glow animation
     */
    private static final double PILLAR_GLOW_SPEED = 0.05;

    /**
     * Speed multiplier for chest glow animation
     */
    private static final double CHEST_GLOW_SPEED = 0.04;

    /**
     * Maximum distance in pixels for pillar collection detection
     */
    private static final double PILLAR_COLLECTION_DISTANCE = 30.0;

    /**
     * Maximum distance in pixels for chest collection detection
     */
    private static final double CHEST_COLLECTION_DISTANCE = 40.0;

    /**
     * Constructs a new GameScreenRenderer with the specified dependencies.
     *
     * @param controller       The main application controller for accessing game state
     * @param canvasDimensions The dimension calculator for responsive rendering
     * @throws IllegalArgumentException if controller or canvasDimensions is null
     */
    public GameScreenRenderer(Controller controller, GameScreenCanvasDimensions canvasDimensions) {
        if (controller == null) {
            throw new IllegalArgumentException("Controller cannot be null");
        }
        if (canvasDimensions == null) {
            throw new IllegalArgumentException("CanvasDimensions cannot be null");
        }

        this.controller = controller;
        this.canvasDimensions = canvasDimensions;
    }

    /**
     * Initializes the renderer with the given scene for responsive bindings.
     *
     * <p>This method creates the main room canvas and sets up responsive bindings
     * that automatically adjust the canvas size based on the scene dimensions.
     * The canvas is constrained to maintain aspect ratio while fitting within
     * the available space.</p>
     *
     * @param scene The JavaFX scene containing the game interface
     * @throws IllegalArgumentException if scene is null
     */
    public void initialize(Scene scene) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }

        roomCanvas = new Canvas(480.0, 480.0); // DEFAULT_CANVAS_SIZE
        graphicsContext = roomCanvas.getGraphicsContext2D();
        setupCanvasBindings(scene);
    }

    /**
     * Sets up responsive bindings for canvas size based on scene dimensions.
     *
     * <p>The canvas size is calculated to fit within the available space while
     * maintaining a square aspect ratio. The bindings ensure the canvas
     * automatically resizes when the window is resized.</p>
     *
     * @param scene The scene to bind canvas dimensions to
     */
    private void setupCanvasBindings(Scene scene) {
        // Canvas responsive bindings
        javafx.beans.binding.NumberBinding availableWidth = scene.widthProperty()
                .subtract(280) // Approximate panel widths
                .subtract(40);
        javafx.beans.binding.NumberBinding availableHeight = scene.heightProperty().multiply(0.70);
        javafx.beans.binding.NumberBinding canvasSizeBinding = javafx.beans.binding.Bindings.max(300.0,
                javafx.beans.binding.Bindings.min(availableWidth, availableHeight));

        roomCanvas.widthProperty().bind(canvasSizeBinding);
        roomCanvas.heightProperty().bind(canvasSizeBinding);
        roomCanvas.widthProperty().addListener((obs, oldVal, newVal) -> scaleCanvas());
    }

    /**
     * Configures the canvas for initial rendering.
     *
     * <p>This method should be called after initialization to ensure
     * the canvas is properly sized and ready for rendering operations.</p>
     */
    public void setupCanvas() {
        scaleCanvas();
    }

    /**
     * Updates canvas dimensions and scales all game elements accordingly.
     *
     * <p>This method recalculates all dimension-dependent values when the canvas
     * size changes. It also updates the hero's movement speed to maintain
     * consistent gameplay across different canvas sizes.</p>
     *
     * <p>The rendering is performed on the JavaFX Application Thread to ensure
     * thread safety with UI updates.</p>
     */
    public void scaleCanvas() {
        canvasDimensions.updateSize(roomCanvas.getWidth());

        Hero player = controller.getPlayer();
        if (player != null) {
            player.setMovementSpeedForCanvasSize(canvasDimensions.getSize());
        }

        Platform.runLater(this::renderRoom);
    }

    /**
     * Loads all sprite assets required for rendering.
     *
     * <p>This method attempts to load pillar and chest sprites from the
     * resources directory. If loading fails, the renderer will fall back
     * to procedurally generated graphics during rendering.</p>
     *
     * <p>Should be called before any rendering operations that require sprites.</p>
     */
    public void loadSprites() {
        loadPillarSprite();
        loadChestSprite();
    }

    /**
     * Loads the pillar sprite from resources.
     *
     * <p>Attempts to load the pillar sprite from "/sprites/icons/pillar.png".
     * If loading fails, pillarSprite will be null and fallback rendering
     * will be used.</p>
     */
    private void loadPillarSprite() {
        try {
            pillarSprite = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/sprites/icons/pillar.png")));
        } catch (Exception e) {
            System.err.println("Could not load pillar sprite: " + e.getMessage());
            pillarSprite = null;
        }
    }

    /**
     * Loads the chest sprite from resources.
     *
     * <p>Attempts to load the chest sprite from "/sprites/icons/chest.png".
     * If loading fails, chestSprite will be null and fallback rendering
     * will be used.</p>
     */
    private void loadChestSprite() {
        try {
            chestSprite = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/sprites/icons/chest.png")));
        } catch (Exception e) {
            System.err.println("Could not load chest sprite: " + e.getMessage());
            chestSprite = null;
        }
    }

    /**
     * Initializes the hero's starting position when entering a room.
     *
     * <p>If the hero's pixel position is at the origin (0,0), this method
     * positions them at the bottom entrance of the room. This is typically
     * called when starting a new game or transitioning between rooms.</p>
     */
    public void initializeHeroPosition() {
        Hero player = controller.getPlayer();
        if (player != null && player.getPixelX() == 0 && player.getPixelY() == 0) {
            // Place the player at the bottom entrance of the first room instead of the center.
            double startX = canvasDimensions.getSize() / 2 - canvasDimensions.getHeroSize() / 2;
            double startY = canvasDimensions.getBoundaryBottom() - 1;
            player.setPixelPosition(startX, startY);
        }
    }

    /**
     * Renders the complete room scene including all visual elements.
     *
     * <p>This is the main rendering method that draws the entire room in layers:</p>
     * <ol>
     *   <li>Clear the canvas with background color</li>
     *   <li>Draw the checkerboard floor pattern</li>
     *   <li>Draw walls and doors based on room connectivity</li>
     *   <li>Draw interactive objects (pillars, chests)</li>
     *   <li>Draw the player character</li>
     * </ol>
     *
     * <p>If the graphics context is not available, this method returns early
     * without performing any rendering operations.</p>
     */
    public void renderRoom() {
        if (graphicsContext == null) return;

        clearCanvas();
        drawFloor();
        drawWalls();
        drawPillar();
        drawChest();
        drawPlayer();
    }

    /**
     * Clears the canvas and fills it with the background color.
     *
     * <p>Uses a dark blue-gray color (RGB: 20, 20, 25) as the background
     * to provide contrast for the game elements.</p>
     */
    private void clearCanvas() {
        graphicsContext.setFill(Color.rgb(20, 20, 25));
        graphicsContext.fillRect(0, 0, canvasDimensions.getSize(), canvasDimensions.getSize());
    }

    /**
     * Draws a checkerboard pattern floor across the entire canvas.
     *
     * <p>The floor consists of alternating dark and light stone-colored tiles
     * arranged in a checkerboard pattern. The tile size is determined by
     * the current canvas dimensions to maintain consistent proportions.</p>
     *
     * <p>Colors used:</p>
     * <ul>
     *   <li>Dark stone: RGB(40, 40, 45)</li>
     *   <li>Light stone: RGB(50, 50, 55)</li>
     * </ul>
     */
    private void drawFloor() {
        Color darkStone = Color.rgb(40, 40, 45);
        Color lightStone = Color.rgb(50, 50, 55);
        int tilesPerSide = (int) (canvasDimensions.getSize() / canvasDimensions.getTileSize());

        for (int y = 0; y < tilesPerSide; y++) {
            for (int x = 0; x < tilesPerSide; x++) {
                graphicsContext.setFill(((x + y) % 2 == 0) ? darkStone : lightStone);
                graphicsContext.fillRect(x * canvasDimensions.getTileSize(), y * canvasDimensions.getTileSize(),
                        canvasDimensions.getTileSize(), canvasDimensions.getTileSize());
            }
        }
    }

    /**
     * Draws the room's walls and doors based on the current room's connectivity.
     *
     * <p>This method draws walls around the perimeter of the room and creates
     * door openings where the current room connects to adjacent rooms. Each
     * wall is checked for door connectivity and rendered accordingly.</p>
     *
     * <p>If the current room cannot be determined, no walls are drawn.</p>
     *
     * <p>Colors used:</p>
     * <ul>
     *   <li>Wall color: RGB(80, 85, 100)</li>
     *   <li>Door color: RGB(25, 25, 30)</li>
     * </ul>
     */
    private void drawWalls() {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null) return;

        Color wallColor = Color.rgb(80, 85, 100);
        Color doorColor = Color.rgb(25, 25, 30);

        double size = canvasDimensions.getSize();
        double thickness = canvasDimensions.getWallThickness();
        double doorWidth = canvasDimensions.getDoorWidth();

        // Draw walls
        drawWall(0, 0, size, thickness, currentRoom.hasNorthDoor(), true, doorWidth, wallColor, doorColor);
        drawWall(0, size - thickness, size, thickness, currentRoom.hasSouthDoor(), true, doorWidth, wallColor, doorColor);
        drawWall(0, 0, thickness, size, currentRoom.hasWestDoor(), false, doorWidth, wallColor, doorColor);
        drawWall(size - thickness, 0, thickness, size, currentRoom.hasEastDoor(), false, doorWidth, wallColor, doorColor);
    }

    /**
     * Draws a single wall with an optional door opening.
     *
     * @param x            The x-coordinate of the wall's top-left corner
     * @param y            The y-coordinate of the wall's top-left corner
     * @param width        The width of the wall
     * @param height       The height of the wall
     * @param hasDoor      Whether this wall should have a door opening
     * @param isHorizontal Whether this is a horizontal wall (affects door placement)
     * @param doorSize     The size of the door opening
     * @param wallColor    The color to fill the wall with
     * @param doorColor    The color to fill the door opening with
     */
    private void drawWall(double x, double y, double width, double height, boolean hasDoor,
                          boolean isHorizontal, double doorSize, Color wallColor, Color doorColor) {
        graphicsContext.setFill(wallColor);
        graphicsContext.fillRect(x, y, width, height);

        if (hasDoor) {
            graphicsContext.setFill(doorColor);
            if (isHorizontal) {
                double doorStart = x + (width - doorSize) / 2;
                graphicsContext.fillRect(doorStart, y, doorSize, height);
            } else {
                double doorStart = y + (height - doorSize) / 2;
                graphicsContext.fillRect(x, doorStart, width, doorSize);
            }
        }
    }

    /**
     * Draws a pillar in the center of the room if one exists and is not activated.
     *
     * <p>The pillar is rendered with an animated glow effect using a sine wave
     * to vary opacity and intensity. If a pillar sprite is available, it will
     * be used; otherwise, a fallback rectangular pillar will be drawn.</p>
     *
     * <p>The pillar is only drawn if:</p>
     * <ul>
     *   <li>The current room contains a pillar</li>
     *   <li>The pillar has not been activated yet</li>
     * </ul>
     */
    private void drawPillar() {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null || !currentRoom.hasPillar() || currentRoom.getPillar().isActivated()) {
            return;
        }

        double pillarSize = canvasDimensions.getTileSize() * 4;
        double pillarX = (canvasDimensions.getSize() - pillarSize) / 2;
        double pillarY = (canvasDimensions.getSize() - pillarSize) / 2;

        if (pillarSprite != null) {
            double glowIntensity = (Math.sin(pillarGlowAnimation) + 1.0) / 2.0;
            double alpha = 0.7 + (glowIntensity * 0.3);
            double originalAlpha = graphicsContext.getGlobalAlpha();

            graphicsContext.setGlobalAlpha(alpha);
            graphicsContext.drawImage(pillarSprite, pillarX, pillarY, pillarSize, pillarSize);

            graphicsContext.setGlobalAlpha(glowIntensity * 0.3);
            graphicsContext.setFill(Color.YELLOW);
            graphicsContext.fillOval(pillarX - 5, pillarY - 5, pillarSize + 10, pillarSize + 10);

            graphicsContext.setGlobalAlpha(originalAlpha);
        } else {
            drawFallbackPillar(pillarX, pillarY, pillarSize);
        }
    }

    /**
     * Draws a fallback pillar when no sprite is available.
     *
     * <p>Creates a stone-colored rectangular pillar with an animated
     * yellow glow effect around it.</p>
     *
     * @param pillarX    The x-coordinate of the pillar's top-left corner
     * @param pillarY    The y-coordinate of the pillar's top-left corner
     * @param pillarSize The width and height of the pillar
     */
    private void drawFallbackPillar(double pillarX, double pillarY, double pillarSize) {
        double glowIntensity = (Math.sin(pillarGlowAnimation) + 1.0) / 2.0;

        graphicsContext.setFill(Color.rgb(255, 255, 0, glowIntensity * 0.3));
        graphicsContext.fillOval(pillarX - 10, pillarY - 10, pillarSize + 20, pillarSize + 20);

        graphicsContext.setFill(Color.rgb(200, 200, 200));
        graphicsContext.fillRect(pillarX, pillarY, pillarSize, pillarSize);

        Color pillarColor = Color.rgb(
                (int) (255 * (0.7 + glowIntensity * 0.3)),
                (int) (255 * (0.7 + glowIntensity * 0.3)),
                0
        );
        graphicsContext.setFill(pillarColor);
        graphicsContext.fillRect(pillarX + 5, pillarY + 5, pillarSize - 10, pillarSize - 10);
    }

    /**
     * Draws a treasure chest if one exists in the room and hasn't been opened.
     *
     * <p>The chest is rendered with an animated golden glow effect. Its position
     * is calculated to avoid overlapping with pillars when both are present.
     * If a chest sprite is available, it will be used; otherwise, a fallback
     * chest will be drawn.</p>
     *
     * <p>The chest is only drawn if:</p>
     * <ul>
     *   <li>The current room contains a chest</li>
     *   <li>The chest has not been opened yet</li>
     * </ul>
     */
    private void drawChest() {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null || !currentRoom.hasChest() || currentRoom.isChestOpened()) {
            return;
        }

        double chestSize = canvasDimensions.getTileSize() * 3;
        double[] chestPosition = calculateChestPosition(currentRoom, chestSize);
        double chestX = chestPosition[0];
        double chestY = chestPosition[1];

        if (chestSprite != null) {
            drawChestSprite(chestX, chestY, chestSize);
        } else {
            drawFallbackChest(chestX, chestY, chestSize);
        }
    }

    /**
     * Calculates the optimal position for a chest based on room contents.
     *
     * <p>If the room contains an unactivated pillar, the chest is positioned
     * to the right and above the pillar to avoid overlap. Otherwise, the
     * chest is centered in the room.</p>
     *
     * @param currentRoom The room containing the chest
     * @param chestSize   The size of the chest to be positioned
     * @return An array containing [x, y] coordinates for the chest position
     */
    private double[] calculateChestPosition(Room currentRoom, double chestSize) {
        double chestX, chestY;
        if (currentRoom.hasPillar() && !currentRoom.getPillar().isActivated()) {
            chestX = (canvasDimensions.getSize() / 2 + canvasDimensions.getTileSize() * 3) - (chestSize / 2);
            chestY = (canvasDimensions.getSize() / 2 - canvasDimensions.getTileSize() * 2) - (chestSize / 2);
        } else {
            chestX = (canvasDimensions.getSize() - chestSize) / 2;
            chestY = (canvasDimensions.getSize() - chestSize) / 2;
        }
        return new double[]{chestX, chestY};
    }

    /**
     * Draws a chest using its sprite with animated glow effects.
     *
     * @param chestX    The x-coordinate of the chest
     * @param chestY    The y-coordinate of the chest
     * @param chestSize The size of the chest
     */
    private void drawChestSprite(double chestX, double chestY, double chestSize) {
        double glowIntensity = (Math.sin(chestGlowAnimation) + 1.0) / 2.0;
        double alpha = 0.7 + (glowIntensity * 0.3);
        double originalAlpha = graphicsContext.getGlobalAlpha();

        graphicsContext.setGlobalAlpha(alpha);
        graphicsContext.drawImage(chestSprite, chestX, chestY, chestSize, chestSize);

        graphicsContext.setGlobalAlpha(glowIntensity * 0.3);
        graphicsContext.setFill(Color.GOLD);
        graphicsContext.fillOval(chestX - 5, chestY - 5, chestSize + 10, chestSize + 10);

        graphicsContext.setGlobalAlpha(originalAlpha);
    }

    /**
     * Draws a fallback chest when no sprite is available.
     *
     * <p>Creates a brown wooden chest with golden highlights and a lock,
     * complete with an animated golden glow effect.</p>
     *
     * @param chestX    The x-coordinate of the chest
     * @param chestY    The y-coordinate of the chest
     * @param chestSize The size of the chest
     */
    private void drawFallbackChest(double chestX, double chestY, double chestSize) {
        double glowIntensity = (Math.sin(chestGlowAnimation) + 1.0) / 2.0;

        graphicsContext.setFill(Color.rgb(255, 215, 0, glowIntensity * 0.3));
        graphicsContext.fillOval(chestX - 10, chestY - 10, chestSize + 20, chestSize + 20);

        graphicsContext.setFill(Color.rgb(139, 69, 19));
        graphicsContext.fillRect(chestX, chestY, chestSize, chestSize);

        Color chestColor = Color.rgb(
                (int) (255 * (0.7 + glowIntensity * 0.3)),
                (int) (215 * (0.7 + glowIntensity * 0.3)),
                0
        );
        graphicsContext.setFill(chestColor);
        graphicsContext.fillRect(chestX + 5, chestY + 5, chestSize - 10, chestSize - 10);

        graphicsContext.setFill(Color.DARKGRAY);
        graphicsContext.fillOval(chestX + chestSize / 2 - 5, chestY + chestSize / 2 - 5, 10, 10);
    }

    /**
     * Draws the player character at their current position.
     *
     * <p>If the hero has a sprite sheet available, the current animation frame
     * is drawn. Otherwise, a simple blue rectangle is drawn as a fallback.
     * The size of the hero is determined by the current canvas dimensions.</p>
     */
    private void drawPlayer() {
        Hero player = controller.getPlayer();
        if (player == null) return;

        double heroSize = canvasDimensions.getHeroSize();
        Image spriteSheet = player.getSpriteSheet();

        if (spriteSheet != null) {
            graphicsContext.drawImage(spriteSheet,
                    player.getCurrentFrameX() * Hero.SPRITE_FRAME_WIDTH,
                    player.getAnimationRow() * Hero.SPRITE_FRAME_HEIGHT,
                    Hero.SPRITE_FRAME_WIDTH, Hero.SPRITE_FRAME_HEIGHT,
                    player.getPixelX(), player.getPixelY(), heroSize, heroSize);
        } else {
            graphicsContext.setFill(Color.BLUE);
            graphicsContext.fillRect(player.getPixelX(), player.getPixelY(), heroSize, heroSize);
        }
    }

    /**
     * Updates animation states for all animated objects.
     *
     * <p>This method advances the animation phases for pillar and chest glow
     * effects. It should be called once per frame to maintain smooth animations.
     * Animation phases wrap around at 2π to prevent floating-point overflow.</p>
     */
    public void updateAnimations() {
        pillarGlowAnimation += PILLAR_GLOW_SPEED;
        if (pillarGlowAnimation > Math.PI * 2) {
            pillarGlowAnimation = 0.0;
        }

        chestGlowAnimation += CHEST_GLOW_SPEED;
        if (chestGlowAnimation > Math.PI * 2) {
            chestGlowAnimation = 0.0;
        }
    }

    /**
     * Checks for and handles collection of interactive objects (pillars and chests).
     *
     * <p>This method should be called each frame to detect when the player
     * is close enough to collect pillars or open chests. It handles the
     * game logic for these interactions automatically.</p>
     */
    public void checkCollectibles() {
        checkPillarCollection();
        checkChestCollection();
    }

    /**
     * Checks if the player is close enough to collect a pillar and handles collection.
     *
     * <p>Only processes pillar collection if:</p>
     * <ul>
     *   <li>A valid room and player exist</li>
     *   <li>The room contains a pillar</li>
     *   <li>The pillar has not been activated</li>
     *   <li>The player is within collection distance</li>
     * </ul>
     */
    private void checkPillarCollection() {
        Room currentRoom = getCurrentRoom();
        Hero player = controller.getPlayer();

        if (currentRoom == null || player == null || !currentRoom.hasPillar()
                || currentRoom.getPillar().isActivated()) {
            return;
        }

        if (isPlayerNearPillar(player)) {
            collectPillar(currentRoom);
        }
    }

    /**
     * Determines if the player is within collection distance of the pillar.
     *
     * @param player The player character to check
     * @return true if the player is close enough to collect the pillar
     */
    private boolean isPlayerNearPillar(Hero player) {
        double pillarCenterX = canvasDimensions.getSize() / 2;
        double pillarCenterY = canvasDimensions.getSize() / 2;
        double playerCenterX = player.getPixelX() + canvasDimensions.getHeroSize() / 2;
        double playerCenterY = player.getPixelY() + canvasDimensions.getHeroSize() / 2;

        double distance = Math.sqrt(
                Math.pow(playerCenterX - pillarCenterX, 2) +
                        Math.pow(playerCenterY - pillarCenterY, 2)
        );

        return distance <= PILLAR_COLLECTION_DISTANCE;
    }

    /**
     * Handles the collection of a pillar by the player.
     *
     * <p>Activates the pillar and increments the player's pillar count.
     * Any exceptions during collection are caught and logged.</p>
     *
     * @param room The room containing the pillar to collect
     */
    private void collectPillar(Room room) {
        try {
            Hero player = controller.getPlayer();
            room.getPillar().activate(player);
            player.setPillarsActivated(player.getPillarsActivated() + 1);
        } catch (Exception e) {
            System.err.println("Error collecting pillar: " + e.getMessage());
        }
    }

    /**
     * Checks if the player is close enough to open a chest and handles collection.
     *
     * <p>Only processes chest collection if:</p>
     * <ul>
     *   <li>A valid room and player exist</li>
     *   <li>The room contains a chest</li>
     *   <li>The chest has not been opened</li>
     *   <li>The player hasn't already attempted collection this session</li>
     *   <li>The player is within collection distance</li>
     * </ul>
     *
     * <p>Resets collection state when moving away from chests.</p>
     */
    private void checkChestCollection() {
        Room currentRoom = getCurrentRoom();
        Hero player = controller.getPlayer();

        if (currentRoom == null || player == null || !currentRoom.hasChest()) {
            hasTriedChestCollection = false;
            return;
        }

        if (currentRoom.isChestOpened() || hasTriedChestCollection) {
            return;
        }

        if (isPlayerNearChest(currentRoom, player)) {
            collectChest(currentRoom);
        } else {
            hasShownGoldWarning = false;
        }
    }

    /**
     * Determines if the player is within collection distance of the chest.
     *
     * @param currentRoom The room containing the chest
     * @param player      The player character to check
     * @return true if the player is close enough to open the chest
     */
    private boolean isPlayerNearChest(Room currentRoom, Hero player) {
        double chestSize = canvasDimensions.getTileSize() * 3;
        double[] chestPosition = calculateChestPosition(currentRoom, chestSize);
        double chestCenterX = chestPosition[0] + chestSize / 2;
        double chestCenterY = chestPosition[1] + chestSize / 2;

        double playerCenterX = player.getPixelX() + canvasDimensions.getHeroSize() / 2;
        double playerCenterY = player.getPixelY() + canvasDimensions.getHeroSize() / 2;

        double distance = Math.sqrt(
                Math.pow(playerCenterX - chestCenterX, 2) +
                        Math.pow(playerCenterY - chestCenterY, 2)
        );

        return distance <= CHEST_COLLECTION_DISTANCE;
    }

    /**
     * Handles the collection of items from a chest by the player.
     *
     * <p>Chests require the player to have at least 5 gold to open. If the player
     * lacks sufficient gold, a warning is shown once per chest. Upon successful
     * opening, the player's gold is reduced and any items are added to their
     * inventory.</p>
     *
     * <p>Collection attempts are tracked to prevent multiple interactions with
     * the same chest in a single session.</p>
     *
     * @param room The room containing the chest to open
     */
    private void collectChest(Room room) {
        try {
            Hero player = controller.getPlayer();

            if (player.getGold() < 5) {
                if (!hasShownGoldWarning) {
                    hasShownGoldWarning = true;
                }
                return;
            }

            int originalGold = player.getGold();
            int originalInventorySize = player.getInventory().size();

            room.openChest(player);

            if (player.getGold() < originalGold) {
                hasTriedChestCollection = true;

                List<Item> currentInventory = player.getInventory();
                List<Item> newlyCollectedItems = new ArrayList<>();

                for (int i = originalInventorySize; i < currentInventory.size(); i++) {
                    newlyCollectedItems.add(currentInventory.get(i));
                }
            }
        } catch (Exception e) {
            System.err.println("Error collecting chest: " + e.getMessage());
        }
    }

    /**
     * Resets collection state when the player moves to a new room.
     *
     * <p>This method should be called whenever the player transitions between
     * rooms to ensure chest collection state is properly reset for the new room.</p>
     */
    public void onRoomChanged() {
        hasTriedChestCollection = false;
    }

    /**
     * Retrieves the current room based on the player's position.
     *
     * @return The current room object, or null if the player or dungeon is not available
     */
    private Room getCurrentRoom() {
        if (controller == null || controller.getPlayer() == null || controller.getDungeon() == null) {
            return null;
        }
        return controller.getDungeon().getRoom(controller.getPlayer().getPosition());
    }

    // ====== GETTER METHODS ======

    /**
     * Gets the main room canvas used for rendering.
     *
     * @return The Canvas object used for room rendering
     */
    public Canvas getRoomCanvas() {
        return roomCanvas;
    }

    /**
     * Checks if the insufficient gold warning has been shown for the current chest.
     *
     * @return true if the warning has already been displayed
     */
    public boolean hasShownGoldWarning() {
        return hasShownGoldWarning;
    }

    /**
     * Checks if the player has already attempted to collect the current chest.
     *
     * @return true if a collection attempt has been made for the current chest
     */
    public boolean hasTriedChestCollection() {
        return hasTriedChestCollection;
    }
}