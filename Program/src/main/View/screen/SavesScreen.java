package main.View.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.View.GameUI;

import java.io.InputStream;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Class for the SavesScreen.
 * Allows players to view, load, and delete previously saved games
 * retrieved from a database. It features a scrollable list of save files
 * and provides options to return to the main menu or pause menu.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/10/2025
 */
public class SavesScreen extends Screen {

    //<editor-fold desc="UI Constants">
    /** Base font size for the screen title. */
    private static final int BASE_TITLE_FONT_SIZE = 45;
    /** Base font size for the main action buttons (e.g., Back). */
    private static final int BASE_BUTTON_FONT_SIZE = 22;
    /** Base font size for the text within each save slot button. */
    private static final int BASE_SAVE_BUTTON_FONT_SIZE = 14;
    /** Base font size for the delete button. */
    private static final int BASE_DELETE_BUTTON_FONT_SIZE = 16;
    /** Base font size for informational messages (e.g., "No saves found"). */
    private static final int BASE_MESSAGE_FONT_SIZE = 18;

    /** Base vertical spacing for the main content VBox. */
    private static final int BASE_CONTENT_SPACING = 20;
    /** Base top padding for the root layout. */
    private static final int BASE_TOP_PADDING = 60;
    /** Base vertical spacing between items in the saves list. */
    private static final int BASE_SAVE_LIST_SPACING = 15;
    /** Base horizontal spacing within a single save row (between load and delete buttons). */
    private static final int BASE_SAVE_ROW_SPACING = 15;

    /** Base width for the 'Back to Menu' button. */
    private static final int BASE_BACK_BUTTON_WIDTH = 420;
    /** Base height for the 'Back to Menu' button. */
    private static final int BASE_BACK_BUTTON_HEIGHT = 45;
    /** Base width for a save slot button. */
    private static final int BASE_SAVE_BUTTON_WIDTH = 450;
    /** Base height for a save slot button. */
    private static final int BASE_SAVE_BUTTON_HEIGHT = 80;
    /** Base width for the delete button. */
    private static final int BASE_DELETE_BUTTON_WIDTH = 160;
    /** Base height for the delete button. */
    private static final int BASE_DELETE_BUTTON_HEIGHT = 80;

    /** Base preferred height for the scrollable area containing the saves list. */
    private static final int BASE_SCROLL_AREA_HEIGHT = 300;

    /** Path to the custom pixel font resource. */
    private static final String FONT_PATH = "/main/View/fonts/PixelFont.ttf";
    /** Path to the background image resource. */
    private static final String BACKGROUND_PATH = "/sprites/backgrounds/brick_wall_background.png";
    /** CSS style for the drop shadow effect on text. */
    private static final String SHADOW_STYLE = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";

    /** Unique CSS style for the delete button. */
    private static final String DELETE_BUTTON_STYLE =
            "-fx-background-color: #8B0000; -fx-text-fill: #E0E0E0; -fx-border-color: #8B4513; " +
                    "-fx-border-width: 4px; -fx-padding: 10px 15px; -fx-background-radius: 0; -fx-border-radius: 0; " +
                    "-fx-alignment: center; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 2, 2);";

    /** A tan color constant used for message text. */
    private static final Color TAN_COLOR = Color.rgb(222, 184, 135);
    //</editor-fold>

    /** The scene for this screen. */
    private Scene scene;

