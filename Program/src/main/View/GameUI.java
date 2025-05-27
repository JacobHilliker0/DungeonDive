package main.View;

import main.Controller.Controller;
import main.Controller.InputController;
// import main.Controller.GameController;
import main.Model.character.Hero;
import main.Model.character.Monster;
import main.Model.dungeon.Room;
import main.View.screen.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Model.element.Item;
import main.Model.element.Pillar;
import main.Model.element.Trap;
import java.util.List;


/**
 * Manages the overall UI of the game, including screen transitions.
 * It holds references to the primary stage and the main controller.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class GameUI {
    private final Stage myPrimaryStage;
    private final Controller myController;
    private InputController myInputController; // Field to store the InputController
    private GameScreen myGameScreen;
    private CombatScreen myCombatScreen;

    /**
     * This is here because the inventory screen worked differently than others.
     * it is on a separate stage, and therefore needed an outside field to deal
     * with opening and closing inventory.
     */
    private final InventoryScreen myInventoryScreen;

    /**
     * Constructor for GameUI.
     *
     * @param thePrimaryStage The primary stage of the application.
     * @param theController   The main controller instance for the application.
     */
    public GameUI(final Stage thePrimaryStage, final Controller theController) {
        if (thePrimaryStage == null) {
            throw new IllegalArgumentException("Primary stage cannot be null for GameUI.");
        }
        if (theController == null) {
            throw new IllegalArgumentException("Controller cannot be null for GameUI.");
        }
        this.myPrimaryStage = thePrimaryStage;
        this.myController = theController;
        this.myGameScreen = new GameScreen(myPrimaryStage, myController);
        this.myCombatScreen = new CombatScreen(myPrimaryStage, myController);
        this.myInventoryScreen = new InventoryScreen(myPrimaryStage, myController);
    }

    public void showIntroScreen() {
        IntroScreen intro = new IntroScreen(myPrimaryStage, myController);
        intro.showScreen(this);
    }

    public void showHeroSelection() {
        HeroSelectionScreen heroSelection = new HeroSelectionScreen(myPrimaryStage, myController);
        heroSelection.showScreen(this);
    }

    public void showHelpMenu() {
        myGameScreen.stopGameLoop();
        HelpScreen helpScreen = new HelpScreen(myPrimaryStage, myController);
        helpScreen.showScreen(this);
    }

    public void showPauseMenu() {
        myGameScreen.stopGameLoop();
        PauseScreen pauseScreen = new PauseScreen(myPrimaryStage, myController);
        pauseScreen.showScreen(this);
    }

    public void showSavesScreen() {
        SavesScreen savesScreen = new SavesScreen(myPrimaryStage, myController);
        savesScreen.showScreen(this);
    }

    public void showWinScreen() {
        myGameScreen.stopGameLoop();
        WinScreen winScreen = new WinScreen(myPrimaryStage, myController);
        winScreen.showScreen(this);
    }

    public void showLoseScreen() {
        myGameScreen.stopGameLoop();
        LoseScreen loseScreen = new LoseScreen(myPrimaryStage, myController);
        loseScreen.showScreen(this);
    }

    public void showCombatScreen(final List<Monster> monsters) {
        myCombatScreen.showScreen(this, monsters); // <-- Use the instance
        attachKeyListenersToScene();
        System.out.println("Combat screen shown via GameUI.");
    }

    public void showGameScreen() {
        myGameScreen.showScreen(this);
        attachKeyListenersToScene();
        System.out.println("Game screen shown.");
    }

    public Controller getController() {
        return myController;
    }

    public Stage getPrimaryStage() {
        return myPrimaryStage;
    }

    public GameScreen getGameScreen() {
        return myGameScreen;
    }

    public CombatScreen getCombatScreen() { return myCombatScreen; }

    public void setGameScreen(GameScreen gameScreen) {
        this.myGameScreen = gameScreen;
    }

    /**
     * Sets the input controller and attaches its listeners to the current scene.
     * This method should be called after a scene that requires input (like GameScreen) is set on the stage.
     * @param theInputController The input controller for game actions.
     */
    public void setInputController(final InputController theInputController) {
        this.myInputController = theInputController;
        attachKeyListenersToScene(); // Attempt to attach listeners immediately
    }

    /**
     * Updates the combat screen by calling its update method.
     * MODIFIED: Actually calls the screen's update method.
     */
    public void updateCombatScreen(final List<Monster> monsters) {
        if (myCombatScreen != null) {
            // Check if the combat scene is currently active (optional but good)
            Scene currentScene = myPrimaryStage.getScene();
            if (currentScene != null && myPrimaryStage.getTitle().contains("Combat")) {
                myCombatScreen.updateDisplay(monsters); // <-- CALL THE UPDATE METHOD
                System.out.println("GameUI: Combat screen update called.");
            }
        } else {
            System.err.println("GameUI: CombatScreen instance is null, cannot update.");
        }
    }

    /**
     * Attaches key listeners from the InputController to the current scene on the primary stage.
     * This should be called whenever the scene changes to one that needs key input (e.g., GameScreen).
     */
    private void attachKeyListenersToScene() {
        Scene currentScene = myPrimaryStage.getScene();
        if (currentScene != null && this.myInputController != null) {
            currentScene.setOnKeyPressed(this.myInputController::handleKeyPress);
            currentScene.setOnKeyReleased(this.myInputController::handleKeyRelease);
            System.out.println("InputController key listeners attached to scene.");
            currentScene.getRoot().requestFocus(); // Ensure focus for key events
        } else {
            System.err.println("GameUI: Cannot attach key listeners - Scene or InputController is null.");
        }
    }

    /**
     * updates the player's position on game screen
     * called after player movement to refresh the UI.
     */
    public void updatePlayerPosition() {
        //get current scene and update player position visual
        Scene currentScene = myPrimaryStage.getScene();
        if (currentScene != null) {
            //update player's visual position on game map
            //this would involve updating a player indicator
            System.out.println("Player position updated in UI");
        }
    }

    /**
     * Hides the combat screen when combat ends.
     */
    public void hideCombatScreen() {
        System.out.println("Combat screen hidden, returning to game screen.");
        showGameScreen();
    }

    /**
     * Shows a prompt indicating a chest is in the room.
     */
    public void showChestPrompt() {
        // Display a visual indicator or message about the chest
        System.out.println("Chest prompt shown");
    }

    /**
     * Shows the chest interaction interface.
     */
    public void showChestInteraction() {
        // Display chest opening interface
        System.out.println("Chest interaction screen shown");
    }

    /**
     * Hides the chest interaction interface.
     */
    public void hideChestInteraction() {
        // Hide chest interface
        System.out.println("Chest interaction hidden");
    }

    /**
     * Shows the contents of a chest after opening it.
     *
     * @param items The items found in the chest
     */
    public void showChestContents(final List<Item> items) {
        // Display the items found in the chest
        StringBuilder message = new StringBuilder("Found in chest: ");
        for (Item item : items) {
            message.append(item.getName()).append(", ");
        }
        System.out.println(message.toString());
    }

    /**
     * Shows a message when items are collected from the ground.
     *
     * @param items The items that were collected
     */
    public void showItemCollectionMessage(final List<Item> items) {
        // Display a message about collected items
        StringBuilder message = new StringBuilder("Collected items: ");
        for (Item item : items) {
            message.append(item.getName()).append(", ");
        }
        System.out.println(message.toString());
    }

    /**
     * Shows the inventory screen.
     */
    public void showInventoryScreen() {
        // Display inventory interface
        myInventoryScreen.showScreen(this);
        System.out.println("Inventory screen shown");
    }

    /**
     * Hides the inventory screen.
     */
    public void hideInventoryScreen() {
        // Hide inventory interface
        myInventoryScreen.closeScreen();
        System.out.println("Inventory screen hidden");
    }

    /**
     * Updates the inventory display.
     */
    public void updateInventory() {
        // Refresh inventory display with current items
        System.out.println("Inventory display updated");
    }

    /**
     * Updates the selected item in the inventory.
     *
     * @param theSelectedIndex The index of the selected item
     */
    public void updateInventorySelection(final int theSelectedIndex) {
        // Highlight the selected inventory item
        System.out.println("Inventory selection updated to index " + theSelectedIndex);
    }

    /**
     * Shows a message when a pillar is found.
     *
     * @param thePillar The pillar that was found
     */
    public void showPillarFound(final Pillar thePillar) {
        // Display a message about finding a pillar
        System.out.println("Pillar found: " + thePillar.getType().getDisplayName());
    }

    /**
     * Updates the display of collected pillars.
     */
    public void updatePillarCollection() {
        // Update the UI to show which pillars have been collected
        System.out.println("Pillar collection display updated");
    }

    /**
     * Shows the effect of a trap when triggered.
     *
     * @param theTrap The trap that was triggered
     */
    public void showTrapEffect(final Trap theTrap) {
        // Display visual effects for the trap
        System.out.println("Trap effect shown: " + theTrap.getName());
    }

    /**
     * Shows the victory screen.
     */
    public void showVictoryScreen() {
        WinScreen winScreen = new WinScreen(myPrimaryStage, myController);
        winScreen.showScreen(this);
    }

    /**
     * Shows the game over screen.
     */
    public void showGameOverScreen() {
        LoseScreen loseScreen = new LoseScreen(myPrimaryStage, myController);
        loseScreen.showScreen(this);
    }

    /**
     * Shows a message when the player doesn't have enough mana for a special attack.
     */
    public void showNotEnoughManaMessage() {
        // Display a message about insufficient mana
        System.out.println("Not enough mana/energy for special attack!");
    }

    /**
     * Shows a message when the player fails to run from combat.
     */
    public void showRunFailedMessage() {
        // Display a message about failing to run
        System.out.println("Failed to run away from combat!");
    }

    /**
     * Shows a message when the game is saved successfully.
     */
    public void showSaveSuccessMessage() {
        // Display a message about successful save
        System.out.println("Game saved successfully!");
    }

    /**
     * Shows a message when the game fails to save.
     */
    public void showSaveFailureMessage() {
        // Display a message about failed save
        System.out.println("Failed to save game!");
    }

    /**
     * Shows the level up screen.
     */
    public void showLevelUpScreen() {
        // Display level up interface
        System.out.println("Level up screen shown");
    }

    /**
     * Updates the room description display.
     *
     * @param theRoom The room to describe
     */
    public void updateRoomDescription(final Room theRoom) {
        // Update the room description text
        System.out.println("Room description updated for room at " + theRoom.getPosition());
    }

    /**
     * Shows a monster attack effect.
     *
     * @param theMonster The monster that attacked
     * @param theDamage The amount of damage dealt
     */
    public void showMonsterAttackEffect(final Monster theMonster, final int theDamage) {
        // Display visual effects for monster attack
        System.out.println(theMonster.getName() + " attacked for " + theDamage + " damage!");
    }

    /**
     * Hides all overlay screens.
     */
    public void hideAllOverlays() {
        // Hide all overlay screens (inventory, combat, etc.)
        System.out.println("All overlay screens hidden");
    }

    /**
     *shows message when a pillar is activated
     *
     * @param thePillar pillar that was activated
     */
    public void showPillarActivated(final Pillar thePillar) {
        //display message about activating pillar and stat bonus
        System.out.println("Pillar of " + thePillar.getType().getDisplayName() +
                " activated! " + thePillar.getType().getDescription());

        // This would involve showing special effect
    }

    /**
     * updates the player stats display in the UI
     * This should be called whenever the player's stats change (health, attack,etc.)
     */
    public void updatePlayerStats() {
        //get current player from controller
        Hero player = myController.getPlayer();

        if (player != null) {
            //update UI elements that display player stats
            // involves updating labels or text fields

            System.out.println("Player stats updated in UI: " +
                    player.getName() + " - Health: " +
                    player.getHealth() + "/" + player.getMaxHealth());
        } else {
            System.err.println("Cannot update player stats: Player is null");
        }
    }


    public InputController getInputController() {
        return myInputController;
    }
}

