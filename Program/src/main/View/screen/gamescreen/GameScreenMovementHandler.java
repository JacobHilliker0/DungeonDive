package main.View.screen.gamescreen;

import main.Controller.Controller;
import main.Model.character.Hero;
import main.Model.dungeon.Room;

/**
 * Handles player movement and room transitions in the game screen.
 * This class manages the logic for detecting when a player moves between rooms
 * and ensures proper positioning when transitioning occurs.
 *
 * Key responsibilities:
 * - Detecting room transition conditions
 * - Validating door accessibility
 * - Positioning player correctly after transitions
 * - Constraining player movement within room boundaries
 * - Managing entrance/exit door alignment
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
class GameScreenMovementHandler {

    /** The main controller for accessing game state and handling room changes */
    private final Controller controller;

    /** Renderer reference for accessing current room state */
    private final GameScreenRenderer renderer;

    /** Canvas dimensions for calculating boundaries and positioning */
    private final GameScreenCanvasDimensions canvasDimensions;

    /**
     * Constructs a new movement handler with the required dependencies.
     *
     * @param controller The main controller for accessing game state
     * @param renderer The renderer for accessing current room state
     * @param canvasDimensions The canvas dimensions for movement calculations
     */
    public GameScreenMovementHandler(Controller controller, GameScreenRenderer renderer,
                                     GameScreenCanvasDimensions canvasDimensions) {
        this.controller = controller;
        this.renderer = renderer;
        this.canvasDimensions = canvasDimensions;
    }

    /**
     * Checks if the player should transition to a different room.
     * This method examines the player's current position relative to room boundaries
     * and available doors to determine if a room transition should occur.
     *
     * @return true if a room transition occurred, false otherwise
     */
    public boolean checkRoomTransition() {
        Hero player = controller.getPlayer();
        Room currentRoom = getCurrentRoom();
        if (player == null || currentRoom == null) return false;

        double px = player.getPixelX();
        double py = player.getPixelY();
        double heroSize = canvasDimensions.getHeroSize();
        boolean transitioned = false;

        // Check each direction for potential room transitions
        if (py <= canvasDimensions.getBoundaryTop() && currentRoom.hasNorthDoor()) {
            if (isPlayerInDoorRange(px, heroSize)) {
                controller.getGameController().movePlayerNorth();
                player.setPixelPosition(px, canvasDimensions.getBoundaryBottom() - 1);
                transitioned = true;
            }
        } else if (py >= canvasDimensions.getBoundaryBottom() && currentRoom.hasSouthDoor()) {
            if (isPlayerInDoorRange(px, heroSize)) {
                controller.getGameController().movePlayerSouth();
                player.setPixelPosition(px, canvasDimensions.getBoundaryTop() + 1);
                transitioned = true;
            }
        } else if (px <= canvasDimensions.getBoundaryLeft() && currentRoom.hasWestDoor()) {
            if (isPlayerInDoorRange(py, heroSize)) {
                controller.getGameController().movePlayerWest();
                player.setPixelPosition(canvasDimensions.getBoundaryRight() - 1, py);
                transitioned = true;
            }
        } else if (px >= canvasDimensions.getBoundaryRight() && currentRoom.hasEastDoor()) {
            if (isPlayerInDoorRange(py, heroSize)) {
                controller.getGameController().movePlayerEast();
                player.setPixelPosition(canvasDimensions.getBoundaryLeft() + 1, py);
                transitioned = true;
            }
        }

        // If no transition occurred, constrain player within current room boundaries
        if (!transitioned) {
            constrainPlayerPosition(player, px, py);
        }

        return transitioned;
    }

    /**
     * Constrains the player's position within the current room boundaries.
     * This method ensures the player cannot move outside the playable area
     * when no room transition is available.
     *
     * @param player The player character to constrain
     * @param px The player's current X position
     * @param py The player's current Y position
     */
    private void constrainPlayerPosition(Hero player, double px, double py) {
        double constrainedX = Math.max(canvasDimensions.getBoundaryLeft(),
                Math.min(canvasDimensions.getBoundaryRight(), px));
        double constrainedY = Math.max(canvasDimensions.getBoundaryTop(),
                Math.min(canvasDimensions.getBoundaryBottom(), py));
        player.setPixelPosition(constrainedX, constrainedY);
    }

    /**
     * Checks if the player is positioned within the door range for transition.
     * This method verifies that the player's center point is aligned with a door
     * opening, allowing them to pass through to the adjacent room.
     *
     * @param playerPos The player's position (X for north/south doors, Y for east/west doors)
     * @param heroSize The size of the hero sprite for center calculation
     * @return true if the player is within the door range, false otherwise
     */
    private boolean isPlayerInDoorRange(double playerPos, double heroSize) {
        double playerCenter = playerPos + heroSize / 2;
        double doorWidth = canvasDimensions.getDoorWidth();
        double canvasSize = canvasDimensions.getSize();
        double doorStart = (canvasSize - doorWidth) / 2;
        double doorEnd = doorStart + doorWidth;

        return playerCenter >= doorStart && playerCenter <= doorEnd;
    }

    /**
     * Gets the current room the player is in.
     *
     * @return The current room, or null if the room cannot be determined
     */
    private Room getCurrentRoom() {
        if (controller == null || controller.getPlayer() == null || controller.getDungeon() == null) {
            return null;
        }
        return controller.getDungeon().getRoom(controller.getPlayer().getPosition());
    }
}