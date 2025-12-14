package main.View.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.View.GameUI;

import java.io.InputStream;

/**
 * The HelpScreen class displays game instructions and controls to assist players.
 * This screen provides comprehensive information about game objectives, movement controls,
 * combat mechanics, and inventory management. It features a responsive design that
 * adapts to different screen sizes while maintaining visual consistency with other
 * game screens.
 *
 * <p>The help screen includes:</p>
 * <ul>
 *   <li>Game objective explanation</li>
 *   <li>Movement controls for dungeon exploration</li>
 *   <li>Combat controls and mechanics</li>
 *   <li>Inventory management instructions</li>
 *   <li>Navigation buttons to resume game or activate cheats</li>
 * </ul>
 *
 * <p>The screen uses a brick wall background for thematic consistency and
 * the custom PixelFont for retro gaming aesthetics.</p>
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/10/2025
 * @see Screen
 * @see GameUI
 * @see Controller
 */
public class HelpScreen extends Screen {

    // ====== FONT SIZE CONSTANTS ======
    /** Font size for the main title text */
    private static final int TITLE_FONT_SIZE = 45;

    /** Font size for section subtitle headers */
    private static final int SUBTITLE_FONT_SIZE = 22;

    /** Font size for content body text */
    private static final int CONTENT_FONT_SIZE = 18;

    /** Font size for navigation buttons */
    private static final int BUTTON_FONT_SIZE = 28;

    // ====== SPACING CONSTANTS ======
    /** Vertical spacing between major content sections */
    private static final int CONTENT_SPACING = 30;

    /** Vertical spacing between subsections within content areas */
    private static final int SECTION_SPACING = 20;

    // ====== RESOURCE PATHS ======
    /** Path to the custom pixel font used throughout the application */
    private static final String FONT_PATH = "/main/View/fonts/PixelFont.ttf";

    /** Path to the brick wall background image */
    private static final String BACKGROUND_PATH = "/sprites/backgrounds/brick_wall_background.png";

