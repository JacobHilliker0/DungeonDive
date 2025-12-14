package main.View.screen;

import javafx.scene.control.*;
import main.Controller.Controller;
import main.View.GameUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

// Additional imports needed for the blur effect and image processing
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.sql.ResultSet;
import java.util.Optional;

/**
 * Class for the PauseScreen.
 * Shown when the player pauses the game.
 * Now with responsive scaling and consistent styling.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/11/2025
 */
public class PauseScreen extends Screen {

    // Constants for styling
    private static final int TITLE_FONT_SIZE = 45;
    private static final int BUTTON_FONT_SIZE = 22;
    private static final int CONTENT_SPACING = 40;
    private static final int BUTTON_SPACING = 20;
    private static final int TOP_PADDING = 50;

    private static final String FONT_PATH = "/main/View/fonts/PixelFont.ttf";
    private static final String SHADOW_STYLE = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";

    /**
     * Constructor for PauseScreen.
     *
     * @param thePrimaryStage The primary Stage for the application.
     * @param theController   The main application Controller instance.
     */
    public PauseScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    @Override
    public void showScreen(final GameUI theUI) {
        // Use a StackPane as the root for proper layering and scaling
        StackPane root = new StackPane();

        // Get current scene dimensions, or use defaults for adaptability
        double currentWidth = getStage().getScene() != null ? getStage().getScene().getWidth() : BASE_WIDTH;
        double currentHeight = getStage().getScene() != null ? getStage().getScene().getHeight() : BASE_HEIGHT;
        Scene pauseScene = new Scene(root, currentWidth, currentHeight);

        // 1. Create and add the blurred background
        ImageView backgroundView = createBlurredBackground(theUI);
        if (backgroundView != null) {
            backgroundView.fitWidthProperty().bind(root.widthProperty());
            backgroundView.fitHeightProperty().bind(root.heightProperty());
            root.getChildren().add(backgroundView);
        } else {
            root.setStyle("-fx-background-color: #2C2C2C;");
        }

        // 2. Create and add a dark overlay for text readability
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        root.getChildren().add(overlay);

        // 3. Create the menu content
        VBox centerContent = new VBox(CONTENT_SPACING);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(TOP_PADDING, 50, 50, 50));
        centerContent.setMaxWidth(BASE_WIDTH);

        VBox titleBox = createTitle();
        VBox buttonBox = createButtons(theUI);

        centerContent.getChildren().addAll(titleBox, buttonBox);
        root.getChildren().add(centerContent);

        // 4. Apply parent's scaling logic for responsiveness
        applyScaling(root, pauseScene);

        // 5. Set the scene and show the stage
        getStage().setScene(pauseScene);
        getStage().setTitle("Dungeon Dive - Game Paused");
        getStage().show();
    }

    /**
     * Creates a blurred snapshot of the current game scene.
     * @param theUI The GameUI instance to get the current scene from.
     * @return An ImageView containing the blurred snapshot, or null if it fails.
     */
    private ImageView createBlurredBackground(GameUI theUI) {
        try {
            // Check if there's an active game first
            if (getController().getPlayer() == null) {
                return null; // No game running, use solid background
            }

            // Temporarily switch to game screen to capture it
            theUI.showGameScreen();

            // Get the game screen scene after switching to it
            Scene gameScene = getStage().getScene();
            if (gameScene != null) {
                WritableImage snapshot = gameScene.snapshot(null);
                ImageView backgroundImage = new ImageView(snapshot);
                GaussianBlur blur = new GaussianBlur();
                blur.setRadius(20);
                backgroundImage.setEffect(blur);
                return backgroundImage;
            }
        } catch (Exception e) {
            System.err.println("Could not create blurred game screen background: " + e.getMessage());
        }
        return null;
    }

    /**
     * Creates the title section using themed fonts and styles.
     */
    private VBox createTitle() {
        Font titleFont = loadFont(FONT_PATH, TITLE_FONT_SIZE, "Impact");

        VBox titleBox = new VBox(10);
        titleBox.setAlignment(Pos.CENTER);

        Text gameText = new Text("GAME");
        gameText.setFont(titleFont);
        gameText.setFill(Color.ORANGE);
        gameText.setStyle(SHADOW_STYLE);

        Text pausedText = new Text("PAUSED");
        pausedText.setFont(titleFont);
        pausedText.setFill(Color.ORANGE);
        pausedText.setStyle(SHADOW_STYLE);

        titleBox.getChildren().addAll(gameText, pausedText);
        return titleBox;
    }

    /**
     * Creates the button section using the parent's styled buttons.
     */
    private VBox createButtons(GameUI theUI) {
        Font buttonFont = loadFont(FONT_PATH, BUTTON_FONT_SIZE, "Courier New");

        VBox buttonBox = new VBox(BUTTON_SPACING);
        buttonBox.setAlignment(Pos.CENTER);

        // Use the standardized createStyledButton from the Screen superclass
        Button resumeBtn = createStyledButton("RESUME GAME", buttonFont);
        Button saveBtn = createStyledButton("SAVE GAME", buttonFont);
        Button loadBtn = createStyledButton("LOAD GAME", buttonFont);
        Button quitToMenuBtn = createStyledButton("QUIT TO MENU", buttonFont);

        // Set button actions
        resumeBtn.setOnAction(event -> getController().resumeCurrentGame(theUI));
        saveBtn.setOnAction(event -> showSaveDialog(theUI));
        loadBtn.setOnAction(event -> theUI.showSavesScreen());
        quitToMenuBtn.setOnAction(event -> getController().quitToMenu(theUI));

        buttonBox.getChildren().addAll(resumeBtn, saveBtn, loadBtn, quitToMenuBtn);
        return buttonBox;
    }

    /**
     * Shows save dialog with custom naming.
     */
    private void showSaveDialog(GameUI theUI) {
        TextInputDialog dialog = new TextInputDialog("My Save");
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Enter a name for your save:");
        dialog.setContentText("Save name:");
        dialog.initOwner(getStage());

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String saveName = result.get().trim();

            if (saveNameExists(saveName)) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Overwrite Save");
                alert.setHeaderText("A save with the name '" + saveName + "' already exists. Overwrite it?");
                alert.initOwner(getStage());

                Optional<ButtonType> confirmResult = alert.showAndWait();
                if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
                    getController().getGameController().saveGameWithName(saveName);
                }
            } else {
                getController().getGameController().saveGameWithName(saveName);
            }
        } else {
            System.out.println("Save cancelled or no name provided");
        }
    }

    /**
     * Checks if a save name already exists in the database.
     */
    private boolean saveNameExists(String saveName) {
        try (ResultSet rs = getController().getGameModel().getDatabase().loadGameData(saveName)) {
            return rs != null && rs.next();
        } catch (Exception e) {
            System.err.println("Error checking if save exists: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads a game directly from the database and initializes the game state
     */
    private boolean loadGameFromDatabase(String saveName, GameUI theUI) {
        try (ResultSet rs = getController().getGameModel().getDatabase().loadGameData(saveName)) {
            if (rs != null && rs.next()) {
                String playerData = rs.getString("player_data");
                String dungeonData = rs.getString("dungeon_data");
                String gameStateData = rs.getString("game_state");

                getController().initializeGameControllersForLoadedGame(theUI);

                boolean loaded = getController().getGameController().loadGameFromSaveData(
                        playerData, dungeonData, gameStateData);

                if (loaded) {
                    System.out.println("Game loaded successfully from PauseScreen");
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading game from PauseScreen: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}