package main.View.screen;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.View.GameUI;

/**
 * Class for the LoseScreen.
 * Displayed when the player is defeated.
 * Now with responsive scaling and enhanced visual design.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/10/2025
 */
public class LoseScreen extends Screen {

    private static final int TITLE_FONT_SIZE = 40;
    private static final int BUTTON_FONT_SIZE = 24;
    private static final int CONTENT_SPACING = 60;

    /**
     * Constructor for LoseScreen.
     *
     * @param thePrimaryStage The primary Stage for the application.
     * @param theController   The main application Controller instance.
     */
    public LoseScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    /**
     * showScreen for the LoseScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff and screen transitions.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        // Use a StackPane to hold the content and apply scaling
        StackPane scalableRoot = new StackPane();

        // The actual content pane
        BorderPane contentPane = new BorderPane();
        contentPane.setStyle("-fx-padding: 50;");

        setupBackground(contentPane);

        Text title = createTitle();
        VBox buttons = tripleButtonVBox(theUI);

        // Use the tripleButtonStructure from Screen superclass
        tripleButtonStructure(title, buttons, contentPane);

        scalableRoot.getChildren().add(contentPane);

        Scene loseScene = new Scene(scalableRoot, getStage().getScene().getWidth(), getStage().getScene().getHeight());
        applyScaling(scalableRoot, loseScene);

        getStage().setScene(loseScene);
        getStage().setTitle("Dungeon Dive - Game Over");
        getStage().show();
    }

    private void setupBackground(BorderPane root) {
        // Dark, ominous background for defeat screen
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a0000, #000000); " +
                "-fx-border-color: #8B0000; " +
                "-fx-border-width: 3px;");
    }

    private Text createTitle() {
        Font titleFont = loadFont("/main/View/fonts/PixelFont.ttf", TITLE_FONT_SIZE, "Impact");

        Text title = new Text("YOU HAVE BEEN DEFEATED!");
        title.setFont(titleFont);
        title.setFill(Color.DARKRED);
        title.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(139,0,0,0.8), 10, 0.5, 4, 4); " +
                "-fx-stroke: #8B0000; " +
                "-fx-stroke-width: 2px;");

        return title;
    }
}