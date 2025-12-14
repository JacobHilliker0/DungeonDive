package main.View.screen.gamescreen;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import main.Controller.Controller;
import main.Model.character.Hero;
import main.View.GameUI;

import java.io.InputStream;
import java.util.Objects;

/**
 * Manages all UI elements and layout for the game screen.
 * This class is responsible for creating, organizing, and updating all
 * user interface components including panels, stats displays, buttons,
 * messaging system, and visual effects.
 *
 * Main responsibilities:
 * - Creating responsive UI layout with left/center/right panels
 * - Managing player statistics display with icons
 * - Handling character portrait rendering
 * - Creating and updating minimap display
 * - Managing game messages and scrolling
 * - Providing damage flash effects
 * - Coordinating with theme and responsive dimension systems
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
public class GameScreenUIManager {

    /** The main controller for accessing game state */
    private final Controller controller;

    /** Theme provider for consistent styling */
    private final GameScreenTheme theme;

    /** Responsive dimensions manager */
    private GameScreenResponsiveDimensions responsiveDims;

    /** Main game renderer */
    private final GameScreenRenderer renderer;

    /** Minimap renderer */
    private final GameScreenMinimapRenderer minimapRenderer;

    /** Character portrait renderer */
    private final GameScreenPortraitRenderer portraitRenderer;

    // UI Components for player statistics
    private Label playerHealthLabel;
    private Label playerManaLabel;
    private Label playerAttackLabel;
    private Label playerGoldLabel;
    private Label playerPillarsLabel;

    // Message system components
    private VBox messagesArea;
    private ScrollPane messageScrollPane;
    private VBox messageContainer;

    // Visual effects
    private Pane damageFlashOverlay;

    // Canvas components
    private Canvas minimapCanvas;
    private Canvas portraitCanvas;

    /**
     * Constructs a new UI manager with the required dependencies.
     *
     * @param controller The main controller for accessing game state
     * @param renderer The main game renderer
     */
    public GameScreenUIManager(Controller controller, GameScreenRenderer renderer) {
        this.controller = controller;
        this.renderer = renderer;
        this.theme = new GameScreenTheme();
        this.minimapRenderer = new GameScreenMinimapRenderer(controller);
        this.portraitRenderer = new GameScreenPortraitRenderer(controller);
    }

    /**
     * Initializes the UI manager with the provided scene.
     * Sets up responsive dimensions and creates damage flash overlay.
     *
     * @param scene The scene to initialize UI components for
     */
    public void initialize(Scene scene) {
        this.responsiveDims = new GameScreenResponsiveDimensions(scene);
        createDamageFlashOverlay(scene);
    }

    /**
     * Sets up the themed background for the root container.
     * Attempts to load background image with fallback to solid color.
     *
     * @param root The root BorderPane to apply background to
     */
    public void setupThemedBackground(BorderPane root) {
        try (InputStream bgStream = getClass().getResourceAsStream(theme.getBackgroundPath())) {
            if (bgStream != null) {
                root.setStyle(theme.getBackgroundStyle());
            } else {
                root.setStyle(theme.getFallbackBackgroundStyle());
            }
        } catch (Exception e) {
            root.setStyle(theme.getFallbackBackgroundStyle());
        }
    }

    /**
     * Creates the main game area layout with three panels.
     *
     * @param gameUI The GameUI instance for button actions
     * @return HBox containing the complete main game area
     */
    public HBox createMainGameArea(GameUI gameUI) {
        HBox mainGameArea = new HBox();
        mainGameArea.setStyle(theme.getMainGameAreaStyle());
        mainGameArea.setFillHeight(true);
        mainGameArea.setMinWidth(600);

        VBox leftPanel = createLeftPanel();
        VBox centerArea = createCenterArea();
        VBox rightPanel = createRightPanel(gameUI);

        HBox.setHgrow(leftPanel, Priority.NEVER);
        HBox.setHgrow(centerArea, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.NEVER);

        mainGameArea.getChildren().addAll(leftPanel, centerArea, rightPanel);
        return mainGameArea;
    }

    /**
     * Creates the left panel containing player stats and portrait.
     *
     * @return VBox containing the left panel elements
     */
    private VBox createLeftPanel() {
        VBox leftPanel = createThemedPanel();

        VBox statsBox = createPlayerStatsBox();
        VBox portraitSection = createCharacterPortrait();
        Button inventoryButton = createThemedButton("INV");
        inventoryButton.setOnAction(e -> controller.getGameController().openInventory());

        leftPanel.getChildren().addAll(statsBox, portraitSection, inventoryButton);
        return leftPanel;
    }

    /**
     * Creates the player statistics display box with icons and values.
     *
     * @return VBox containing all player stat displays
     */
    private VBox createPlayerStatsBox() {
        VBox statsBox = new VBox();
        statsBox.spacingProperty().bind(responsiveDims.getPanelPaddingBinding().divide(3));
        statsBox.setStyle(theme.getStatsBoxStyle());

        playerHealthLabel = createThemedStatLabel();
        HBox healthBox = createStatBox("/sprites/icons/heart.png", playerHealthLabel);

        playerAttackLabel = createThemedStatLabel();
        HBox attackBox = createStatBox("/sprites/icons/sword.png", playerAttackLabel);

        playerManaLabel = createThemedStatLabel();
        HBox manaBox = createStatBox("/sprites/icons/mana.png", playerManaLabel);

        playerGoldLabel = createThemedStatLabel();
        HBox goldBox = createStatBox("/sprites/icons/coin.png", playerGoldLabel);

        playerPillarsLabel = createThemedStatLabel();
        HBox pillarsBox = createStatBox("/sprites/icons/pillar.png", playerPillarsLabel);

        statsBox.getChildren().addAll(healthBox, manaBox, attackBox, goldBox, pillarsBox);
        return statsBox;
    }

    /**
     * Creates a stat display box with icon and label.
     *
     * @param iconPath Path to the icon image resource
     * @param valueLabel Label to display the stat value
     * @return HBox containing the complete stat display
     */
    private HBox createStatBox(String iconPath, Label valueLabel) {
        Image iconImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
        ImageView iconView = new ImageView(iconImage);

        iconView.fitWidthProperty().bind(responsiveDims.getStatsIconSizeBinding());
        iconView.fitHeightProperty().bind(responsiveDims.getStatsIconSizeBinding());
        iconView.setPreserveRatio(true);

        valueLabel.setWrapText(true);
        valueLabel.setMaxWidth(Double.MAX_VALUE);

        HBox statBox = new HBox(6, iconView, valueLabel);
        statBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(valueLabel, Priority.ALWAYS);

        statBox.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(0, responsiveDims.getPanelPaddingBinding().getValue().doubleValue() / 4, 0, 0),
                responsiveDims.getPanelPaddingBinding()));

        return statBox;
    }

    /**
     * Creates a themed label for stat values.
     *
     * @return Configured Label for displaying stat values
     */
    private Label createThemedStatLabel() {
        Label label = new Label();
        label.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(theme.getFontPath(), responsiveDims.getStatsFontSizeBinding().getValue().intValue(), "Monospaced"),
                responsiveDims.getStatsFontSizeBinding()));
        label.setTextFill(theme.getTanColor());
        label.setStyle(theme.getShadowStyle());
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER_LEFT);
        return label;
    }

    /**
     * Creates the character portrait section.
     *
     * @return VBox containing the character portrait canvas
     */
    private VBox createCharacterPortrait() {
        VBox portraitSection = new VBox();
        portraitSection.setAlignment(Pos.CENTER);

        portraitCanvas = new Canvas();
        portraitCanvas.widthProperty().bind(responsiveDims.getPortraitSizeBinding());
        portraitCanvas.heightProperty().bind(responsiveDims.getPortraitSizeBinding());

        portraitRenderer.initialize(portraitCanvas);

        StackPane portraitContainer = new StackPane(portraitCanvas);
        portraitContainer.setStyle(
                "-fx-border-color: " + theme.getPanelBorderColor() + "; " +
                        "-fx-border-width: 2px; " +
                        "-fx-background-color: black;"
        );

        portraitSection.getChildren().add(portraitContainer);
        updateCharacterPortrait();
        return portraitSection;
    }

    /**
     * Creates the center area containing the title and game canvas.
     *
     * @return VBox containing the center area elements
     */
    private VBox createCenterArea() {
        VBox centerArea = new VBox();
        centerArea.setAlignment(Pos.CENTER);
        centerArea.setStyle(theme.getCenterAreaStyle());

        VBox titleBox = createThemedTitleBox();
        StackPane canvasContainer = createCanvasContainer();

        centerArea.getChildren().addAll(titleBox, canvasContainer);
        VBox.setVgrow(canvasContainer, Priority.ALWAYS);
        return centerArea;
    }

    /**
     * Creates a container for the game canvas with styling.
     *
     * @return StackPane containing the styled canvas container
     */
    private StackPane createCanvasContainer() {
        Canvas roomCanvas = renderer.getRoomCanvas();
        StackPane canvasContainer = new StackPane(roomCanvas);
        canvasContainer.setAlignment(Pos.CENTER);
        canvasContainer.setStyle(
                "-fx-background-color: rgba(0,0,0,0.7); " +
                        "-fx-border-color: #DAA520; " +
                        "-fx-border-width: 3px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px;" +
                        theme.getShadowStyle()
        );
        canvasContainer.setPadding(new Insets(10));
        return canvasContainer;
    }

    /**
     * Creates the themed title box with game title.
     *
     * @return VBox containing the styled title elements
     */
    private VBox createThemedTitleBox() {
        VBox titleBox = new VBox(10);
        titleBox.spacingProperty().bind(responsiveDims.getPanelPaddingBinding().divide(2));
        titleBox.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(responsiveDims.getPanelPaddingBinding().getValue().doubleValue()),
                responsiveDims.getPanelPaddingBinding()));
        titleBox.setAlignment(Pos.CENTER);

        Text gameTitle = new Text("DUNGEON");
        gameTitle.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(theme.getFontPath(), responsiveDims.getTitleFontSizeBinding().getValue().intValue(), "Impact"),
                responsiveDims.getTitleFontSizeBinding()));
        gameTitle.setFill(theme.getOrangeColor());
        gameTitle.setStyle(theme.getShadowStyle());

        Text subtitle = new Text("DIVE");
        subtitle.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(theme.getFontPath(), (int)(responsiveDims.getTitleFontSizeBinding().getValue().doubleValue() * 0.6), "Impact"),
                responsiveDims.getTitleFontSizeBinding()));
        subtitle.setFill(theme.getOrangeColor());
        subtitle.setStyle(theme.getShadowStyle());

        titleBox.getChildren().addAll(gameTitle, subtitle);
        return titleBox;
    }

    /**
     * Creates the right panel containing minimap and buttons.
     *
     * @param gameUI The GameUI instance for button actions
     * @return VBox containing the right panel elements
     */
    private VBox createRightPanel(GameUI gameUI) {
        VBox rightPanel = createThemedPanel();

        minimapCanvas = new Canvas();
        minimapCanvas.widthProperty().bind(responsiveDims.getMinimapSizeBinding());
        minimapCanvas.heightProperty().bind(responsiveDims.getMinimapSizeBinding());
        minimapRenderer.initialize(minimapCanvas);

        Button helpButton = createThemedButton("HELP");
        Button pauseButton = createThemedButton("PAUSE");

        helpButton.setOnAction(event -> controller.helpMenu(event, gameUI));
        pauseButton.setOnAction(event -> controller.pauseGame(event, gameUI));

        rightPanel.getChildren().addAll(minimapCanvas, helpButton, pauseButton);
        return rightPanel;
    }

    /**
     * Creates a themed panel with consistent styling and responsive properties.
     *
     * @return VBox configured as a themed panel
     */
    private VBox createThemedPanel() {
        VBox panel = new VBox();
        panel.setAlignment(Pos.TOP_CENTER);
        panel.spacingProperty().bind(responsiveDims.getPanelPaddingBinding());
        panel.prefWidthProperty().bind(responsiveDims.getPanelWidthBinding());
        panel.maxWidthProperty().bind(responsiveDims.getPanelWidthBinding().multiply(1.2));
        panel.minWidthProperty().bind(responsiveDims.getPanelWidthBinding().multiply(0.8));
        panel.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(responsiveDims.getPanelPaddingBinding().getValue().doubleValue()),
                responsiveDims.getPanelPaddingBinding()));
        panel.setStyle(theme.getPanelStyle());
        return panel;
    }

    /**
     * Creates a themed button with consistent styling and responsive properties.
     *
     * @param text The text to display on the button
     * @return Button configured with theme styling
     */
    private Button createThemedButton(String text) {
        Button button = new Button(text);
        button.setTextFill(Color.WHITE);
        button.setTextOverrun(OverrunStyle.CLIP);

        button.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(theme.getFontPath(),
                                responsiveDims.getButtonFontSizeBinding().getValue().intValue(),
                                "Courier New"),
                responsiveDims.getButtonFontSizeBinding()));

        button.prefWidthProperty().bind(responsiveDims.getPanelWidthBinding().multiply(0.95));
        button.prefHeightProperty().bind(responsiveDims.getButtonHeightBinding());
        button.setWrapText(true);
        button.setAlignment(Pos.CENTER);

        String baseStyle = theme.getButtonBaseStyle();
        button.setStyle(baseStyle);

        button.setOnMouseEntered(e -> button.setStyle(theme.getButtonHoverStyle()));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    /**
     * Creates the message area components for displaying game messages.
     */
    public void createMessageArea() {
        messageContainer = createThemedMessageContainer();
        messagesArea = createMessagesArea();
        messageScrollPane = createMessageScrollPane();
        messageContainer.getChildren().add(messageScrollPane);
    }

    /**
     * Creates the themed container for messages.
     *
     * @return VBox configured for message display
     */
    private VBox createThemedMessageContainer() {
        VBox container = new VBox();
        container.setStyle(theme.getMessageContainerStyle());
        container.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(responsiveDims.getPanelPaddingBinding().getValue().doubleValue()),
                responsiveDims.getPanelPaddingBinding()));
        return container;
    }

    /**
     * Creates the messages area for holding individual message labels.
     *
     * @return VBox configured for message content
     */
    private VBox createMessagesArea() {
        VBox area = new VBox();
        area.spacingProperty().bind(responsiveDims.getPanelPaddingBinding().divide(2));
        area.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(responsiveDims.getPanelPaddingBinding().getValue().doubleValue()),
                responsiveDims.getPanelPaddingBinding()));
        area.setAlignment(Pos.TOP_LEFT);
        return area;
    }

    /**
     * Creates the scroll pane for messages.
     *
     * @return ScrollPane configured for message scrolling
     */
    private ScrollPane createMessageScrollPane() {
        ScrollPane scrollPane = new ScrollPane(messagesArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle(theme.getMessageScrollPaneStyle());

        scrollPane.prefHeightProperty().bind(
                Bindings.max(50.0,
                        Bindings.min(100.0,
                                responsiveDims.getScene().heightProperty().multiply(0.08)))
        );
        return scrollPane;
    }

    /**
     * Creates the damage flash overlay for visual feedback.
     *
     * @param scene The scene to bind overlay dimensions to
     */
    private void createDamageFlashOverlay(Scene scene) {
        damageFlashOverlay = new Pane();
        damageFlashOverlay.setStyle("-fx-background-color: rgba(255, 0, 0, 0.4);");
        damageFlashOverlay.setVisible(false);
        damageFlashOverlay.setMouseTransparent(true);

        damageFlashOverlay.prefWidthProperty().bind(scene.widthProperty());
        damageFlashOverlay.prefHeightProperty().bind(scene.heightProperty());
    }

    /**
     * Sets up responsive bindings for UI elements.
     * This method can be used to establish additional responsive behaviors.
     */
    public void setupResponsiveBindings() {
        // Additional responsive bindings can be added here if needed
    }

    /**
     * Adds a new message to the game message display.
     *
     * @param message The message text to display
     */
    public void addGameMessage(String message) {
        if (messagesArea == null || messageScrollPane == null) return;

        Label messageLabel = createThemedMessageLabel(message);
        messagesArea.getChildren().add(messageLabel);

        if (messagesArea.getChildren().size() > 20) { // MAX_MESSAGES
            messagesArea.getChildren().remove(0);
        }
        javafx.application.Platform.runLater(() -> messageScrollPane.setVvalue(1.0));
    }

    /**
     * Creates a themed label for displaying messages.
     *
     * @param message The message text to display
     * @return Label configured with message styling
     */
    private Label createThemedMessageLabel(String message) {
        Label label = new Label("• " + message);
        label.fontProperty().bind(Bindings.createObjectBinding(() ->
                        loadFont(theme.getFontPath(), responsiveDims.getMessageFontSizeBinding().getValue().intValue(), "Monospaced"),
                responsiveDims.getMessageFontSizeBinding()));
        label.paddingProperty().bind(Bindings.createObjectBinding(() ->
                        new Insets(responsiveDims.getPanelPaddingBinding().getValue().doubleValue() / 4, 0,
                                responsiveDims.getPanelPaddingBinding().getValue().doubleValue() / 4, 0),
                responsiveDims.getPanelPaddingBinding()));
        label.setTextFill(theme.getTanColor());
        label.setWrapText(true);
        label.setStyle(theme.getShadowStyle());
        return label;
    }

    /**
     * Updates the player statistics display with current values.
     */
    public void updatePlayerStats() {
        Hero player = controller.getPlayer();
        if (player != null) {
            javafx.application.Platform.runLater(() -> {
                playerHealthLabel.setText("HP: " + player.getHealth() + "/" + player.getMaxHealth());
                playerManaLabel.setText("MP: " + player.getSpecialMana() + "/4");
                playerAttackLabel.setText("ATK: " + player.getType().getBaseAttack());
                playerGoldLabel.setText("Gold: " + player.getGold());
                playerPillarsLabel.setText("Pillars: " + player.getPillarsActivated() + "/4");
            });
        }
    }

    /**
     * Updates the minimap display.
     */
    public void updateMinimap() {
        if (minimapRenderer != null) {
            minimapRenderer.updateMinimap();
        }
    }

    /**
     * Updates the character portrait display.
     */
    public void updateCharacterPortrait() {
        if (portraitRenderer != null) {
            portraitRenderer.updatePortraitCanvas();
        }
    }

    /**
     * Displays a damage flash effect across the screen.
     * Creates a red flash animation to indicate the player has taken damage.
     */
    public void flashDamageEffect() {
        if (damageFlashOverlay == null) {
            System.err.println("Warning: damageFlashOverlay is null, cannot show damage flash");
            return;
        }

        damageFlashOverlay.setVisible(true);
        damageFlashOverlay.setOpacity(0.0);

        javafx.animation.Timeline flashTimeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.ZERO,
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.0)),
                new javafx.animation.KeyFrame(Duration.millis(50),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.8)),
                new javafx.animation.KeyFrame(Duration.millis(150),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.2)),
                new javafx.animation.KeyFrame(Duration.millis(250),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.6)),
                new javafx.animation.KeyFrame(Duration.millis(350),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.1)),
                new javafx.animation.KeyFrame(Duration.millis(450),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.4)),
                new javafx.animation.KeyFrame(Duration.millis(600),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.2)),
                new javafx.animation.KeyFrame(Duration.millis(1200),
                        new javafx.animation.KeyValue(damageFlashOverlay.opacityProperty(), 0.0))
        );

        flashTimeline.setOnFinished(event -> damageFlashOverlay.setVisible(false));
        flashTimeline.play();
    }

    /**
     * Handles room change events by updating relevant UI components.
     */
    public void onRoomChanged() {
        updateMinimap();
        updatePlayerStats();
        if (controller.getGameController() != null) {
            addGameMessage(controller.getGameController().getCurrentRoomDescription());
        }
    }

    /**
     * Loads a font from resources with fallback support.
     *
     * @param path Path to the font resource
     * @param size Desired font size
     * @param fallback Fallback font family name
     * @return Font object with specified size
     */
    private Font loadFont(String path, int size, String fallback) {
        try (InputStream fontStream = getClass().getResourceAsStream(path)) {
            if (fontStream != null) {
                Font customFont = Font.loadFont(fontStream, size);
                if (customFont != null) return customFont;
            }
        } catch (Exception e) {
            System.err.println("Failed to load custom font " + path + ". Using fallback. Error: " + e.getMessage());
        }
        return Font.font(fallback, size);
    }

    // Getter methods for accessing UI components

    /**
     * Gets the message container for external layout management.
     *
     * @return The message container VBox
     */
    public VBox getMessageContainer() {
        return messageContainer;
    }

    /**
     * Gets the damage flash overlay for external layout management.
     *
     * @return The damage flash overlay Pane
     */
    public Pane getDamageFlashOverlay() {
        return damageFlashOverlay;
    }
}