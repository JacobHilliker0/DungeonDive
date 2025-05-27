package main.View.screen;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.View.GameUI;



public class HelpScreen extends Screen {

    public HelpScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);

    }
    @Override
    public void showScreen(final GameUI theUI) {
        BorderPane root = new BorderPane();
        Scene helpScene = new Scene(root, 600, 500);

        Label gameObjective = new Label("GAME INSTRUCTIONS: Find rooms with pillars" +
                " and click interact to collect them. After all 4 have been collected," +
                "\nfind the exit to win. You can also find chests and items in some rooms. " +
                "Some rooms have traps and monsters.\nIf your health gets to 0, you will lose the game.");
        BorderPane.setAlignment(gameObjective, Pos.TOP_CENTER);
        root.setTop(gameObjective);

        VBox keybindsVBox = new VBox(10);
        Label keybinds = new Label("--- DUNGEON MOVEMENT ---\n" +
                "w/\u2191 - move up\na/\u2190 - move left\n" +
                "d/\u2192 - move right\ns/\u2193 - move down\n" +
                "--- COMBAT CONTROLS ---\na - Attack\ns - Special Attack\nr - Run\n" +
                "i - inventory\nesc - pause game\n--- INVENTORY CONTROLS ---\nw - scroll up" +
                "\ns - scroll down\nEnter - use selected item\nesc - close inventory");
        keybindsVBox.getChildren().add(keybinds);
        root.setCenter(keybinds);

        Button resumeGameButton = new Button("Resume Game");
        setButtonSize(resumeGameButton);
        resumeGameButton.setOnAction(event -> getController().resumeCurrentGame(theUI));
        BorderPane.setAlignment(resumeGameButton, Pos.BOTTOM_CENTER);
        root.setBottom(resumeGameButton);

        getStage().setScene(helpScene);
        getStage().setTitle("Help Menu");
        getStage().show();
    }
}
