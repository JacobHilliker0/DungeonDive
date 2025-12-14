package main.View.screen;

import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Controller.Controller;
import main.View.GameUI;
import main.Model.util.HeroType;

import java.io.InputStream;

/**
 * The HeroSelectionScreen class provides an interactive interface for players to
 * select their character type and enter their hero's name before starting the game.
 * This screen features sprite-based hero selection with visual feedback, responsive
 * scaling, and comprehensive form validation.
 *
 * <p>Key features include:</p>
 * <ul>
 *   <li>Visual hero type selection with sprite previews (Warrior, Priestess, Thief)</li>
 *   <li>Hero name input with real-time validation</li>
 *   <li>Interactive hover and selection effects</li>
 *   <li>Dynamic hero descriptions based on selection</li>
 *   <li>Responsive design that adapts to different screen sizes</li>
 *   <li>Consistent theming with other game screens</li>
 * </ul>
 *
 * <p>The screen uses a brick wall background and custom PixelFont for thematic
 * consistency with the dungeon adventure theme. Hero sprites are loaded dynamically
 * with fallback colors if images are unavailable.</p>
 *
 * <p>Form validation ensures both a hero name and hero type are selected before
 * allowing game progression. Visual feedback guides the user through the selection
 * process with appropriate error states and success indicators.</p>
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/10/2025
 * @see Screen
 * @see HeroType
 * @see GameUI
 * @see Controller
 */
public class HeroSelectionScreen extends Screen {

    // ====== SCALING CONSTANTS ======
    /** Minimum scale factor for responsive UI scaling */
    private static final double MIN_SCALE = 0.5;

    /** Maximum scale factor for responsive UI scaling */
    private static final double MAX_SCALE = 2.0;

    // ====== FONT SIZE CONSTANTS ======
    /** Base font size for the main title text */
    private static final int BASE_TITLE_FONT_SIZE = 45;

    /** Base font size for section labels and descriptions */
    private static final int BASE_LABEL_FONT_SIZE = 18;

    /** Base font size for hero name labels under sprites */
    private static final int BASE_HERO_NAME_FONT_SIZE = 16;

    /** Base font size for action buttons */
    private static final int BASE_BUTTON_FONT_SIZE = 22;

    /** Base font size for text input fields */
    private static final int BASE_TEXT_FIELD_FONT_SIZE = 14;

    // ====== UI DIMENSION CONSTANTS ======
    /** Base width for text input fields */
    private static final int BASE_TEXT_FIELD_WIDTH = 400;

    /** Base height for text input fields */
    private static final int BASE_TEXT_FIELD_HEIGHT = 30;

    /** Base width for action buttons */
    private static final int BASE_BUTTON_WIDTH = 280;

    /** Base height for action buttons */
    private static final int BASE_BUTTON_HEIGHT = 45;

    /** Base size for hero sprite images */
    private static final int BASE_SPRITE_SIZE = 64;

    /** Base width for description text area */
    private static final int BASE_DESCRIPTION_WIDTH = 700;

    /** Base height for description text area */
    private static final int BASE_DESCRIPTION_HEIGHT = 60;

    // ====== SPACING CONSTANTS ======
    /** Vertical spacing between major content sections */
    private static final int BASE_CONTENT_SPACING = 20;

    /** Top padding for the main content area */
    private static final int BASE_TOP_PADDING = 30;

    /** Horizontal spacing between hero sprite options */
    private static final int BASE_HERO_SPACING = 100;

    /** Vertical spacing within the title section */
    private static final int BASE_TITLE_SPACING = 10;

    /** Vertical spacing within the name input section */
    private static final int BASE_NAME_SECTION_SPACING = 10;

    /** Vertical spacing within hero selection sections */
    private static final int BASE_HERO_SECTION_SPACING = 15;

    /** Vertical spacing within individual hero sprite boxes */
    private static final int BASE_HERO_BOX_SPACING = 5;

    // ====== VISUAL EFFECT CONSTANTS ======
    /** Glow intensity level for sprite hover and selection effects */
    private static final double GLOW_LEVEL = 0.8;

    /** Shadow radius for normal sprite hover effects */
    private static final int SHADOW_RADIUS = 15;

    /** Shadow radius for selected sprite emphasis */
    private static final int SELECTED_SHADOW_RADIUS = 20;

    /** Duration of button flash animation in milliseconds */
    private static final int FLASH_DURATION_MS = 150;

