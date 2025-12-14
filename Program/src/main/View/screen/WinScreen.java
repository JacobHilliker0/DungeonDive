package main.View.screen;

import main.Controller.Controller;
import main.View.GameUI;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Class for the WinScreen.
 * This screen is displayed when the player successfully completes the game,
 * offering congratulations and options to start a new game or exit.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/10/2025
 */
public class WinScreen extends Screen {

    /**
     * Constructor for WinScreen.
     *
     * @param thePrimaryStage The primary Stage for the application.
     * @param theController   The main application Controller instance.
     */
    public WinScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    /**
     * Sets up and displays the win screen.
     * This method constructs a scene with a victory message and buttons for
     * starting a new game or quitting.
     *
     * @param theUI The GameUI instance used for screen transitions and button actions.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        BorderPane root = new BorderPane();
        // Use default dimensions from the Screen superclass
        Scene winScene = new Scene(root, BASE_WIDTH, BASE_HEIGHT);

        // Apply a default background style
        root.setStyle("-fx-background-color: #1a1a1a;");

        Text title = new Text("Congratulations! You Won!");
        title.setFont(Font.font("Verdana", 30));
        title.setStyle("-fx-fill: limegreen; -fx-effect: dropshadow(gaussian, rgba(0,255,0,0.5), 10, 0, 0, 0);");

        // Use the tripleButtonVBox from Screen superclass for consistent button layout
        VBox buttons = tripleButtonVBox(theUI);

        // Use the tripleButtonStructure from Screen superclass to arrange title and buttons
        tripleButtonStructure(title, buttons, root);

        getStage().setScene(winScene);
        getStage().setTitle("Dungeon Dive - Victory!");
        getStage().show();
    }
}