package main.View.screen.gamescreen;

import main.Controller.Controller;

/**
 * Handles input events for the game screen.
 * This class processes keyboard inputs and translates them into appropriate
 * game actions such as pausing, opening inventory, or accessing help.
 *
 * The input handler manages:
 * - Escape key for pausing the game
 * - Inventory key for opening the inventory screen
 * - Help key for accessing the help menu
 * - Game loop coordination during state changes
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
public class GameScreenInputHandler {

    /** The main controller for handling game actions */
    private final Controller controller;

    /** Reference to the game loop for stopping/starting during menu access */
    private final GameScreenGameLoop gameLoop;

    /**
     * Constructs a new input handler with the required dependencies.
     *
     * @param controller The main controller for handling game actions
     * @param gameLoop The game loop to control during menu transitions
     */
    public GameScreenInputHandler(Controller controller, GameScreenGameLoop gameLoop) {
        this.controller = controller;
        this.gameLoop = gameLoop;
    }

    /**
     * Handles the help key press event.
     * Stops the game loop and presumably opens the help menu.
     * This ensures the game is paused while help information is displayed.
     */
    public void handleHelpKey() {
        gameLoop.stop();
    }
}