    /** CSS style string for drop shadow effects */
    private static final String SHADOW_STYLE = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 4, 4);";

    // ====== COLOR CONSTANTS ======
    /** Background color for content panels (semi-transparent dark gray) */
    private static final String PANEL_COLOR = "rgba(42, 42, 42, 0.9)";

    /** Border color for panels and UI elements (brown/bronze theme) */
    private static final String BORDER_COLOR = "#8B4513";

    /** Primary text color (light gray) */
    private static final String TEXT_COLOR = "#E0E0E0";

    /** Accent color for subtitles and highlights (golden/yellow) */
    private static final String SUBTITLE_COLOR = "#DAA520";

    /**
     * Constructs a new HelpScreen instance.
     *
     * @param thePrimaryStage The main application stage that will host this screen.
     *                       Must not be null.
     * @param theController   The main controller instance for handling user interactions
     *                       and game state management. Must not be null.
     * @throws IllegalArgumentException if either parameter is null (inherited from Screen)
     */
    public HelpScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    /**
     * Displays the help screen with comprehensive game instructions and controls.
     * This method creates and configures the complete UI layout including:
     * - Title section with styled text
     * - Scrollable content area with game instructions
     * - Navigation buttons for user actions
     *
     * <p>The screen is designed to be responsive and will scale appropriately
     * to different window sizes while maintaining readability and visual appeal.</p>
     *
     * @param theUI The GameUI instance used for screen transitions and
     *              coordinating with other parts of the application.
     *              Must not be null.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        // Use a StackPane to hold the content and apply scaling
        StackPane scalableRoot = new StackPane();

        // The actual content pane, similar to IntroScreen
        VBox contentPane = new VBox(CONTENT_SPACING);
        contentPane.setAlignment(Pos.CENTER);
        contentPane.setPadding(new Insets(50));
        contentPane.setMaxWidth(BASE_WIDTH - 100); // Set a max width for content

        setupBackground(scalableRoot); // Apply background to the root pane

        VBox titleSection = createTitleSection();
        ScrollPane contentArea = createContentArea();
        VBox buttonSection = createButtonSection(theUI);

        contentPane.getChildren().addAll(titleSection, contentArea, buttonSection);
        scalableRoot.getChildren().add(contentPane);

        // Get current scene dimensions, or use defaults
        double currentWidth = getStage().getScene() != null ? getStage().getScene().getWidth() : BASE_WIDTH;
        double currentHeight = getStage().getScene() != null ? getStage().getScene().getHeight() : BASE_HEIGHT;

        Scene helpScene = new Scene(scalableRoot, currentWidth, currentHeight);

        // This call handles the window size adaptability
        applyScaling(scalableRoot, helpScene);

        getStage().setScene(helpScene);
        getStage().setTitle("Help - Dungeon Dive");
        getStage().show();
    }

    /**
     * Sets up the background styling for the help screen.
     * Attempts to load the brick wall background image from resources.
     * If the image cannot be loaded, falls back to a solid color background.
     *
     * @param root The root StackPane that will receive the background styling.
     *             Must not be null.
     */
    private void setupBackground(StackPane root) {
        try (InputStream bgStream = getClass().getResourceAsStream(BACKGROUND_PATH)) {
            if (bgStream != null) {
                // Use the same background as IntroScreen for consistency
                root.setStyle("-fx-background-image: url('" + getClass().getResource(BACKGROUND_PATH).toExternalForm() + "'); " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center;");
            } else {
                // Fallback to solid color if background image fails
                System.err.println("Could not load background image, using fallback color");
                root.setStyle("-fx-background-color: #2a2a2a;");
            }
        } catch (Exception e) {
            System.err.println("Could not load background image, using fallback color: " + e.getMessage());
            root.setStyle("-fx-background-color: #2a2a2a;");
        }
    }

    /**
     * Creates the title section containing the main "GAME INSTRUCTIONS" header.
     * The title uses the custom PixelFont with appropriate styling including
     * drop shadow effects for visual appeal.
     *
     * @return A VBox containing the styled title text, properly aligned and padded.
     */
    private VBox createTitleSection() {
        VBox titleSection = new VBox();
        titleSection.setAlignment(Pos.CENTER);
        titleSection.setPadding(new Insets(0, 0, 20, 0));

        // Use Text object for title like IntroScreen
        Text titleLabel = new Text("GAME INSTRUCTIONS");
        Font titleFont = loadFont(FONT_PATH, TITLE_FONT_SIZE, "Impact");
        titleLabel.setFont(titleFont);
        titleLabel.setFill(Color.ORANGE);
        titleLabel.setStyle(SHADOW_STYLE);

        titleSection.getChildren().add(titleLabel);
        return titleSection;
    }

    /**
     * Creates the main content area containing all game instructions.
     * This method builds a scrollable pane that includes sections for:
     * - Game objective
     * - Movement controls
     * - Combat controls
     * - Inventory controls
     *
     * <p>The content is organized in clearly separated sections with
     * appropriate styling and formatting for readability.</p>
     *
     * @return A ScrollPane containing all instruction content with proper
     *         styling and scroll behavior configured.
     */
    private ScrollPane createContentArea() {
        VBox contentContainer = new VBox(SECTION_SPACING);
        contentContainer.setAlignment(Pos.TOP_LEFT);
        contentContainer.setPadding(new Insets(25));
        contentContainer.setStyle("-fx-background-color: " + PANEL_COLOR + "; " +
                "-fx-border-color: " + BORDER_COLOR + "; " +
                "-fx-border-width: 3px; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-radius: 10px;");

        // Game objective section
        VBox objectiveSection = createContentSection(
                "GAME OBJECTIVE",
                "Find rooms with pillars and click interact to collect them. After all 4 have been collected, " +
                        "find the exit to win. You can also find chests and items in some rooms. Some rooms have traps " +
                        "and monsters. If your health gets to 0, you will lose the game."
        );

        // Controls sections
        VBox movementSection = createContentSection(
                "DUNGEON MOVEMENT",
                "W/↑ - Move Up\n" +
                        "A/← - Move Left\n" +
                        "D/→ - Move Right\n" +
                        "S/↓ - Move Down"
        );

        VBox combatSection = createContentSection(
                "COMBAT CONTROLS",
                "A - Attack\n" +
                        "S - Special Attack\n" +
                        "R - Run\n" +
                        "I - Inventory\n" +
                        "ESC - Pause Game"
        );

        VBox inventorySection = createContentSection(
                "INVENTORY CONTROLS",
                "W - Scroll Up\n" +
                        "S - Scroll Down\n" +
                        "ENTER - Use Selected Item\n" +
                        "ESC - Close Inventory"
        );

        contentContainer.getChildren().addAll(objectiveSection, movementSection, combatSection, inventorySection);

        ScrollPane scrollPane = new ScrollPane(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefViewportHeight(300); // Set a base height, scaling will handle the rest

        return scrollPane;
    }

    /**
     * Creates a content section with a title and body text.
     * This is a utility method for generating consistent formatting
     * across all instruction sections.
     *
     * @param title   The section title (e.g., "COMBAT CONTROLS").
     *                Must not be null.
     * @param content The section content text. Can contain newlines
     *                for formatting. Must not be null.
     * @return A VBox containing the formatted title and content labels
     *         with appropriate styling applied.
     */
    private VBox createContentSection(String title, String content) {
        VBox section = new VBox(10);
        section.setAlignment(Pos.TOP_LEFT);

        // Section title using PixelFont
        Label titleLabel = new Label(title);
        Font subtitleFont = loadFont(FONT_PATH, SUBTITLE_FONT_SIZE, "Courier New");
        titleLabel.setFont(subtitleFont);
        titleLabel.setTextFill(Color.web(SUBTITLE_COLOR));
        titleLabel.setStyle("-fx-underline: true;");

        // Section content using PixelFont
        Label contentLabel = new Label(content);
        Font contentFont = loadFont(FONT_PATH, CONTENT_FONT_SIZE, "Courier New");
        contentLabel.setFont(contentFont);
        contentLabel.setTextFill(Color.web(TEXT_COLOR));
        contentLabel.setWrapText(true);
        contentLabel.setLineSpacing(5); // Add some line spacing for readability

        section.getChildren().addAll(titleLabel, contentLabel);
        return section;
    }

    /**
     * Creates the button section containing navigation and action buttons.
     * This section includes:
     * - Resume Game button: Returns the player to the active game
     * - Vision Cheat button: Activates the dungeon vision cheat for debugging/assistance
     *
     * <p>Both buttons use the standardized styling from the Screen superclass
     * for consistency with other screens in the application.</p>
     *
     * @param theUI The GameUI instance needed for button action handlers
     *              and screen transitions. Must not be null.
     * @return A VBox containing the styled navigation buttons with
     *         appropriate event handlers configured.
     */
    private VBox createButtonSection(GameUI theUI) {
        VBox buttonSection = new VBox();
        buttonSection.setAlignment(Pos.CENTER);
        buttonSection.setPadding(new Insets(20, 0, 0, 0));

        // Use the createStyledButton method from the Screen superclass
        Font buttonFont = loadFont(FONT_PATH, BUTTON_FONT_SIZE, "Courier New");
        Button resumeGameButton = createStyledButton("RESUME GAME ", buttonFont);
        Button activateCheatButton = createStyledButton("VISION CHEAT", buttonFont);

        // Configure button actions
        resumeGameButton.setOnAction(event -> getController().resumeCurrentGame(theUI));
        activateCheatButton.setOnAction(event -> getController().getGameController().activateDungeonVisionCheat());

        buttonSection.getChildren().addAll(resumeGameButton, activateCheatButton);
        return buttonSection;
    }
}