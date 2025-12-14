package main.View.screen;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.View.GameUI;
import java.io.InputStream;

/**
 * An abstract class for the various Screens.
 * Includes common functionality for creating scalable and responsive UIs.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/10/2025
 */
public abstract class Screen {
    protected final Stage MY_PRIMARY_STAGE;
    protected final Controller MY_CONTROLLER;

    // Base dimensions for scaling calculations
    protected static final double BASE_WIDTH = 800.0;
    protected static final double BASE_HEIGHT = 600.0;
    private static final double MIN_SCALE = 0.7;
    private static final double MAX_SCALE = 2.0;

    protected Screen(final Stage thePrimaryStage, final Controller theController) {
        if (thePrimaryStage == null) {
            throw new IllegalArgumentException("Primary stage cannot be null.");
        }
        if (theController == null) {
            throw new IllegalArgumentException("Controller cannot be null.");
        }
        this.MY_PRIMARY_STAGE = thePrimaryStage;
        this.MY_CONTROLLER = theController;
    }

    public abstract void showScreen(final GameUI theUI);

    /**
     * Applies a scaling transformation to the root pane to make the UI responsive.
     * @param root The root pane of the scene (e.g., StackPane).
     * @param scene The scene containing the root pane.
     */
    protected void applyScaling(Pane root, Scene scene) {
        NumberBinding scaleBinding = Bindings.createDoubleBinding(() -> {
            double widthScale = scene.getWidth() / BASE_WIDTH;
            double heightScale = scene.getHeight() / BASE_HEIGHT;
            double scale = Math.min(widthScale, heightScale);
            return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
        }, scene.widthProperty(), scene.heightProperty());

        root.scaleXProperty().bind(scaleBinding);
        root.scaleYProperty().bind(scaleBinding);
    }

    /**
     * Loads a custom font from the resources, with a fallback to a system font.
     * @param path The resource path to the font file.
     * @param size The desired font size.
     * @param fallbackFamily The name of the fallback font family.
     * @return The loaded Font object.
     */
    protected Font loadFont(String path, int size, String fallbackFamily) {
        try (InputStream fontStream = getClass().getResourceAsStream(path)) {
            if (fontStream != null) {
                Font customFont = Font.loadFont(fontStream, size);
                if (customFont != null) return customFont;
            }
        } catch (Exception e) {
            System.err.println("Failed to load custom font " + path + ". Using fallback. Error: " + e.getMessage());
        }
        return Font.font(fallbackFamily, size);
    }

    /**
     * Creates a standardized VBox for screens with a title and three buttons.
     * @param theUI The GameUI instance.
     * @return A VBox containing the standard button layout.
     */
    public VBox tripleButtonVBox(final GameUI theUI) {
        VBox buttons = new VBox(20); // Increased spacing
        buttons.setAlignment(Pos.CENTER);

        Font buttonFont = loadFont("/main/View/fonts/PixelFont.ttf", 24, "Courier New");

        Button newGameBtn = createStyledButton("New Game", buttonFont);
        Button savedGamesBtn = createStyledButton("Load Game", buttonFont);
        Button quitToMenuBtn = createStyledButton("Quit to Menu", buttonFont);

        newGameBtn.setOnAction(event -> MY_CONTROLLER.newGame(event, theUI));
        savedGamesBtn.setOnAction(event -> MY_CONTROLLER.savedGames(event, theUI));
        quitToMenuBtn.setOnAction(event -> MY_CONTROLLER.quitToMenu(theUI));

        buttons.getChildren().addAll(newGameBtn, savedGamesBtn, quitToMenuBtn);
        return buttons;
    }

    /**
     * Creates a styled button with a consistent look and feel.
     * @param text The text for the button.
     * @param font The font for the button text.
     * @return A styled Button.
     */
    protected Button createStyledButton(String text, Font font) {
        Button button = new Button(text);
        button.setFont(font);

        String buttonStyle = "-fx-background-color: #2C2C2C; " +
                "-fx-text-fill: #E0E0E0; " +
                "-fx-border-color: #8B4513; " +
                "-fx-border-width: 4px; " +
                "-fx-padding: 15px 40px; " +
                "-fx-background-radius: 0; " +
                "-fx-border-radius: 0; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 2, 2);";

        button.setStyle(buttonStyle);
        button.setMinWidth(300);
        button.setMinHeight(60);

        // Add hover effects
        button.setOnMouseEntered(e ->
                button.setStyle(buttonStyle + "-fx-background-color: #3C3C3C;"));
        button.setOnMouseExited(e ->
                button.setStyle(buttonStyle));

        return button;
    }


    /**
     * Sets up a standard structure with a title and buttons in a BorderPane.
     * @param theTitle The title text.
     * @param theButtons The VBox containing the buttons.
     * @param theRoot The root BorderPane.
     */
    public void tripleButtonStructure(final Text theTitle, final VBox theButtons, final BorderPane theRoot) {
        VBox content = new VBox(80, theTitle, theButtons);
        content.setAlignment(Pos.CENTER);
        theRoot.setCenter(content);
    }

    /**
     * Gets the primary stage of the application.
     * @return The primary stage.
     */
    public Stage getStage() {
        return MY_PRIMARY_STAGE;
    }

    /**
     * Gets the main controller of the application.
     * @return The main controller.
     */
    public Controller getController() {
        return MY_CONTROLLER;
    }
}