    /**
     * Constructs a SavesScreen.
     *
     * @param thePrimaryStage The primary stage of the application.
     * @param theController   The main controller for handling user actions.
     */
    public SavesScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    /**
     * Sets up and displays the saved games screen.
     * This method builds the scene graph, including the title, background, a scrollable
     * list of saved games from the database, and action buttons.
     *
     * @param theUI The main GameUI instance, used for navigation.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        VBox contentPane = createRootLayout();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(contentPane);
        Stage currentStage = getStage();

        double width = currentStage.getScene() != null ?
                currentStage.getScene().getWidth() : BASE_WIDTH;
        double height = currentStage.getScene() != null ?
                currentStage.getScene().getHeight() : BASE_HEIGHT;

        scene = new Scene(root, width, height);

        setupBackground(root);
        setupComponents(contentPane, theUI);

        // Use parent's scaling method
        applyScaling(contentPane, scene);

        boolean wasFullScreen = currentStage.isFullScreen();

        currentStage.setScene(scene);
        currentStage.setTitle("Dungeon Dive - Load Game");

        if (wasFullScreen && !currentStage.isFullScreen()) {
            currentStage.setFullScreen(true);
        }

        if (!currentStage.isShowing()) {
            currentStage.show();
        }
    }

    /**
     * Creates the root VBox layout for the screen content.
     * @return A configured VBox to serve as the main content pane.
     */
    private VBox createRootLayout() {
        VBox root = new VBox(BASE_CONTENT_SPACING);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(BASE_TOP_PADDING, 20, 20, 20));
        root.setMaxWidth(BASE_WIDTH * 1.2);
        root.setMaxHeight(BASE_HEIGHT * 1.2);
        root.setMinHeight(BASE_HEIGHT * 0.8);
        return root;
    }

    /**
     * Sets the background for the screen.
     * @param root The root pane to which the background style will be applied.
     */
    private void setupBackground(javafx.scene.layout.Pane root) {
        try (InputStream bgStream = getResourceStream(BACKGROUND_PATH)) {
            if (bgStream != null) {
                root.setStyle(buildBackgroundStyle());
            } else {
                root.setStyle("-fx-background-color: #202020;");
                logError("Background image not found: " + BACKGROUND_PATH + ". Using fallback color.");
            }
        } catch (Exception e) {
            root.setStyle("-fx-background-color: #202020;");
            logError("Error loading background: " + e.getMessage());
        }
    }

    /**
     * Builds the CSS string for the background style.
     * @return The CSS style string for the background image.
     */
    private String buildBackgroundStyle() {
        return "-fx-background-image: url('" + BACKGROUND_PATH + "'); " +
                "-fx-background-repeat: no-repeat; " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center center;";
    }

    /**
     * Initializes and adds all UI components (title, saves list, buttons) to the root pane.
     * @param root The root VBox to which components will be added.
     * @param theUI The GameUI instance for handling actions.
     */
    private void setupComponents(VBox root, GameUI theUI) {
        FontBundle fonts = loadFonts();

        Text titleText = createTitle(fonts.title);
        VBox titleContainer = new VBox();
        titleContainer.setAlignment(Pos.CENTER);
        titleContainer.setPadding(new Insets(0, 0, 20, 0));
        titleContainer.getChildren().add(titleText);

        ScrollPane savesList = createSavesListArea(fonts, theUI);
        Button backButton = createBackButton(fonts.button, theUI);

        root.getChildren().addAll(titleContainer, savesList, backButton);
    }

    /**
     * A private static inner class to bundle all the required fonts for the screen.
     */
    private static class FontBundle {
        final Font title, button, saveButton, deleteButton, message;

        /**
         * Constructs a FontBundle.
         * @param title Font for the main title.
         * @param button Font for primary action buttons.
         * @param saveButton Font for save slot buttons.
         * @param deleteButton Font for delete buttons.
         * @param message Font for informational messages.
         */
        FontBundle(Font title, Font button, Font saveButton, Font deleteButton, Font message) {
            this.title = title;
            this.button = button;
            this.saveButton = saveButton;
            this.deleteButton = deleteButton;
            this.message = message;
        }
    }

    /**
     * Loads all necessary fonts for the screen.
     * @return A {@link FontBundle} containing all loaded fonts.
     */
    private FontBundle loadFonts() {
        return new FontBundle(
                loadFont(FONT_PATH, BASE_TITLE_FONT_SIZE, "Impact"),
                loadFont(FONT_PATH, BASE_BUTTON_FONT_SIZE, "Courier New"),
                loadFont(FONT_PATH, BASE_SAVE_BUTTON_FONT_SIZE, "Courier New"),
                loadFont(FONT_PATH, BASE_DELETE_BUTTON_FONT_SIZE, "Courier New"),
                loadFont(FONT_PATH, BASE_MESSAGE_FONT_SIZE, "Arial")
        );
    }

    /**
     * Creates the main title Text node.
     * @param titleFont The font to use for the title.
     * @return A configured Text node for the title.
     */
    private Text createTitle(Font titleFont) {
        Text titleText = new Text("LOAD GAME");
        titleText.setFont(titleFont);
        titleText.setFill(Color.ORANGE);
        titleText.setStyle(SHADOW_STYLE);
        titleText.setVisible(true);
        titleText.setManaged(true);
        return titleText;
    }

    /**
     * Creates the scrollable area that will contain the list of save files.
     * @param fonts The FontBundle containing fonts for styling.
     * @param theUI The GameUI instance for handling actions.
     * @return A configured ScrollPane containing the list of saves.
     */
    private ScrollPane createSavesListArea(FontBundle fonts, GameUI theUI) {
        VBox savesList = new VBox(BASE_SAVE_LIST_SPACING);
        savesList.setAlignment(Pos.TOP_CENTER);
        savesList.setPadding(new Insets(20));

        populateSavesList(savesList, fonts, theUI);

        ScrollPane scrollPane = new ScrollPane(savesList);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        scrollPane.setPrefViewportHeight(BASE_SCROLL_AREA_HEIGHT);
        scrollPane.setMinHeight(150);
        scrollPane.setMaxHeight(BASE_SCROLL_AREA_HEIGHT * 1.5);

        scrollPane.setStyle(
                "-fx-background: rgba(42, 42, 42, 0.9); " +
                        "-fx-background-color: rgba(42, 42, 42, 0.9); " +
                        "-fx-border-color: #8B4513; " +
                        "-fx-border-width: 4px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 8, 0.3, 3, 3);"
        );

        return scrollPane;
    }

    /**
     * Fetches save games from the database and populates the UI list.
     * @param savesList The VBox container for the save rows.
     * @param fonts The FontBundle for styling.
     * @param theUI The GameUI instance for button actions.
     */
    private void populateSavesList(VBox savesList, FontBundle fonts, GameUI theUI) {
        try (ResultSet saves = getController().getGameModel().getDatabase().getAllSaveGames()) {
            boolean hasSaves = false;
            if (saves != null) {
                while (saves.next()) {
                    hasSaves = true;
                    String saveName = saves.getString("save_name");
                    String saveDate = saves.getString("save_date");
                    HBox saveRow = createSaveRow(saveName, formatSaveDate(saveDate), fonts, theUI);
                    savesList.getChildren().add(saveRow);
                }
            }
            if (!hasSaves) {
                savesList.getChildren().add(createNoSavesLabel(fonts.message));
            }
        } catch (Exception e) {
            System.err.println("Error loading save games: " + e.getMessage());
            e.printStackTrace();
            savesList.getChildren().add(createErrorLabel(fonts.message));
        }
    }

    /**
     * Creates a single horizontal row for a save file, containing load and delete buttons.
     * @param saveName The name of the save file.
     * @param formattedDate The formatted date of the save.
     * @param fonts The FontBundle for styling.
     * @param theUI The GameUI instance for actions.
     * @return An HBox representing a single save entry.
     */
    private HBox createSaveRow(String saveName, String formattedDate, FontBundle fonts, GameUI theUI) {
        HBox saveRow = new HBox(BASE_SAVE_ROW_SPACING);
        saveRow.setAlignment(Pos.CENTER);

        Button saveButton = createSaveButton(saveName, formattedDate, fonts.saveButton, theUI);
        Button deleteButton = createDeleteButton(saveName, fonts.deleteButton, theUI);

        saveRow.getChildren().addAll(saveButton, deleteButton);
        return saveRow;
    }

    /**
     * Creates a styled button for loading a specific save game.
     * @param saveName The name of the save.
     * @param formattedDate The formatted date of the save.
     * @param buttonFont The font for the button text.
     * @param theUI The GameUI instance for actions.
     * @return A configured Button for loading a game.
     */
    private Button createSaveButton(String saveName, String formattedDate, Font buttonFont, GameUI theUI) {
        Button saveButton = createStyledButton(saveName + "\n" + formattedDate, buttonFont);

        saveButton.setPrefWidth(BASE_SAVE_BUTTON_WIDTH);
        saveButton.setPrefHeight(BASE_SAVE_BUTTON_HEIGHT);
        saveButton.setMinWidth(BASE_SAVE_BUTTON_WIDTH);
        saveButton.setMinHeight(BASE_SAVE_BUTTON_HEIGHT);
        saveButton.setWrapText(true);

        String customStyle =
                "-fx-background-color: #2C2C2C; " +
                        "-fx-text-fill: #E0E0E0; " +
                        "-fx-border-color: #DAA520; " +
                        "-fx-border-width: 4px; " +
                        "-fx-padding: 10px 15px; " +
                        "-fx-background-radius: 0; " +
                        "-fx-border-radius: 0; " +
                        "-fx-alignment: center; " +
                        "-fx-text-alignment: center; " +
                        "-fx-content-display: center; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 2, 2);";
        saveButton.setStyle(customStyle);

        saveButton.setOnMouseEntered(e -> saveButton.setStyle(customStyle + "-fx-background-color: #3C3C3C;"));
        saveButton.setOnMouseExited(e -> saveButton.setStyle(customStyle));

        saveButton.setOnAction(event -> {
            if (loadGameFromDatabase(saveName, theUI)) {
                theUI.showGameScreen();
            } else {
                showErrorAlert("Load Failed", "Failed to load game: " + saveName);
            }
        });
        return saveButton;
    }

    /**
     * Creates a styled button for deleting a specific save game.
     * @param saveName The name of the save to delete.
     * @param buttonFont The font for the button text.
     * @param theUI The GameUI instance for refreshing the screen after deletion.
     * @return A configured Button for deleting a game.
     */
    private Button createDeleteButton(String saveName, Font buttonFont, GameUI theUI) {
        Button deleteButton = new Button("DELETE");
        deleteButton.setFont(buttonFont);
        deleteButton.setPrefWidth(BASE_DELETE_BUTTON_WIDTH);
        deleteButton.setPrefHeight(BASE_DELETE_BUTTON_HEIGHT);
        deleteButton.setMinWidth(BASE_DELETE_BUTTON_WIDTH);
        deleteButton.setMinHeight(BASE_DELETE_BUTTON_HEIGHT);
        deleteButton.setStyle(DELETE_BUTTON_STYLE);

        deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(DELETE_BUTTON_STYLE + "-fx-background-color: #A52A2A;"));
        deleteButton.setOnMouseExited(e -> deleteButton.setStyle(DELETE_BUTTON_STYLE));

        deleteButton.setOnAction(event -> handleDeleteSave(saveName, theUI));
        return deleteButton;
    }

    /**
     * Creates a label to display when no saved games are found.
     * @param messageFont The font for the label.
     * @return A styled Label indicating no saves were found.
     */
    private Label createNoSavesLabel(Font messageFont) {
        Label noSavesLabel = new Label("No saved games found.");
        noSavesLabel.setFont(messageFont);
        noSavesLabel.setTextFill(TAN_COLOR);
        noSavesLabel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 3, 0, 1, 1);");
        return noSavesLabel;
    }

    /**
     * Creates a label to display when there is an error loading from the database.
     * @param messageFont The font for the label.
     * @return A styled Label indicating a data loading error.
     */
    private Label createErrorLabel(Font messageFont) {
        Label errorLabel = new Label("Error: Could not load save games from database.");
        errorLabel.setFont(messageFont);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 3, 0, 1, 1);");
        return errorLabel;
    }

    /**
     * Creates the 'Back to Menu' button.
     * @param buttonFont The font for the button.
     * @param theUI The GameUI instance for handling the back action.
     * @return A configured Button to go back.
     */
    private Button createBackButton(Font buttonFont, GameUI theUI) {
        Button backButton = createStyledButton("BACK TO MENU", buttonFont);
        backButton.setPrefWidth(BASE_BACK_BUTTON_WIDTH);
        backButton.setPrefHeight(BASE_BACK_BUTTON_HEIGHT);
        backButton.setOnAction(_ -> handleBackButtonAction(theUI));
        return backButton;
    }

    /**
     * Handles the action for the 'Back' button, navigating to the appropriate screen.
     * Navigates to the pause menu if a game is active, otherwise to the intro screen.
     * @param theUI The GameUI instance to control screen transitions.
     */
    private void handleBackButtonAction(GameUI theUI) {
        boolean hasActiveGame = getController().getPlayer() != null;
        if (hasActiveGame) {
            theUI.showPauseMenu();
        } else {
            theUI.showIntroScreen();
        }
    }

    /**
     * Handles the logic for deleting a save file, including user confirmation.
     * @param saveName The name of the save to delete.
     * @param theUI The GameUI instance to refresh the screen upon success.
     */
    private void handleDeleteSave(String saveName, GameUI theUI) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Save");
        alert.setHeaderText("Are you sure you want to delete this save?");
        alert.setContentText("This action cannot be undone.\nSave: '" + saveName + "'");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (getController().getGameModel().getDatabase().deleteSaveGame(saveName)) {
                showScreen(theUI); // Refresh the screen
            } else {
                showErrorAlert("Delete Failed", "Could not delete the save file from the database.");
            }
        }
    }

    /**
     * Loads game data from the database using the specified save name.
     * @param saveName The name of the save file to load.
     * @param theUI The GameUI instance for initializing controllers.
     * @return True if the game was loaded successfully, false otherwise.
     */
    private boolean loadGameFromDatabase(String saveName, GameUI theUI) {
        try (ResultSet rs = getController().getGameModel().getDatabase().loadGameData(saveName)) {
            if (rs != null && rs.next()) {
                String playerData = rs.getString("player_data");
                String dungeonData = rs.getString("dungeon_data");
                String gameStateData = rs.getString("game_state");
                getController().initializeGameControllersForLoadedGame(theUI);
                return getController().getGameController().loadGameFromSaveData(playerData, dungeonData, gameStateData);
            }
        } catch (Exception e) {
            System.err.println("Error loading game from SavesScreen: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Formats a raw date string from the database into a more readable format.
     * @param saveDate The raw date string (e.g., ISO_LOCAL_DATE_TIME format).
     * @return A formatted date string (e.g., "Jun 10, 2025 - 20:13").
     */
    private String formatSaveDate(String saveDate) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(saveDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - HH:mm");
            return dateTime.format(formatter);
        } catch (Exception e) {
            return saveDate; // Return original if formatting fails
        }
    }

    /**
     * Displays a simple error alert dialog.
     * @param title The title of the alert window.
     * @param message The content message of the alert.
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Gets an InputStream for a resource located on the classpath.
     * @param path The path to the resource.
     * @return An InputStream for the resource, or null if not found.
     */
    private InputStream getResourceStream(String path) {
        return getClass().getResourceAsStream(path);
    }


    /**
     * Logs an error message to the standard error stream.
     * @param message The error message to log.
     */
    private void logError(String message) {
        System.err.println(message);
    }
}