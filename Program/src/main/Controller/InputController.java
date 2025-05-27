package main.Controller;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import main.Controller.StateController.GameState;
import main.Model.util.Direction;


/**
 * Handles raw keyboard input events and translates them into actions
 * by calling methods on the GameController.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class InputController {
    private final GameController myGameController;
    private final StateController myStateController;

    /**
     * Constructor for InputController
     *
     * @param theGameController game controller will process game actions
     * @param theStateController state controller tracks game state
     */
    public InputController(final GameController theGameController, final StateController theStateController) {
        if (theGameController == null) {
            throw new IllegalArgumentException("GameController cannot be null for InputController.");
        }
        if (theStateController == null) {
            throw new IllegalArgumentException("StateController cannot be null for InputController.");
        }
        this.myGameController = theGameController;
        this.myStateController = theStateController;
        System.out.println("InputController initialized with GameController and StateController.");
    }

    /**
     * Handles key pressed events based on the current game state.
     *
     * @param theEvent The KeyEvent representing the key press.
     */
    public void handleKeyPress(final KeyEvent theEvent) {
        KeyCode code = theEvent.getCode();
        System.out.println("Key Pressed: " + code + " in state: " + myStateController.getCurrentState());

        // Handle input based on current game state
        switch (myStateController.getCurrentState()) {
            case EXPLORING:
                handleExplorationInput(code);
                break;
            case COMBAT:
                handleCombatInput(code);
                break;
            case INVENTORY:
                handleInventoryInput(code);
                break;
            case CHEST:
                handleChestInput(code);
                break;
            case PAUSED:
                // Handle pause menu inputs if needed
                break;
            case GAME_OVER:
            case VICTORY:
                // Handle game over/victory screen inputs
                break;
        }

        theEvent.consume();
    }

    /**
     * Handles input during exploration mode.
     *
     * @param code The key code pressed
     */
    private void handleExplorationInput(final KeyCode code) {
        switch (code) {
            case UP:
            case W:
                myGameController.startPlayerMovement(Direction.NORTH);
                break;
            case DOWN:
            case S:
                myGameController.startPlayerMovement(Direction.SOUTH);
                break;
            case LEFT:
            case A:
                myGameController.startPlayerMovement(Direction.WEST);
                break;
            case RIGHT:
            case D:
                myGameController.startPlayerMovement(Direction.EAST);
                break;
            // ... other cases
        }
    }

    /**
     * Handles key released events.
     *
     * @param theEvent The KeyEvent representing the key release.
     */
     public void handleKeyRelease(final KeyEvent theEvent) {
        KeyCode code = theEvent.getCode();

        if (myStateController.getCurrentState() == GameState.EXPLORING) {
            switch (code) {
                case UP:
                case W:
                case DOWN:
                case S:
                case LEFT:
                case A:
                case RIGHT:
                case D:
                    myGameController.stopPlayerMovement();
                    break;
            }
        }
        theEvent.consume();
    }

    /**
     * Handles input during combat mode.
     *
     * @param theCode The key code pressed
     */
    private void handleCombatInput(final KeyCode theCode) {
        switch (theCode) {
            case A:
                // Regular attack
                myGameController.playerAttack();
                break;
            case S:
                // Special attack
                myGameController.playerSpecialAttack();
                break;
            case R:
                // Run from combat
                myGameController.playerRun();
                break;
            case I:
                // Open inventory during combat
                myGameController.openInventory();
                break;
            case ESCAPE:
                // Pause game
                myGameController.pauseGame();
                break;
            default:
                // Ignore other keys
                break;
        }
    }

    /**
     * Handles input during inventory mode.
     *
     * @param theCode The key code pressed
     */
    private void handleInventoryInput(final KeyCode theCode) {
        switch (theCode) {
            case UP:
            case W:
                // Scroll inventory up
                myGameController.scrollInventoryUp();
                break;
            case DOWN:
            case S:
                // Scroll inventory down
                myGameController.scrollInventoryDown();
                break;
            case E:
            case ENTER:
                // Use selected item
                myGameController.useSelectedItem();
                break;
            case I:
            case ESCAPE:
                // Close inventory
                myGameController.closeInventory();
                break;
            default:
                // Ignore other keys
                break;
        }
    }

    /**
     * Handles input when interacting with a chest.
     *
     * @param theCode The key code pressed
     */
    private void handleChestInput(final KeyCode theCode) {
        switch (theCode) {
            case E:
            case ENTER:
                // Open chest and collect items
                myGameController.openChest();
                break;
            case ESCAPE:
                // Cancel chest interaction
                myGameController.cancelChestInteraction();
                break;
            default:
                // Ignore other keys
                break;
        }
    }
}
