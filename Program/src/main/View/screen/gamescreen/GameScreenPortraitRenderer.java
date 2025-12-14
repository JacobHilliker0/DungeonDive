package main.View.screen.gamescreen;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import main.Controller.Controller;
import main.Model.character.Hero;

/**
 * Renders the character portrait display for the game screen.
 * This class handles drawing the player's current character sprite in a dedicated
 * portrait area, showing the current animation frame and state.
 *
 * Features:
 * - Responsive canvas sizing
 * - Current animation frame display
 * - Proper sprite scaling and centering
 * - Background styling
 * - Automatic updates on canvas resize
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
class GameScreenPortraitRenderer {

    /** The main controller for accessing player data */
    private final Controller controller;

    /** Canvas for rendering the character portrait */
    private Canvas portraitCanvas;

    /** Graphics context for drawing operations */
    private GraphicsContext portraitGraphics;

    /**
     * Constructs a new portrait renderer.
     *
     * @param controller The main controller for accessing player data
     */
    public GameScreenPortraitRenderer(Controller controller) {
        this.controller = controller;
    }

    /**
     * Initializes the portrait renderer with the provided canvas.
     * Sets up the canvas and graphics context, and adds listeners for size changes.
     *
     * @param portraitCanvas The canvas to render the portrait on
     */
    public void initialize(Canvas portraitCanvas) {
        this.portraitCanvas = portraitCanvas;
        this.portraitGraphics = portraitCanvas.getGraphicsContext2D();

        // Add listeners to update background when canvas size changes
        portraitCanvas.widthProperty().addListener((obs, oldVal, newVal) -> updatePortraitBackground());
        portraitCanvas.heightProperty().addListener((obs, oldVal, newVal) -> updatePortraitBackground());
    }

    /**
     * Updates the portrait canvas with the current player sprite.
     * This method should be called whenever the player's animation state changes
     * or when the portrait needs to be refreshed.
     *
     * Renders:
     * - Dark background
     * - Player sprite centered and scaled
     * - Current animation frame
     * - Proper aspect ratio preservation
     */
    public void updatePortraitCanvas() {
        if (portraitGraphics == null) return;

        Hero player = controller.getPlayer();
        if (player == null) return;

        double w = portraitCanvas.getWidth();
        double h = portraitCanvas.getHeight();

        // Fill background with dark color
        portraitGraphics.setFill(Color.rgb(40, 40, 45));
        portraitGraphics.fillRect(0, 0, w, h);

        // Calculate sprite display size (80% of canvas, maintaining aspect ratio)
        double size = Math.min(w, h) * 0.8;
        double x = (w - size) / 2;
        double y = (h - size) / 2;

        // Draw the player sprite if available
        Image spriteSheet = player.getSpriteSheet();
        if (spriteSheet != null) {
            portraitGraphics.drawImage(spriteSheet,
                    // Source rectangle (current animation frame)
                    player.getCurrentFrameX() * Hero.SPRITE_FRAME_WIDTH,
                    player.getAnimationRow() * Hero.SPRITE_FRAME_HEIGHT,
                    Hero.SPRITE_FRAME_WIDTH, Hero.SPRITE_FRAME_HEIGHT,
                    // Destination rectangle (centered and scaled)
                    x, y, size, size);
        }
    }

    /**
     * Updates the portrait background.
     * Called when the canvas is resized to ensure proper background coverage.
     */
    private void updatePortraitBackground() {
        if (portraitGraphics != null) {
            portraitGraphics.setFill(Color.rgb(40, 40, 45));
            portraitGraphics.fillRect(0, 0, portraitCanvas.getWidth(), portraitCanvas.getHeight());
        }
    }
}