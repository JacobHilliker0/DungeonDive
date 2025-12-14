package main.View.screen.gamescreen;

import javafx.animation.AnimationTimer;
import main.Controller.Controller;
import main.Controller.StateController;
import main.Model.character.Hero;

/**
 * Manages the main game loop for the game screen.
 * This class handles the continuous updating and rendering of the game state
 * using JavaFX's AnimationTimer for smooth gameplay.
 *
 * The game loop is responsible for:
 * - Updating player position and animations
 * - Checking for collectible interactions
 * - Handling room transitions
 * - Updating UI elements
 * - Managing game state transitions
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
public class GameScreenGameLoop {

    /** The main controller for accessing game state */
    private final Controller controller;

    /** Renderer for drawing the game scene */
    private final GameScreenRenderer renderer;

    /** UI manager for updating interface elements */
    private final GameScreenUIManager uiManager;

    /** Handler for player movement and room transitions */
    private final GameScreenMovementHandler movementHandler;

    /** JavaFX animation timer that drives the game loop */
    private AnimationTimer gameLoop;

    /** Flag to prevent double-processing during room transitions */
    private boolean justTransitioned = false;

    /**
     * Constructs a new game loop with the required dependencies.
     *
     * @param controller The main controller for accessing game state
     * @param renderer The renderer for drawing the game scene
     * @param uiManager The UI manager for updating interface elements
     * @param canvasDimensions The canvas dimensions for movement calculations
     */
    public GameScreenGameLoop(Controller controller, GameScreenRenderer renderer,
                              GameScreenUIManager uiManager, GameScreenCanvasDimensions canvasDimensions) {
        this.controller = controller;
        this.renderer = renderer;
        this.uiManager = uiManager;
        this.movementHandler = new GameScreenMovementHandler(controller, renderer, canvasDimensions);
    }

    /**
     * Starts the game loop.
     * Creates and starts the AnimationTimer if it doesn't already exist.
     * The loop will continue running until explicitly stopped.
     */
    public void start() {
        if (gameLoop == null) {
            gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (shouldUpdateGame()) {
                        updateGame();
                    }
                    renderer.renderRoom();
                }
            };
        }
        gameLoop.start();
    }

    /**
     * Stops the game loop.
     * This will pause all game updates and rendering.
     */
    public void stop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    /**
     * Determines whether the game should be updated this frame.
     * Checks that all required components are available and the game is in the exploring state.
     *
     * @return true if the game should be updated, false otherwise
     */
    private boolean shouldUpdateGame() {
        return controller != null &&
                controller.getGameController() != null &&
                controller.getPlayer() != null &&
                controller.getGameController().getStateController().isInState(StateController.GameState.EXPLORING);
    }

    /**
     * Updates the game state for the current frame.
     * This method handles all game logic updates including:
     * - Player position and animation updates
     * - UI updates
     * - Collectible checking
     * - Animation updates
     * - Room transition checks
     */
    private void updateGame() {
        Hero player = controller.getPlayer();
        if (player != null) {
            // Update player state
            player.updatePixelPosition();
            player.updateAnimation(System.nanoTime());

            // Update UI elements
            uiManager.updateCharacterPortrait();

            // Check for collectibles only if we haven't just transitioned
            if (!justTransitioned) {
                renderer.checkCollectibles();
            }

            // Update animations
            renderer.updateAnimations();

            // Check for room transitions
            if (movementHandler.checkRoomTransition()) {
                onRoomTransition();
            }

            // Update player stats display
            uiManager.updatePlayerStats();

            // Reset transition flag
            justTransitioned = false;
        }
    }

    /**
     * Handles room transition events.
     * Sets the transition flag and notifies relevant components about the room change.
     * This prevents double-processing of events during the transition frame.
     */
    private void onRoomTransition() {
        justTransitioned = true;
        renderer.onRoomChanged();
        uiManager.onRoomChanged();
    }
}