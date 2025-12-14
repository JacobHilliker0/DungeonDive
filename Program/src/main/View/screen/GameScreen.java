package main.View.screen;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.View.GameUI;
import main.View.screen.gamescreen.*;

/**
 * Main game screen implementation for the active gameplay phase.
 * This class coordinates all game screen components including rendering,
 * UI management, game loop control, and input handling to provide
 * the primary gameplay experience.
 *
 * The GameScreen integrates multiple specialized components:
 * - GameScreenRenderer: Handles all visual rendering
 * - GameScreenUIManager: Manages user interface elements
 * - GameScreenGameLoop: Controls the main game update cycle
 * - GameScreenInputHandler: Processes user input
 * - GameScreenCanvasDimensions: Manages responsive sizing
 *
 * Key features:
 * - Responsive canvas-based rendering
 * - Real-time player statistics display
 * - Interactive minimap
 * - Message system for game events
 * - Damage flash effects
 * - Room transition handling
 * - Pause/inventory/help integration
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
public class GameScreen extends Screen {

    /** Renderer for all game visuals */
    private final GameScreenRenderer renderer;

    /** UI manager for interface elements */
    private final GameScreenUIManager uiManager;

    /** Main game loop controller */
    private final GameScreenGameLoop gameLoop;

    /** Input event handler */
    private final GameScreenInputHandler inputHandler;

    /** Canvas dimensions manager */
    private final GameScreenCanvasDimensions canvasDimensions;

    /** Main scene for this screen */
    private Scene scene;

    /** Root container for scalable content */
    private StackPane scalableRoot;

    /**
     * Constructs a new GameScreen with all required subsystems.
     * Initializes all component managers and establishes their relationships.
     *
     * @param primaryStage The primary application stage
     * @param controller The main application controller
     */
    public GameScreen(final Stage primaryStage, final Controller controller) {
        super(primaryStage, controller);
        this.canvasDimensions = new GameScreenCanvasDimensions();
        this.renderer = new GameScreenRenderer(controller, canvasDimensions);
        this.uiManager = new GameScreenUIManager(controller, renderer);
        this.gameLoop = new GameScreenGameLoop(controller, renderer, uiManager, canvasDimensions);
        this.inputHandler = new GameScreenInputHandler(controller, gameLoop);
    }

    /**
     * Displays the game screen with full initialization.
     * This method performs complete setup of all game screen components
     * and starts the game session if the model is properly initialized.
     *
     * @param gameUI The GameUI instance for screen coordination
     */
    @Override
    public void showScreen(final GameUI gameUI) {
        initializeScreen(gameUI);
        setupScene(gameUI);
        setupEventHandlers(gameUI);
        finalizeScreen();

        Platform.runLater(() -> {
            if (isModelInitialized()) {
                startGameSession();
            } else {
                retryInitialization();
            }
        });
    }

    /**
     * Initializes the screen components and scene structure.
     * Sets up the scalable root container and creates the main scene.
     *
     * @param gameUI The GameUI instance for initialization
     */
    private void initializeScreen(GameUI gameUI) {
        scalableRoot = new StackPane();

        double currentWidth = getStage().getScene() != null ?
                getStage().getScene().getWidth() : BASE_WIDTH;
        double currentHeight = getStage().getScene() != null ?
                getStage().getScene().getHeight() : BASE_HEIGHT;

        scene = new Scene(scalableRoot, currentWidth, currentHeight);

        renderer.initialize(scene);
        uiManager.initialize(scene);
    }

    /**
     * Sets up the scene content and layout structure.
     * Creates the main content layout and establishes responsive bindings.
     *
     * @param gameUI The GameUI instance for scene setup
     */
    private void setupScene(GameUI gameUI) {
        BorderPane mainContent = createMainContent(gameUI);
        scalableRoot.getChildren().addAll(mainContent, uiManager.getDamageFlashOverlay());

        uiManager.setupResponsiveBindings();
        renderer.setupCanvas();
    }

    /**
     * Creates the main content layout for the game screen.
     * Establishes the primary layout structure with themed background.
     *
     * @param gameUI The GameUI instance for content creation
     * @return BorderPane containing the complete main content
     */
    private BorderPane createMainContent(GameUI gameUI) {
        BorderPane root = new BorderPane();
        uiManager.setupThemedBackground(root);

        HBox mainGameArea = uiManager.createMainGameArea(gameUI);
        uiManager.createMessageArea();

        root.setCenter(mainGameArea);
        root.setBottom(uiManager.getMessageContainer());

        return root;
    }

    /**
     * Sets up event handlers for user input.
     * Configures keyboard input handling for game controls.
     *
     * @param gameUI The GameUI instance for event handling
     */
    private void setupEventHandlers(GameUI gameUI) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                getController().pauseGame(null, gameUI);
            }
        });
    }

    /**
     * Finalizes the screen setup and displays it.
     * Sets the scene on the stage and handles fullscreen state preservation.
     */
    private void finalizeScreen() {
        boolean wasFullScreen = getStage().isFullScreen();

        getStage().setScene(scene);
        getStage().setTitle("Dungeon Dive - Pixel Adventure");

        if (wasFullScreen) {
            getStage().setFullScreen(true);
        }

        if (!getStage().isShowing()) {
            getStage().show();
        }
    }

    /**
     * Checks if the game model is properly initialized.
     * Verifies that all required game components are available.
     *
     * @return true if the model is fully initialized, false otherwise
     */
    private boolean isModelInitialized() {
        return getController() != null &&
                getController().getPlayer() != null &&
                getController().getDungeon() != null;
    }

    /**
     * Starts a complete game session with all subsystems.
     * Loads resources, initializes positions, and begins the game loop.
     */
    private void startGameSession() {
        renderer.loadSprites();
        renderer.initializeHeroPosition();
        renderer.scaleCanvas();
        renderer.renderRoom();

        uiManager.updatePlayerStats();
        uiManager.updateMinimap();
        gameLoop.start();

        uiManager.addGameMessage("Welcome to the dungeon!");
        if (getController().getGameController() != null) {
            uiManager.addGameMessage(getController().getGameController().getCurrentRoomDescription());
        }
    }

    /**
     * Attempts to retry initialization if the model isn't ready.
     * Provides fallback behavior for delayed model initialization.
     */
    private void retryInitialization() {
        System.err.println("GameScreen: Model not fully initialized yet, retrying...");
        Platform.runLater(() -> {
            if (isModelInitialized()) {
                startGameSession();
            } else {
                System.err.println("GameScreen: Model still not initialized, starting partial session");
                gameLoop.start();
                uiManager.updatePlayerStats();
            }
        });
    }

    // Public interface methods for external interaction

    /**
     * Adds a message to the game message display.
     *
     * @param message The message text to display to the player
     */
    public void addGameMessage(String message) {
        uiManager.addGameMessage(message);
    }

    /**
     * Updates the player statistics display.
     * Refreshes all stat values shown in the UI.
     */
    public void updatePlayerStats() {
        uiManager.updatePlayerStats();
    }

    /**
     * Updates the minimap display.
     * Refreshes the minimap to show current room state.
     */
    public void updateMinimap() {
        uiManager.updateMinimap();
    }

    /**
     * Triggers a damage flash effect on the screen.
     * Provides visual feedback when the player takes damage.
     */
    public void flashDamageEffect() {
        uiManager.flashDamageEffect();
    }

    /**
     * Handles room change events.
     * Updates all relevant components when the player moves to a new room.
     */
    public void onRoomChanged() {
        renderer.onRoomChanged();
        uiManager.onRoomChanged();
    }

    /**
     * Stops the game loop.
     * Used when pausing or transitioning away from the game screen.
     */
    public void stopGameLoop() {
        gameLoop.stop();
    }

    /**
     * Gets the canvas dimensions manager.
     * Provides access to canvas sizing information for external components.
     *
     * @return The canvas dimensions manager instance
     */
    public GameScreenCanvasDimensions getCanvasDimensions() {
        return canvasDimensions;
    }
}