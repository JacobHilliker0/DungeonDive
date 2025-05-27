package main.View.screen;

import main.Controller.Controller;
import main.View.GameUI;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Text;

/**
 * An abstract class for the various Screens.
 * Each screen is associated with the primary stage and a shared controller.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public abstract class Screen { // Changed to public if accessed from other packages like GameUI
    /**
     * Primary Stage needed to show every scene.
     * Made protected so subclasses can access if absolutely needed, though getStage() is preferred.
     */
    protected final Stage MY_PRIMARY_STAGE; // Renamed for convention
    /**
     * Controller used for event handlers. Passed in, not created here.
     * Made protected so subclasses can access if absolutely needed, though getController() is preferred.
     */
    protected final Controller MY_CONTROLLER; // Renamed for convention

    /**
     * Width for consistent button widths.
     */
    private static final double BUTTON_WIDTH = 100; // Made static as it's a constant for all screens
    /**
     * Height for consistent button heights.
     */
    private static final double BUTTON_HEIGHT = 20; // Made static

    /**
     * Assigns Stage and Controller used by Screen subclasses.
     * The Controller instance is shared across screens and typically created in GameUI or Main.
     *
     * @param thePrimaryStage The primary Stage for the application.
     * @param theController   The main application Controller instance.
     */
    protected Screen(final Stage thePrimaryStage, final Controller theController) { // Changed to protected or public
        if (thePrimaryStage == null) {
            throw new IllegalArgumentException("Primary stage cannot be null.");
        }
        if (theController == null) {
            throw new IllegalArgumentException("Controller cannot be null.");
        }
        this.MY_PRIMARY_STAGE = thePrimaryStage;
        this.MY_PRIMARY_STAGE.setTitle("DungeonDive"); // Setting title can be done once
        this.MY_CONTROLLER = theController;
    }

    /**
     * Abstract method for showing the current screen.
     *
     * @param theUI The GameUI instance, used for managing screen transitions and UI updates.
     */
    public abstract void showScreen(final GameUI theUI); // Changed to public

    /**
     * Method for all screen classes to create button sizes quickly
     * and consistently.
     *
     * @param theButton the Button to be given a size.
     */
    public void setButtonSize(final Button theButton) {
        if (theButton != null) {
            theButton.setPrefWidth(BUTTON_WIDTH);
            theButton.setPrefHeight(BUTTON_HEIGHT);
        }
    }

    /**
     * Gives a VBox of buttons that are given text for the win and
     * lose screens.
     * Note: Event handlers for these buttons would need to be set, likely using MY_CONTROLLER.
     *
     * @return A VBox of buttons.
     */
    public VBox tripleButtonVBox(final GameUI theUI) { // Pass GameUI for actions
        VBox buttons = new VBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button newGameBtn = new Button("New Game");
        Button savedGamesBtn = new Button("Saved Games"); // Placeholder, needs save/load screen
        Button quitToMenuBtn = new Button("Quit to Menu");

        setButtonSize(newGameBtn);
        setButtonSize(savedGamesBtn);
        setButtonSize(quitToMenuBtn);

        // Example event handlers (these would need to be adapted)
        newGameBtn.setOnAction(event -> MY_CONTROLLER.newGame(event, theUI)); // Assuming Controller has newGame
        savedGamesBtn.setOnAction(event -> MY_CONTROLLER.savedGames(event, theUI)); // Assuming Controller has savedGames
        quitToMenuBtn.setOnAction(event -> MY_CONTROLLER.quitToMenu(theUI)); // Assuming Controller has quitToMenu

        buttons.getChildren().addAll(newGameBtn, savedGamesBtn, quitToMenuBtn);
        return buttons;
    }

    /**
     * Sets the placements of buttons and title for the intro, win and
     * lose screens.
     *
     * @param theTitle The title Text node.
     * @param theButtons The VBox containing buttons.
     * @param theRoot The BorderPane root layout.
     */
    public void tripleButtonStructure(final Text theTitle, final VBox theButtons,
                                      final BorderPane theRoot) {
        if (theTitle != null) {
            BorderPane.setAlignment(theTitle, Pos.TOP_CENTER);
            theRoot.setTop(theTitle);
        }
        if (theButtons != null) {
            BorderPane.setAlignment(theButtons, Pos.CENTER);
            theRoot.setCenter(theButtons);
        }
    }

    /**
     * Getter for PRIMARY_STAGE.
     *
     * @return PRIMARY_STAGE.
     */
    public Stage getStage() {
        return MY_PRIMARY_STAGE;
    }

    /**
     * Getter for CONTROLLER.
     *
     * @return CONTROLLER.
     */
    public Controller getController() {
        return MY_CONTROLLER;
    }
}