    // ====== COLOR CONSTANTS ======
    /** Primary tan color used for text and UI elements */
    private static final Color TAN_COLOR = Color.rgb(222, 184, 135);

    /** Hover glow color for interactive elements */
    private static final Color HOVER_GLOW_COLOR = Color.GOLD;

    /** Selection glow color for chosen elements */
    private static final Color SELECTION_GLOW_COLOR = Color.ORANGE;

    // ====== RESOURCE PATH CONSTANTS ======
    /** Path to the warrior character sprite image */
    private static final String WARRIOR_SPRITE_PATH = "/sprites/heroes/warrior.png";

    /** Path to the priestess character sprite image */
    private static final String PRIESTESS_SPRITE_PATH = "/sprites/heroes/priestess.png";

    /** Path to the thief character sprite image */
    private static final String THIEF_SPRITE_PATH = "/sprites/heroes/thief.png";

    /** Path to the custom pixel font used throughout the application */
    private static final String FONT_PATH = "/main/View/fonts/PixelFont.ttf";

    /** Path to the brick wall background image */
    private static final String BACKGROUND_PATH = "/sprites/backgrounds/brick_wall_background.png";

    // ====== STYLE CONSTANTS ======
    /** CSS style string for drop shadow effects */
    private static final String SHADOW_STYLE = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";

    /** Base CSS style for text input fields */
    private static final String TEXT_FIELD_BASE_STYLE =
            "-fx-background-color: #2C2C2C; -fx-text-fill: #E0E0E0; -fx-border-color: #DAA520; -fx-border-width: 3px; -fx-prompt-text-fill: #888888;";

    /** CSS style for disabled/invalid button flash effect */
    private static final String FLASH_BUTTON_STYLE =
            "-fx-background-color: linear-gradient(to bottom, #2A2A2A, #000000); -fx-text-fill: #888888; -fx-border-color: #444444; -fx-border-width: 4px; -fx-padding: 15px 40px; -fx-background-radius: 0; -fx-border-radius: 0;";

    // ====== INSTANCE VARIABLES ======
    /** Currently selected hero type (null if none selected) */
    private HeroType selectedHeroType;

    /** ImageView of the currently selected hero sprite */
    private ImageView selectedHeroView;

    /** Label displaying hero description text */
    private Label descriptionLabel;

    /** Button to start the game (enabled when form is valid) */
    private Button startGameBtn;

    /** Text field for hero name input */
    private TextField nameBox;

    /** The scene containing this screen's UI */
    private Scene scene;

