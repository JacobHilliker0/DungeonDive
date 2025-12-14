package main.View.screen.gamescreen;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.Controller.Controller;
import main.Model.character.Hero;
import main.Model.dungeon.Dungeon;
import main.Model.dungeon.Room;
import main.Model.util.Point;

/**
 * Renders the minimap display for the game screen.
 * This class handles drawing a small overview map showing visited rooms,
 * the player's current position, and different room types with color coding.
 *
 * The minimap displays:
 * - Visited rooms in a grid layout
 * - Player's current position highlighted
 * - Different room types with distinct colors
 * - Room states (cleared, unopened chests, etc.)
 * - Bordered frame with responsive sizing
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
class GameScreenMinimapRenderer {

    /** The main controller for accessing game state */
    private final Controller controller;

    /** Canvas for rendering the minimap */
    private Canvas minimapCanvas;

    /** Graphics context for drawing operations */
    private GraphicsContext minimapGraphics;

    /** Size of the minimap grid (number of rooms to display) */
    private static final double MINIMAP_GRID_SIZE = 7.0;

    /**
     * Constructs a new minimap renderer.
     *
     * @param controller The main controller for accessing game state
     */
    public GameScreenMinimapRenderer(Controller controller) {
        this.controller = controller;
    }

    /**
     * Initializes the minimap renderer with the provided canvas.
     * Sets up the canvas and graphics context, and adds listeners for size changes.
     *
     * @param minimapCanvas The canvas to render the minimap on
     */
    public void initialize(Canvas minimapCanvas) {
        this.minimapCanvas = minimapCanvas;
        this.minimapGraphics = minimapCanvas.getGraphicsContext2D();

        // Add listeners to update minimap when canvas size changes
        minimapCanvas.widthProperty().addListener((obs, oldVal, newVal) -> updateMinimap());
        minimapCanvas.heightProperty().addListener((obs, oldVal, newVal) -> updateMinimap());
    }

    /**
     * Updates the entire minimap display.
     * This method should be called whenever the game state changes or the canvas is resized.
     * Performs a complete redraw of the minimap including background, border, and rooms.
     */
    public void updateMinimap() {
        if (controller == null || controller.getDungeon() == null || minimapGraphics == null) {
            return;
        }
        clearMinimap();
        drawMinimapBorder();
        drawMinimapRooms();
    }

    /**
     * Clears the minimap canvas and fills it with a black background.
     */
    private void clearMinimap() {
        double size = minimapCanvas.getWidth();
        minimapGraphics.clearRect(0, 0, size, size);
        minimapGraphics.setFill(Color.BLACK);
        minimapGraphics.fillRect(0, 0, size, size);
    }

    /**
     * Draws the decorative border around the minimap.
     * Uses a brown color scheme to match the game's medieval theme.
     */
    private void drawMinimapBorder() {
        double size = minimapCanvas.getWidth();
        minimapGraphics.setStroke(Color.rgb(139, 69, 19));
        minimapGraphics.setLineWidth(2);
        minimapGraphics.strokeRect(1, 1, size - 2, size - 2);
    }

    /**
     * Draws all visited rooms on the minimap.
     * Centers the display around the player's current position and shows
     * a grid of rooms with different colors based on their type and state.
     */
    private void drawMinimapRooms() {
        Dungeon dungeon = controller.getDungeon();
        Hero player = controller.getPlayer();
        if (player == null) return;

        Point playerPos = player.getPosition();
        int gridSize = (int) MINIMAP_GRID_SIZE;

        // Calculate display dimensions
        double canvasSize = minimapCanvas.getWidth();
        double roomDisplaySize = canvasSize / (gridSize + 2);
        double totalGridSize = roomDisplaySize * gridSize;
        double startDrawX = (canvasSize - totalGridSize) / 2;
        double startDrawY = (canvasSize - totalGridSize) / 2;

        // Calculate room coordinates to display (centered on player)
        int startX = playerPos.getX() - gridSize / 2;
        int startY = playerPos.getY() - gridSize / 2;

        // Draw each room in the grid
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                int mapX = startX + x;
                int mapY = startY + y;
                Room room = dungeon.getRoom(mapX, mapY);

                if (room != null && room.isVisited()) {
                    boolean isPlayerHere = (mapX == playerPos.getX() && mapY == playerPos.getY());
                    double roomDrawX = startDrawX + x * roomDisplaySize;
                    double roomDrawY = startDrawY + y * roomDisplaySize;
                    drawMinimapRoom(room, roomDrawX, roomDrawY, roomDisplaySize, isPlayerHere);
                }
            }
        }
    }

    /**
     * Draws a single room on the minimap.
     *
     * @param room The room to draw
     * @param x The X coordinate to draw at
     * @param y The Y coordinate to draw at
     * @param size The size of the room square
     * @param isPlayerPosition Whether this is the player's current room
     */
    private void drawMinimapRoom(Room room, double x, double y, double size, boolean isPlayerPosition) {
        Color roomColor = getMinimapRoomColor(room, isPlayerPosition);
        minimapGraphics.setFill(roomColor);
        minimapGraphics.fillRect(x, y, size, size);

        // Highlight player's current position with a white border
        if (isPlayerPosition) {
            minimapGraphics.setStroke(Color.WHITE);
            minimapGraphics.setLineWidth(1.5);
            minimapGraphics.strokeRect(x, y, size, size);
        }
    }

    /**
     * Determines the color to use for a room on the minimap.
     * Colors are based on room type and current state (e.g., cleared, activated).
     *
     * @param room The room to get the color for
     * @param isPlayerPosition Whether this is the player's current position
     * @return The color to use for this room
     */
    private Color getMinimapRoomColor(Room room, boolean isPlayerPosition) {
        // Player position always shows as lime green
        if (isPlayerPosition) return Color.LIME;

        // Unvisited rooms are transparent
        if (!room.isVisited()) return Color.TRANSPARENT;

        // Color coding based on room type and state
        switch (room.getRoomType()) {
            case ENTRANCE:
                return Color.LIGHTGREEN;
            case EXIT:
                return Color.ORANGE;
            case BOSS:
                return Color.DARKRED;
            case PILLAR:
                // Purple if activated, medium purple if not
                return room.hasPillar() && room.getPillar().isActivated() ?
                        Color.PURPLE : Color.MEDIUMPURPLE;
            case MONSTER:
                // Light gray if cleared, red if monsters remain
                return room.getMonsters().isEmpty() ? Color.LIGHTGRAY : Color.RED;
            case TREASURE:
                // Light gray if opened, gold if unopened
                return room.isChestOpened() ? Color.LIGHTGRAY : Color.GOLD;
            case TRAP:
                // Light gray if sprung, orange-red if active
                return room.hasTrap() && room.getTrap().isSprung() ?
                        Color.LIGHTGRAY : Color.ORANGERED;
            case EMPTY:
            default:
                return Color.LIGHTGRAY;
        }
    }
}