    /**
     * Constructs a new HeroSelectionScreen instance.
     * Initializes the screen with the provided stage and controller references.
     *
     * @param thePrimaryStage The main application stage that will host this screen.
     *                       Must not be null.
     * @param theController   The main controller instance for handling user interactions,
     *                       game state management, and hero creation. Must not be null.
     * @throws IllegalArgumentException if either parameter is null (inherited from Screen)
     */
    public HeroSelectionScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    /**
     * Displays the hero selection screen with all interactive elements.
     * This method creates and configures the complete UI layout including:
     * - Title section with game branding
     * - Hero name input field with validation
     * - Visual hero type selection with sprites
     * - Dynamic description area
     * - Start game button with form validation
     *
     * <p>The screen is designed to be responsive and will scale appropriately
     * to different window sizes while maintaining visual appeal and usability.
     * Form validation ensures the user provides both a name and selects a hero
     * type before proceeding.</p>
     *
     * @param theUI The GameUI instance used for screen transitions and
     *              coordinating with other parts of the application.
     *              Must not be null.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        VBox root = createRootLayout();
        Stage currentStage = getStage();

        double width = currentStage.getScene() != null ?
                currentStage.getScene().getWidth() : BASE_WIDTH;
        double height = currentStage.getScene() != null ?
                currentStage.getScene().getHeight() : BASE_HEIGHT;

        scene = new Scene(root, width, height);

        setupBackground(root);
        setupComponents(root, theUI);

        // Use parent's scaling method instead of custom implementation
        applyScaling(root, scene);

        setupResponsiveBindings();

        boolean wasFullScreen = currentStage.isFullScreen();

        currentStage.setScene(scene);
        currentStage.setTitle("Select Your Hero - Dungeon Dive");

        if (wasFullScreen && !currentStage.isFullScreen()) {
            currentStage.setFullScreen(true);
        }

        if (!currentStage.isShowing()) {
            currentStage.show();
        }
    }

    /**
     * Creates the root layout container for the hero selection screen.
     * Establishes the main vertical layout with appropriate spacing and padding.
     *
     * @return A VBox configured as the root container with proper alignment,
     *         spacing, and padding for the hero selection interface.
     */
    private VBox createRootLayout() {
        VBox root = new VBox(BASE_CONTENT_SPACING);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(BASE_TOP_PADDING));
        return root;
    }

    /**
     * Sets up the background styling for the hero selection screen.
     * Attempts to load the brick wall background image from resources.
     * Falls back to a solid color background if the image cannot be loaded.
     *
     * @param root The root VBox that will receive the background styling.
     *             Must not be null.
     */
    private void setupBackground(VBox root) {
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
     * Builds the CSS background style string for the brick wall image.
     *
     * @return CSS style string for background image with cover sizing and centering.
     */
    private String buildBackgroundStyle() {
        return "-fx-background-image: url('" + BACKGROUND_PATH + "'); " +
                "-fx-background-repeat: no-repeat; " +
                "-fx-background-size: cover; " +
                "-fx-background-position: center center;";
    }

    /**
     * Sets up responsive bindings for UI elements that need to adapt to screen size changes.
     * Configures maximum widths and text wrapping for dynamic content areas.
     */
    private void setupResponsiveBindings() {
        if (nameBox != null) {
            nameBox.setMaxWidth(BASE_TEXT_FIELD_WIDTH);
        }
        if (descriptionLabel != null) {
            descriptionLabel.setMaxWidth(BASE_DESCRIPTION_WIDTH);
            descriptionLabel.setWrapText(true);
        }
    }

    /**
     * Sets up all UI components and adds them to the root container.
     * Creates and configures the complete interface including title, name input,
     * hero selection, description area, and action buttons.
     *
     * @param root  The root VBox container to add components to. Must not be null.
     * @param theUI The GameUI instance needed for button event handlers. Must not be null.
     */
    private void setupComponents(VBox root, GameUI theUI) {
        FontBundle fonts = loadFonts();

        root.getChildren().addAll(
                createTitle(fonts.title),
                createNameSection(fonts.label, fonts.textField),
                createHeroSpriteSection(fonts.label),
                createDescriptionLabel(fonts.label),
                createStartButton(fonts.button, theUI)
        );

        validateForm();
    }

    /**
     * Inner class to hold loaded font instances for consistent styling.
     * Groups related fonts together for easy management and parameter passing.
     */
    private static class FontBundle {
        /** Font for main title text */
        final Font title;

        /** Font for labels and general text */
        final Font label;

        /** Font for buttons */
        final Font button;

        /** Font for text input fields */
        final Font textField;

        /**
         * Creates a new FontBundle with the specified font instances.
         *
         * @param title     Font for title text
         * @param label     Font for labels
         * @param button    Font for buttons
         * @param textField Font for text fields
         */
        FontBundle(Font title, Font label, Font button, Font textField) {
            this.title = title;
            this.label = label;
            this.button = button;
            this.textField = textField;
        }
    }

    /**
     * Loads all required fonts for the hero selection screen.
     * Uses the parent class's font loading method with appropriate fallbacks.
     *
     * @return A FontBundle containing all loaded fonts with appropriate sizes.
     */
    private FontBundle loadFonts() {
        return new FontBundle(
                // Use parent's loadFont method instead of custom implementation
                loadFont(FONT_PATH, BASE_TITLE_FONT_SIZE, "Impact"),
                loadFont(FONT_PATH, BASE_LABEL_FONT_SIZE, "Arial"),
                loadFont(FONT_PATH, BASE_BUTTON_FONT_SIZE, "Courier New"),
                loadFont(FONT_PATH, BASE_TEXT_FIELD_FONT_SIZE, "Courier New")
        );
    }

    /**
     * Creates the title section with "DUNGEON DIVE" branding.
     * Uses styled text elements with the custom font and shadow effects.
     *
     * @param titleFont The font to use for title text. Must not be null.
     * @return A VBox containing the styled title elements with proper alignment.
     */
    private VBox createTitle(Font titleFont) {
        VBox titleBox = new VBox(BASE_TITLE_SPACING);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.getChildren().addAll(
                createStyledText("DUNGEON", titleFont),
                createStyledText("DIVE", titleFont)
        );
        return titleBox;
    }

    /**
     * Creates a styled text element with consistent formatting.
     * Applies the game's signature orange color and drop shadow effect.
     *
     * @param text The text content to display. Must not be null.
     * @param font The font to apply to the text. Must not be null.
     * @return A Text element with the specified content and styling applied.
     */
    private Text createStyledText(String text, Font font) {
        Text textElement = new Text(text);
        textElement.setFont(font);
        textElement.setFill(Color.ORANGE);
        textElement.setStyle(SHADOW_STYLE);
        return textElement;
    }

    /**
     * Creates the name input section with label and text field.
     * Includes event handlers for real-time validation and user interaction.
     *
     * @param labelFont     Font for the section label. Must not be null.
     * @param textFieldFont Font for the text input field. Must not be null.
     * @return A VBox containing the name input label and text field with
     *         appropriate styling and event handlers configured.
     */
    private VBox createNameSection(Font labelFont, Font textFieldFont) {
        VBox nameSection = new VBox(BASE_NAME_SECTION_SPACING);
        nameSection.setAlignment(Pos.CENTER);

        Label nameLabel = createStyledLabel("ENTER YOUR HERO'S NAME:", labelFont, TAN_COLOR);
        nameBox = createNameTextField(textFieldFont);
        setupNameFieldEventHandlers();

        nameSection.getChildren().addAll(nameLabel, nameBox);
        return nameSection;
    }

    /**
     * Creates the name input text field with appropriate styling and dimensions.
     *
     * @param textFieldFont Font to apply to the text field. Must not be null.
     * @return A TextField configured for hero name input with proper styling,
     *         dimensions, and placeholder text.
     */
    private TextField createNameTextField(Font textFieldFont) {
        TextField textField = new TextField();
        textField.setPromptText("Hero Name");
        textField.setFont(textFieldFont);
        textField.setStyle(TEXT_FIELD_BASE_STYLE);
        textField.setPrefWidth(BASE_TEXT_FIELD_WIDTH);
        textField.setPrefHeight(BASE_TEXT_FIELD_HEIGHT);
        return textField;
    }

    /**
     * Sets up event handlers for the name input field.
     * Handles text changes, Enter key submission, and focus loss events
     * to provide real-time validation and smooth user interaction.
     */
    private void setupNameFieldEventHandlers() {
        nameBox.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        nameBox.setOnAction(event -> {
            updateControllerWithName();
            validateForm();
            if (isFormValid()) {
                startGameBtn.fire();
            }
        });
        nameBox.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                updateControllerWithName();
                validateForm();
            }
        });
    }

    /**
     * Checks if the form is valid for game start.
     * Validates that both a hero name and hero type have been selected.
     *
     * @return true if both name and hero type are selected, false otherwise.
     */
    private boolean isFormValid() {
        return !nameBox.getText().trim().isEmpty() && selectedHeroType != null;
    }

    /**
     * Updates the controller with the current hero name from the text field.
     * This ensures the controller has the latest name information for game creation.
     */
    private void updateControllerWithName() {
        if (getController() != null) {
            getController().setHeroName(nameBox.getText());
        }
    }

    /**
     * Creates the hero sprite selection section with visual hero options.
     * Includes a section label and horizontal layout of hero sprite choices.
     *
     * @param labelFont Font for the section label. Must not be null.
     * @return A VBox containing the hero selection label and sprite options
     *         with proper layout and interactive functionality.
     */
    private VBox createHeroSpriteSection(Font labelFont) {
        VBox heroSection = new VBox(BASE_HERO_SECTION_SPACING);
        heroSection.setAlignment(Pos.CENTER);

        Label heroSelectionLabel = createStyledLabel("SELECT YOUR HERO:", labelFont, TAN_COLOR);
        HBox heroSpritesBox = createHeroSpritesBox(labelFont);

        heroSection.getChildren().addAll(heroSelectionLabel, heroSpritesBox);
        return heroSection;
    }

    /**
     * Creates the horizontal container for hero sprite options.
     * Builds individual hero selection boxes for each available hero type.
     *
     * @param labelFont Font for hero name labels. Must not be null.
     * @return An HBox containing interactive hero selection options with
     *         proper spacing and alignment.
     */
    private HBox createHeroSpritesBox(Font labelFont) {
        HBox heroSpritesBox = new HBox(BASE_HERO_SPACING);
        heroSpritesBox.setAlignment(Pos.CENTER);

        heroSpritesBox.getChildren().addAll(
                createHeroBox("WARRIOR", WARRIOR_SPRITE_PATH, HeroType.WARRIOR, labelFont),
                createHeroBox("PRIESTESS", PRIESTESS_SPRITE_PATH, HeroType.PRIESTESS, labelFont),
                createHeroBox("THIEF", THIEF_SPRITE_PATH, HeroType.THIEF, labelFont)
        );

        return heroSpritesBox;
    }

    /**
     * Creates an individual hero selection box with sprite and name label.
     * Each box includes the hero sprite image, name label, and interactive functionality.
     *
     * @param heroName   Display name for the hero type. Must not be null.
     * @param spritePath Path to the hero sprite image. Must not be null.
     * @param heroType   The HeroType enum value this box represents. Must not be null.
     * @param labelFont  Font for the hero name label. Must not be null.
     * @return A VBox containing the hero sprite and name with interactive
     *         selection functionality configured.
     */
    private VBox createHeroBox(String heroName, String spritePath, HeroType heroType, Font labelFont) {
        VBox heroBox = new VBox(BASE_HERO_BOX_SPACING);
        heroBox.setAlignment(Pos.CENTER);

        ImageView heroSprite = createHeroSprite(spritePath, heroType);
        Font heroNameFont = loadFont(FONT_PATH, BASE_HERO_NAME_FONT_SIZE, labelFont.getFamily());
        Label nameLabel = createStyledLabel(heroName, heroNameFont, TAN_COLOR);

        heroBox.getChildren().addAll(heroSprite, nameLabel);
        return heroBox;
    }

    /**
     * Creates an interactive hero sprite ImageView with visual effects.
     * Loads the sprite image and sets up hover, selection, and click functionality.
     *
     * @param spritePath Path to the sprite image file. Must not be null.
     * @param heroType   The hero type this sprite represents. Must not be null.
     * @return An ImageView configured with the sprite image, proper sizing,
     *         and interactive event handlers for selection functionality.
     */
    private ImageView createHeroSprite(String spritePath, HeroType heroType) {
        ImageView heroSprite = new ImageView();
        setupSpriteSize(heroSprite);
        loadSpriteImage(heroSprite, spritePath, heroType);
        setupSpriteEventHandlers(heroSprite, heroType);
        return heroSprite;
    }

    /**
     * Configures the size and scaling properties for a hero sprite.
     * Sets the sprite to maintain aspect ratio and use the base sprite size.
     *
     * @param sprite The ImageView to configure. Must not be null.
     */
    private void setupSpriteSize(ImageView sprite) {
        sprite.setPreserveRatio(true);
        sprite.setFitWidth(BASE_SPRITE_SIZE);
        sprite.setFitHeight(BASE_SPRITE_SIZE);
    }

    /**
     * Loads the sprite image from the specified path with fallback handling.
     * If the image cannot be loaded, creates a colored fallback representation.
     *
     * @param sprite     The ImageView to load the image into. Must not be null.
     * @param spritePath Path to the image file. Must not be null.
     * @param heroType   Hero type for fallback color selection. Must not be null.
     */
    private void loadSpriteImage(ImageView sprite, String spritePath, HeroType heroType) {
        try (InputStream spriteStream = getResourceStream(spritePath)) {
            if (spriteStream != null) {
                sprite.setImage(new Image(spriteStream));
            } else {
                createFallbackSprite(sprite, heroType);
                logError("Sprite image not found: " + spritePath);
            }
        } catch (Exception e) {
            createFallbackSprite(sprite, heroType);
            logError("Error loading sprite: " + spritePath + " - " + e.getMessage());
        }
    }

    /**
     * Creates a fallback visual representation when sprite image loading fails.
     * Uses a colored rectangle with border styling to represent the hero type.
     *
     * @param sprite   The ImageView to apply fallback styling to. Must not be null.
     * @param heroType Hero type for determining fallback color. Must not be null.
     */
    private void createFallbackSprite(ImageView sprite, HeroType heroType) {
        String color = getHeroFallbackColor(heroType);
        sprite.setImage(null);
        sprite.setStyle("-fx-background-color: " + color + "; -fx-border-color: #DAA520; -fx-border-width: 2px;");
        sprite.setFitWidth(BASE_SPRITE_SIZE);
        sprite.setFitHeight(BASE_SPRITE_SIZE);
    }

    /**
     * Sets up interactive event handlers for hero sprite selection.
     * Configures mouse hover effects, click handling, and visual feedback.
     *
     * @param sprite   The ImageView to add event handlers to. Must not be null.
     * @param heroType The hero type this sprite represents. Must not be null.
     */
    private void setupSpriteEventHandlers(ImageView sprite, HeroType heroType) {
        sprite.setOnMouseEntered(e -> applySpriteHoverEffect(sprite));
        sprite.setOnMouseExited(e -> removeSpriteHoverEffect(sprite));
        sprite.setOnMouseClicked(e -> handleSpriteSelection(sprite, heroType));
    }

    /**
     * Applies hover visual effects to a hero sprite.
     * Scales up the sprite and adds a golden glow effect.
     *
     * @param sprite The ImageView to apply hover effects to. Must not be null.
     */
    private void applySpriteHoverEffect(ImageView sprite) {
        sprite.setScaleX(1.25);
        sprite.setScaleY(1.25);
        sprite.setEffect(createGlowEffect(HOVER_GLOW_COLOR, SHADOW_RADIUS));
    }

    /**
     * Removes hover effects from a hero sprite.
     * Restores either the selected state or normal state depending on selection status.
     *
     * @param sprite The ImageView to remove hover effects from. Must not be null.
     */
    private void removeSpriteHoverEffect(ImageView sprite) {
        if (sprite == selectedHeroView) {
            applySelectedState(sprite);
        } else {
            applyNormalState(sprite);
        }
    }

    /**
     * Applies visual styling for a selected hero sprite.
     * Resets scale and applies orange selection glow effect.
     *
     * @param heroSprite The ImageView to apply selection styling to. Must not be null.
     */
    private void applySelectedState(ImageView heroSprite) {
        heroSprite.setScaleX(1.0);
        heroSprite.setScaleY(1.0);
        heroSprite.setEffect(createGlowEffect(SELECTION_GLOW_COLOR, SELECTED_SHADOW_RADIUS));
    }

    /**
     * Applies normal (unselected, unhovered) visual styling to a hero sprite.
     * Resets scale and removes all visual effects.
     *
     * @param heroSprite The ImageView to apply normal styling to. Must not be null.
     */
    private void applyNormalState(ImageView heroSprite) {
        heroSprite.setScaleX(1.0);
        heroSprite.setScaleY(1.0);
        heroSprite.setEffect(null);
    }

    /**
     * Resets the visual state of the previously selected hero sprite.
     * Called when a new hero is selected to clear the old selection.
     */
    private void resetPreviousSelection() {
        if (selectedHeroView != null) {
            applyNormalState(selectedHeroView);
        }
    }

    /**
     * Handles hero sprite selection when clicked.
     * Updates the selected hero, applies visual feedback, and triggers validation.
     *
     * @param sprite   The ImageView that was clicked. Must not be null.
     * @param heroType The hero type represented by the clicked sprite. Must not be null.
     */
    private void handleSpriteSelection(ImageView sprite, HeroType heroType) {
        if (selectedHeroView != sprite) {
            resetPreviousSelection();
            selectedHeroView = sprite;
            selectedHeroType = heroType;
        }
        applySelectedState(sprite);
        updateHeroDescription(heroType);
        validateForm();
    }

    /**
     * Creates a glow visual effect with the specified color and radius.
     * Combines glow and drop shadow effects for enhanced visual appeal.
     *
     * @param glowColor The color for the glow effect. Must not be null.
     * @param radius    The radius/size of the shadow effect.
     * @return A Glow effect with drop shadow input for enhanced visual impact.
     */
    private Glow createGlowEffect(Color glowColor, int radius) {
        Glow glow = new Glow(GLOW_LEVEL);
        DropShadow shadow = new DropShadow();
        shadow.setColor(glowColor);
        shadow.setRadius(radius);
        glow.setInput(shadow);
        return glow;
    }

    /**
     * Updates the hero description text based on the selected hero type.
     * Fetches description from the controller and updates the description label.
     *
     * @param heroType The hero type to display description for. Can be null
     *                 for default "select a hero" message.
     */
    private void updateHeroDescription(HeroType heroType) {
        if (getController() != null && descriptionLabel != null) {
            descriptionLabel.setTextFill(TAN_COLOR);
            int heroTypeIndex = getHeroTypeIndex(heroType);
            if (heroTypeIndex != 0) {
                getController().heroDescription(heroTypeIndex, descriptionLabel);
            } else {
                descriptionLabel.setText("SELECT A HERO TO SEE THEIR DESCRIPTION.");
            }
        }
    }

    /**
     * Converts a HeroType enum to the corresponding integer index.
     * Used for interfacing with controller methods that expect numeric hero types.
     *
     * @param heroType The HeroType enum value to convert. Can be null.
     * @return Integer index: 0 for null/unknown, 1 for WARRIOR, 2 for PRIESTESS, 3 for THIEF.
     */
    private int getHeroTypeIndex(HeroType heroType) {
        if (heroType == null) return 0;
        return switch (heroType) {
            case WARRIOR -> 1;
            case PRIESTESS -> 2;
            case THIEF -> 3;
        };
    }

    /**
     * Gets the fallback color for a hero type when sprite images cannot be loaded.
     * Provides thematically appropriate colors for each hero class.
     *
     * @param heroType The hero type to get a color for. Can be null.
     * @return CSS color string: brown for WARRIOR, gold for PRIESTESS,
     *         gray for THIEF, light gray for null/unknown.
     */
    private String getHeroFallbackColor(HeroType heroType) {
        if (heroType == null) return "#CCCCCC";
        return switch (heroType) {
            case WARRIOR -> "#8B4513";
            case PRIESTESS -> "#DAA520";
            case THIEF -> "#696969";
        };
    }

    /**
     * Creates a styled label with consistent formatting.
     * Applies the specified font and text color for visual consistency.
     *
     * @param text      The label text content. Must not be null.
     * @param font      The font to apply to the label. Must not be null.
     * @param textColor The text color to apply. Must not be null.
     * @return A Label with the specified text, font, and color applied.
     */
    private Label createStyledLabel(String text, Font font, Color textColor) {
        Label label = new Label(text);
        label.setFont(font);
        label.setTextFill(textColor);
        return label;
    }

    /**
     * Creates the hero description label area.
     * Sets up the label for displaying dynamic hero information with proper
     * text wrapping, alignment, and sizing constraints.
     *
     * @param labelFont Font to apply to the description label. Must not be null.
     * @return A Label configured for hero description display with appropriate
     *         styling, sizing, and initial placeholder text.
     */
    private Label createDescriptionLabel(Font labelFont) {
        descriptionLabel = createStyledLabel("SELECT A HERO TO SEE THEIR DESCRIPTION.", labelFont, TAN_COLOR);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setAlignment(Pos.CENTER);
        descriptionLabel.setStyle("-fx-text-alignment: center;");
        descriptionLabel.setPrefHeight(BASE_DESCRIPTION_HEIGHT);
        descriptionLabel.setMaxWidth(BASE_DESCRIPTION_WIDTH);
        return descriptionLabel;
    }

    /**
     * Creates the start game button with form validation integration.
     * Uses the parent class's styling method for consistency and adds
     * specific event handling for game initiation.
     *
     * @param buttonFont Font to apply to the button. Must not be null.
     * @param theUI      GameUI instance for screen transitions. Must not be null.
     * @return A Button configured for starting the game with proper styling,
     *         dimensions, and event handling.
     */
    private Button createStartButton(Font buttonFont, GameUI theUI) {
        // Use parent's createStyledButton method for consistency
        startGameBtn = createStyledButton("START", buttonFont);
        startGameBtn.setOnAction(event -> handleStartGameAction(theUI));

        // Override specific dimensions for this button
        startGameBtn.setPrefWidth(BASE_BUTTON_WIDTH);
        startGameBtn.setPrefHeight(BASE_BUTTON_HEIGHT);

        return startGameBtn;
    }

    /**
     * Handles the start game button action with comprehensive validation.
     * Validates form inputs, provides user feedback for invalid states,
     * and initiates game creation when all requirements are met.
     *
     * @param theUI GameUI instance for screen transitions. Must not be null.
     */
    private void handleStartGameAction(GameUI theUI) {
        String heroName = nameBox.getText().trim();

        if (heroName.isEmpty() || selectedHeroType == null) {
            flashButton();
            handleInvalidForm(heroName);
            return;
        }

        if (getController() != null) {
            updateControllerWithName();
            startGame(theUI);
        }
    }

    /**
     * Handles invalid form submission by providing appropriate user feedback.
     * Focuses the name field if empty, or displays error message if no hero selected.
     *
     * @param heroName The current hero name input (may be empty). Must not be null.
     */
    private void handleInvalidForm(String heroName) {
        if (heroName.isEmpty()) {
            nameBox.requestFocus();
        } else if (selectedHeroType == null && descriptionLabel != null) {
            descriptionLabel.setText("PLEASE SELECT A HERO TYPE.");
            descriptionLabel.setTextFill(Color.RED);
        }
    }

    /**
     * Initiates the game start process with the selected hero configuration.
     * Creates a mock toggle group for compatibility with controller interface
     * and triggers the game start sequence.
     *
     * @param theUI GameUI instance for screen transitions. Must not be null.
     */
    private void startGame(GameUI theUI) {
        ToggleGroup mockGroup = createMockToggleGroup();
        getController().startGame(null, theUI, mockGroup);
    }

    /**
     * Creates a mock ToggleGroup for controller interface compatibility.
     * The controller expects a ToggleGroup for hero selection, so this creates
     * a temporary one with the selected hero type for interface compliance.
     *
     * @return A ToggleGroup containing a selected RadioButton with the chosen
     *         hero type as user data, or empty group if no selection.
     */
    private ToggleGroup createMockToggleGroup() {
        ToggleGroup mockGroup = new ToggleGroup();
        if (selectedHeroType != null) {
            RadioButton mockButton = new RadioButton();
            mockButton.setUserData(selectedHeroType);
            mockButton.setSelected(true);
            mockButton.setToggleGroup(mockGroup);
        }
        return mockGroup;
    }

    /**
     * Validates the current form state and updates UI accordingly.
     * Checks if both hero name and type are selected, then updates button styling
     * and description text to reflect the current validation state.
     */
    private void validateForm() {
        if (startGameBtn == null || nameBox == null) return;

        boolean isValid = isFormValid();
        updateButtonStyle(isValid);
        updateDescriptionIfNeeded(isValid);
    }

    /**
     * Updates description text if it's currently showing an error state.
     * Restores normal description or placeholder text when form becomes valid
     * or when name field becomes empty with a hero selected.
     *
     * @param isValid Current form validation state.
     */
    private void updateDescriptionIfNeeded(boolean isValid) {
        if (descriptionLabel != null && descriptionLabel.getTextFill().equals(Color.RED)) {
            if (isValid || (nameBox.getText().trim().isEmpty() && selectedHeroType != null)) {
                if (selectedHeroType != null) {
                    updateHeroDescription(selectedHeroType);
                } else {
                    descriptionLabel.setText("SELECT A HERO TO SEE THEIR DESCRIPTION.");
                    descriptionLabel.setTextFill(TAN_COLOR);
                }
            }
        }
    }

    /**
     * Updates the start button styling based on form validation state.
     * Applies normal styling for valid forms or disabled styling for invalid forms.
     *
     * @param isValid Current form validation state.
     */
    private void updateButtonStyle(boolean isValid) {
        if (isValid) {
            // Reset to parent's default styling when valid
            startGameBtn.setStyle("");
            startGameBtn.setOnMouseEntered(e -> {});
            startGameBtn.setOnMouseExited(e -> {});
        } else {
            applyInvalidButtonStyle();
        }
    }

    /**
     * Applies disabled/invalid styling to the start button.
     * Uses darker colors and removes hover effects to indicate the button is not available.
     */
    private void applyInvalidButtonStyle() {
        String invalidStyle = "-fx-background-color: linear-gradient(to bottom, #1A1A1A, #0D0D0D); " +
                "-fx-border-color: #333333; " +
                "-fx-text-fill: #666666; " +
                "-fx-border-width: 4px; " +
                "-fx-padding: 15px 40px; " +
                "-fx-background-radius: 0; " +
                "-fx-border-radius: 0;";
        startGameBtn.setStyle(invalidStyle);
        startGameBtn.setOnMouseEntered(e -> {});
        startGameBtn.setOnMouseExited(e -> {});
    }

    /**
     * Creates a brief flash animation on the start button to indicate invalid submission.
     * Temporarily applies a dark flash style then returns to normal validation styling.
     */
    private void flashButton() {
        startGameBtn.setStyle(FLASH_BUTTON_STYLE);
        Timeline timeline = new Timeline(
                new javafx.animation.KeyFrame(Duration.millis(FLASH_DURATION_MS), e -> validateForm())
        );
        timeline.play();
    }

    /**
     * Safely retrieves a resource input stream for the given path.
     *
     * @param path Resource path to load. Must not be null.
     * @return InputStream for the resource, or null if not found.
     */
    private InputStream getResourceStream(String path) {
        return getClass().getResourceAsStream(path);
    }

    /**
     * Logs an error message to the standard error stream.
     * Centralized error logging for consistent error handling.
     *
     * @param message Error message to log. Must not be null.
     */
    private void logError(String message) {
        System.err.println(message);
    }
}