package main.View.screen;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import main.Controller.Controller;
import main.Model.element.Item;
import main.View.GameUI;

import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

/**
 * Manages the inventory UI, allowing players to view, select, and use items.
 * This screen is displayed as a separate, themed window that overlays the main
 * game screen. It features a responsive design that adapts to the main window's size,
 * a scrollable list of items with icons, a detailed description area for the
 * selected item, and keyboard controls for navigation and interaction.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 6/13/2025
 */
public class InventoryScreen extends Screen {

    // ====== RESPONSIVE CONFIGURATION CONSTANTS ======
    private static final class Config {
        // Window size
        static final double WINDOW_WIDTH_RATIO = 0.35;
        static final double WINDOW_HEIGHT_RATIO = 0.75;
        static final double WINDOW_MIN_WIDTH = 400.0;
        static final double WINDOW_MAX_WIDTH = 600.0;
        static final double WINDOW_MIN_HEIGHT = 500.0;
        static final double WINDOW_MAX_HEIGHT = 800.0;

        // Font size ratios relative to window height (further reduced for buttons)
        static final double TITLE_FONT_RATIO = 0.035;
        static final double SECTION_TITLE_FONT_RATIO = 0.025;
        static final double ITEM_FONT_RATIO = 0.022;
        static final double DESC_FONT_RATIO = 0.020;
        static final double BUTTON_FONT_RATIO = 0.020;

        // Spacing and padding ratios
        static final double PADDING_RATIO = 0.025;
        static final double SPACING_RATIO = 0.02;
        static final double ITEM_SPACING_RATIO = 0.015;

        // Colors (Medieval/Fantasy Theme)
        static final String BACKGROUND_COLOR = "#2a2a2a";
        static final String PANEL_COLOR = "#3a3a3a";
        static final String BORDER_COLOR = "#8B4513"; // SaddleBrown
        static final String TEXT_COLOR = "#E0E0E0";
        static final String TITLE_COLOR = "#D4AF37"; // Gold
        static final String SECTION_TITLE_COLOR = "#DAA520"; // GoldenRod
        static final String SELECTION_COLOR = "#8B4513";
        static final String EMPTY_TEXT_COLOR = "#AAAAAA";

        // potion icons
        static final String HEALTH_POTION_ICON = "/sprites/icons/health_potion.png";
        static final String VISION_POTION_ICON = "/sprites/icons/vision_potion.png";
        static final String DEFAULT_ITEM_ICON = "/sprites/icons/default_item.png";
        static final double ITEM_ICON_SIZE_RATIO = 0.08;
        static final double ITEM_ICON_MIN_SIZE = 40.0;
        static final double ITEM_ICON_MAX_SIZE = 80.0;

        // Button styles (matching HeroSelectionScreen)
        static final String BUTTON_BASE_STYLE =
                "-fx-background-color: linear-gradient(to bottom, #4A4A4A, #2A2A2A); " +
                        "-fx-text-fill: #E0E0E0; " +
                        "-fx-border-color: #DAA520; " +
                        "-fx-border-width: 3px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 0; " +
                        "-fx-border-radius: 0; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0.5, 2, 2);";

        static final String BUTTON_HOVER_STYLE =
                "-fx-background-color: linear-gradient(to bottom, #5A5A5A, #3A3A3A); " +
                        "-fx-text-fill: #FFFFFF; " +
                        "-fx-border-color: #FFD700; " +
                        "-fx-border-width: 3px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 0; " +
                        "-fx-border-radius: 0; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(218,165,32,0.8), 15, 0.7, 3, 3);";

        static final String BUTTON_DISABLED_STYLE =
                "-fx-background-color: linear-gradient(to bottom, #1A1A1A, #0D0D0D); " +
                        "-fx-text-fill: #666666; " +
                        "-fx-border-color: #333333; " +
                        "-fx-border-width: 3px; " +
                        "-fx-padding: 10px 20px; " +
                        "-fx-background-radius: 0; " +
                        "-fx-border-radius: 0; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0.3, 1, 1);";

        // Pixel font path
        static final String FONT_PATH = "/main/View/fonts/PixelFont.ttf";
    }

    // ====== UI AND LOGIC MEMBERS ======
    private final Stage myInventoryStage;
    private VBox myItemListVBox;
    private Label myItemDescriptionLabel;
    private Label mySelectedItemTitleLabel;
    private Button myUseButton;
    private Button myCloseButton;
    private int mySelectedIndex = 0;
    private List<Item> myCurrentInventory;
    private boolean isUIInitialized = false;
    private NumberBinding itemIconSizeBinding;


    /**
     * Constructs a new InventoryScreen.
     *
     * @param thePrimaryStage The primary stage of the application.
     * @param theController The main controller for the application.
     */
    public InventoryScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
        myInventoryStage = new Stage();
        myInventoryStage.initStyle(StageStyle.TRANSPARENT);
        myInventoryStage.initOwner(getStage());
    }

    /**
     * Displays the inventory screen as a separate window, calculating its size
     * relative to the main game window. It creates the layout, sets up event
     * handlers, and positions the inventory window in the center of the main stage.
     *
     * @param theUI The main GameUI instance, used for coordinating screen transitions.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        // Calculate initial size based on the main window
        double primaryWidth = getStage().getWidth();
        double primaryHeight = getStage().getHeight();
        double windowWidth = Math.max(Config.WINDOW_MIN_WIDTH,
                Math.min(Config.WINDOW_MAX_WIDTH, primaryWidth * Config.WINDOW_WIDTH_RATIO));
        double windowHeight = Math.max(Config.WINDOW_MIN_HEIGHT,
                Math.min(Config.WINDOW_MAX_HEIGHT, primaryHeight * Config.WINDOW_HEIGHT_RATIO));

        BorderPane root = createInventoryLayout();
        Scene scene = new Scene(root, windowWidth, windowHeight);
        scene.setFill(null); // Make scene background transparent

        itemIconSizeBinding = Bindings.createDoubleBinding(() -> {
            double widthBased = scene.getWidth() * 0.12; // 12% of window width
            double heightBased = scene.getHeight() * Config.ITEM_ICON_SIZE_RATIO;
            double calculated = Math.min(widthBased, heightBased); // Use the smaller of the two
            return Math.max(Config.ITEM_ICON_MIN_SIZE, Math.min(Config.ITEM_ICON_MAX_SIZE, calculated));
        }, scene.heightProperty());

        setupEventHandlers(scene);

        myInventoryStage.setScene(scene);
        myInventoryStage.setTitle("DungeonDive - Inventory");

        // Center the inventory on the main game window
        myInventoryStage.setX(getStage().getX() + (getStage().getWidth() - windowWidth) / 2);
        myInventoryStage.setY(getStage().getY() + (getStage().getHeight() - windowHeight) / 2);

        myInventoryStage.show();

        // Mark UI as initialized and then activate
        isUIInitialized = true;
        onScreenActivated();
    }

    private BorderPane createInventoryLayout() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + Config.BACKGROUND_COLOR + "; " +
                "-fx-border-color: " + Config.BORDER_COLOR + "; " +
                "-fx-border-width: 3px; -fx-background-radius: 6px; -fx-border-radius: 6px;");

        // Responsive Bindings
        NumberBinding padding = root.heightProperty().multiply(Config.PADDING_RATIO);
        NumberBinding spacing = root.heightProperty().multiply(Config.SPACING_RATIO);
        root.paddingProperty().bind(Bindings.createObjectBinding(() ->
                new Insets(padding.doubleValue()), padding));

        // ====== TOP: TITLE ======
        Label titleLabel = createTitleLabel(root);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        BorderPane.setMargin(titleLabel, new Insets(0, 0, 10, 0));

        // ====== CENTER: ITEM LIST AND DESCRIPTION ======
        HBox centerContainer = new HBox();
        centerContainer.spacingProperty().bind(spacing);

        // Left side: Item List
        VBox leftPanel = createItemListPanel(root, spacing);

        // Right side: Selected Item Description
        VBox rightPanel = createDescriptionPanel(root, spacing);

        HBox.setHgrow(leftPanel, Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        centerContainer.getChildren().addAll(leftPanel, rightPanel);

        // ====== BOTTOM: BUTTONS ======
        HBox buttonBox = createButtonBox(root, spacing);

        root.setTop(titleLabel);
        root.setCenter(centerContainer);
        root.setBottom(buttonBox);

        return root;
    }

    private Label createTitleLabel(BorderPane root) {
        Label titleLabel = new Label("INVENTORY");
        titleLabel.setStyle("-fx-text-fill: " + Config.TITLE_COLOR + ";");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double fontSize = Math.min(root.getHeight() * Config.TITLE_FONT_RATIO, 40);
            Font pixelFont = loadFont(Config.FONT_PATH, (int)fontSize, "Impact");
            return Font.font(pixelFont.getFamily(), FontWeight.BOLD, fontSize);
        }, root.heightProperty()));
        return titleLabel;
    }

    private VBox createItemListPanel(BorderPane root, NumberBinding spacing) {
        Label listTitle = new Label("Your Items");
        listTitle.setStyle("-fx-text-fill: " + Config.SECTION_TITLE_COLOR + ";");
        listTitle.setWrapText(true);
        listTitle.setAlignment(Pos.CENTER);
        listTitle.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double fontSize = Math.min(root.getHeight() * Config.SECTION_TITLE_FONT_RATIO, 18);
            Font pixelFont = loadFont(Config.FONT_PATH, (int)fontSize, "Arial");
            return Font.font(pixelFont.getFamily(), FontWeight.BOLD, fontSize);
        }, root.heightProperty()));

        myItemListVBox = new VBox();
        NumberBinding itemSpacing = root.heightProperty().multiply(Config.ITEM_SPACING_RATIO);
        myItemListVBox.spacingProperty().bind(itemSpacing);

        ScrollPane scrollPane = new ScrollPane(myItemListVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + Config.PANEL_COLOR + "; " +
                "-fx-background-color: transparent; " +
                "-fx-border-color: " + Config.BORDER_COLOR + "; " +
                "-fx-border-width: 2px;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox listContainer = new VBox();
        listContainer.spacingProperty().bind(spacing.divide(2));
        listContainer.getChildren().addAll(listTitle, scrollPane);

        NumberBinding containerPadding = root.heightProperty().multiply(Config.PADDING_RATIO / 3);
        listContainer.paddingProperty().bind(Bindings.createObjectBinding(() ->
                new Insets(containerPadding.doubleValue()), containerPadding));

        listContainer.setStyle("-fx-background-color: " + Config.PANEL_COLOR + "; " +
                "-fx-background-radius: 5px; " +
                "-fx-border-color: " + Config.BORDER_COLOR + "; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return listContainer;
    }

    private VBox createDescriptionPanel(BorderPane root, NumberBinding spacing) {
        mySelectedItemTitleLabel = new Label("No Item Selected");
        mySelectedItemTitleLabel.setStyle("-fx-text-fill: " + Config.SECTION_TITLE_COLOR + ";");
        mySelectedItemTitleLabel.setWrapText(true);
        mySelectedItemTitleLabel.setAlignment(Pos.CENTER);
        mySelectedItemTitleLabel.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double fontSize = Math.min(root.getHeight() * Config.SECTION_TITLE_FONT_RATIO, 18);
            Font pixelFont = loadFont(Config.FONT_PATH, (int)fontSize, "Arial");
            return Font.font(pixelFont.getFamily(), FontWeight.BOLD, fontSize);
        }, root.heightProperty()));

        myItemDescriptionLabel = new Label("Select an item to see its description.");
        myItemDescriptionLabel.setStyle("-fx-text-fill: " + Config.TEXT_COLOR + ";");
        myItemDescriptionLabel.setWrapText(true);
        myItemDescriptionLabel.setAlignment(Pos.TOP_LEFT);
        myItemDescriptionLabel.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double fontSize = Math.min(root.getHeight() * Config.DESC_FONT_RATIO, 14);
            Font pixelFont = loadFont(Config.FONT_PATH, (int)fontSize, "Arial");
            return Font.font(pixelFont.getFamily(), fontSize);
        }, root.heightProperty()));

        // Bind the max width to ensure proper wrapping
        myItemDescriptionLabel.maxWidthProperty().bind(root.widthProperty().multiply(0.4));

        VBox descriptionContainer = new VBox();
        descriptionContainer.spacingProperty().bind(spacing.divide(2));
        descriptionContainer.getChildren().addAll(mySelectedItemTitleLabel, myItemDescriptionLabel);

        NumberBinding containerPadding = root.heightProperty().multiply(Config.PADDING_RATIO / 3);
        descriptionContainer.paddingProperty().bind(Bindings.createObjectBinding(() ->
                new Insets(containerPadding.doubleValue()), containerPadding));

        descriptionContainer.setStyle("-fx-background-color: " + Config.PANEL_COLOR + "; " +
                "-fx-background-radius: 5px; " +
                "-fx-border-color: " + Config.BORDER_COLOR + "; " +
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px;");

        VBox.setVgrow(myItemDescriptionLabel, Priority.ALWAYS);

        return descriptionContainer;
    }

    private HBox createButtonBox(BorderPane root, NumberBinding spacing) {
        myUseButton = createStyledButton("Use", root);
        myUseButton.setOnAction(e -> useSelectedItem());

        myCloseButton = createStyledButton("Close", root);
        myCloseButton.setOnAction(e -> closeInventory());

        HBox buttonBox = new HBox();
        buttonBox.spacingProperty().bind(spacing);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(myUseButton, myCloseButton);

        // Make buttons expand to fill available space
        HBox.setHgrow(myUseButton, Priority.ALWAYS);
        HBox.setHgrow(myCloseButton, Priority.ALWAYS);

        return buttonBox;
    }

    private Button createStyledButton(String text, BorderPane root) {
        Button button = new Button(text);

        // Apply initial style
        button.setStyle(Config.BUTTON_BASE_STYLE);

        // Set up font binding with maximum size constraints
        button.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double fontSize = Math.min(root.getHeight() * Config.BUTTON_FONT_RATIO, 14);
            Font pixelFont = loadFont(Config.FONT_PATH, (int)fontSize, "Courier New");
            return Font.font(pixelFont.getFamily(), FontWeight.BOLD, fontSize);
        }, root.heightProperty()));

        // Set up size bindings with better constraints for smaller buttons
        button.prefWidthProperty().bind(root.widthProperty().multiply(0.28));
        button.minWidthProperty().bind(root.widthProperty().multiply(0.25));
        button.maxWidthProperty().bind(root.widthProperty().multiply(0.35));
        button.minHeightProperty().bind(root.heightProperty().multiply(0.055));
        button.maxHeightProperty().bind(root.heightProperty().multiply(0.08));

        // Enable text wrapping for buttons
        button.setWrapText(true);
        button.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Set up hover effects
        setupButtonHoverEffects(button);

        return button;
    }

    private void setupButtonHoverEffects(Button button) {
        button.setOnMouseEntered(e -> {
            if (!button.isDisabled()) {
                button.setStyle(Config.BUTTON_HOVER_STYLE);
            }
        });

        button.setOnMouseExited(e -> {
            if (button.isDisabled()) {
                button.setStyle(Config.BUTTON_DISABLED_STYLE);
            } else {
                button.setStyle(Config.BUTTON_BASE_STYLE);
            }
        });

        // Update style when disabled state changes
        button.disabledProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                button.setStyle(Config.BUTTON_DISABLED_STYLE);
            } else {
                button.setStyle(Config.BUTTON_BASE_STYLE);
            }
        });
    }

    // ====== LOGIC METHODS ======

    /**
     * Refreshes the inventory display. This method is called when the
     * inventory screen is activated to ensure it shows the most up-to-date
     * information.
     */
    public void onScreenActivated() {
        if (!isUIInitialized) {
            return;
        }
        updateInventory();
    }

    /**
     * Updates the list of items displayed in the inventory. It clears the
     * current list and repopulates it with items from the player's inventory.
     * If the inventory is empty, a message is displayed.
     */
    public void updateInventory() {
        if (MY_CONTROLLER.getPlayer() == null) return;

        // Safety check for UI components
        if (myItemListVBox == null || mySelectedItemTitleLabel == null || myItemDescriptionLabel == null) {
            return;
        }

        myCurrentInventory = MY_CONTROLLER.getPlayer().getInventory();
        myItemListVBox.getChildren().clear();

        if (myCurrentInventory.isEmpty()) {
            Label emptyLabel = new Label("Inventory is empty.");
            emptyLabel.setStyle("-fx-text-fill: " + Config.EMPTY_TEXT_COLOR + "; -fx-font-style: italic;");
            emptyLabel.fontProperty().bind(Bindings.createObjectBinding(() -> {
                double fontSize = Math.min(myInventoryStage.getScene().getHeight() * Config.DESC_FONT_RATIO, 14);
                Font pixelFont = loadFont(Config.FONT_PATH, (int)fontSize, "Arial");
                return Font.font(pixelFont.getFamily(), fontSize);
            }, myInventoryStage.getScene().heightProperty()));
            emptyLabel.setWrapText(true);
            emptyLabel.setAlignment(Pos.CENTER);
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            emptyLabel.setPadding(new Insets(20));

            myItemListVBox.getChildren().add(emptyLabel);
            myUseButton.setDisable(true);
            mySelectedIndex = -1;
            updateSelectedItemInfo();
        } else {
            // Ensure index is valid
            mySelectedIndex = Math.max(0, Math.min(mySelectedIndex, myCurrentInventory.size() - 1));

            for (int i = 0; i < myCurrentInventory.size(); i++) {
                final int itemIndex = i;
                Item item = myCurrentInventory.get(i);
                Label itemLabel = createItemLabel(item.getName(), itemIndex);
                myItemListVBox.getChildren().add(itemLabel);
            }
            myUseButton.setDisable(false);
            updateSelectedItemInfo();
        }
    }

    private Label createItemLabel(String itemName, int itemIndex) {
        Label itemLabel = new Label();

        // Create icon for the item
        ImageView itemIcon = createItemIcon(itemName);
        itemLabel.setGraphic(itemIcon);

        // ICON ONLY - No text displayed
        itemLabel.setText(""); // Empty text
        itemLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); // Show only the graphic (icon)

        // Add tooltip so users can see item name on hover
        Tooltip tooltip = new Tooltip(itemName);
        tooltip.setShowDelay(Duration.millis(500)); // Show after 0.5 second hover
        itemLabel.setTooltip(tooltip);

        // Enable text wrapping and set proper alignment
        itemLabel.setWrapText(true);
        itemLabel.setAlignment(Pos.CENTER);

        NumberBinding padding = myInventoryStage.getScene().heightProperty().multiply(0.008);
        itemLabel.paddingProperty().bind(Bindings.createObjectBinding(() ->
                new Insets(padding.doubleValue()), padding));

        itemLabel.setMaxWidth(Double.MAX_VALUE);

        // Bind max width to ensure proper sizing within the panel
        itemLabel.maxWidthProperty().bind(myInventoryStage.getScene().widthProperty().multiply(0.15));

        // Add click handler to select item
        itemLabel.setOnMouseClicked(e -> updateSelection(itemIndex));

        // Add hover effects
        itemLabel.setOnMouseEntered(e -> {
            if (itemIndex != mySelectedIndex) {
                itemLabel.setStyle("-fx-background-color: rgba(139, 69, 19, 0.3); " +
                        "-fx-text-fill: " + Config.TEXT_COLOR + "; " +
                        "-fx-background-radius: 3px; " +
                        "-fx-cursor: hand;");
            }
        });

        itemLabel.setOnMouseExited(e -> {
            if (itemIndex != mySelectedIndex) {
                itemLabel.setStyle("-fx-background-color: transparent; " +
                        "-fx-text-fill: " + Config.TEXT_COLOR + ";");
            }
        });

        // Style the selected item
        if (itemIndex == mySelectedIndex) {
            itemLabel.setStyle("-fx-background-color: " + Config.SELECTION_COLOR + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-background-radius: 3px; " +
                    "-fx-effect: dropshadow(three-pass-box, rgba(218,165,32,0.6), 8, 0.5, 2, 2);");
        } else {
            itemLabel.setStyle("-fx-background-color: transparent; " +
                    "-fx-text-fill: " + Config.TEXT_COLOR + ";");
        }

        return itemLabel;
    }

    private ImageView createItemIcon(String itemName) {
        ImageView iconView = new ImageView();

        // Bind icon size to responsive dimensions
        iconView.fitWidthProperty().bind(itemIconSizeBinding);
        iconView.fitHeightProperty().bind(itemIconSizeBinding);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);

        // Determine which icon to use based on item name
        String iconPath = getIconPathForItem(itemName);

        try (InputStream iconStream = getClass().getResourceAsStream(iconPath)) {
            if (iconStream != null) {
                Image iconImage = new Image(iconStream);
                iconView.setImage(iconImage);
            } else {
                // Fallback to colored rectangle if icon not found
                createFallbackIcon(iconView, itemName);
            }
        } catch (Exception e) {
            System.err.println("Error loading item icon: " + iconPath + " - " + e.getMessage());
            createFallbackIcon(iconView, itemName);
        }

        return iconView;
    }

    private void createFallbackIcon(ImageView iconView, String itemName) {
        // Create a simple colored rectangle as fallback
        String fallbackColor = getFallbackColorForItem(itemName);
        iconView.setStyle("-fx-background-color: " + fallbackColor + "; " +
                "-fx-border-color: #DAA520; " +
                "-fx-border-width: 1px;");
    }

    private String getFallbackColorForItem(String itemName) {
        if (itemName == null) {
            return "#CCCCCC";
        }

        return switch (itemName.toLowerCase()) {
            case "health potion", "minor healing potion" -> "#FF6B6B"; // Red for health
            case "vision potion" -> "#4ECDC4"; // Teal for vision
            default -> "#CCCCCC"; // Gray for unknown
        };
    }

    private String getIconPathForItem(String itemName) {
        if (itemName == null) {
            return Config.DEFAULT_ITEM_ICON;
        }

        // Match item names to their icon paths
        return switch (itemName.toLowerCase()) {
            case "health potion", "minor healing potion" -> Config.HEALTH_POTION_ICON;
            case "vision potion" -> Config.VISION_POTION_ICON;
            default -> Config.DEFAULT_ITEM_ICON;
        };
    }

    private void updateSelectedItemInfo() {
        if (mySelectedIndex >= 0 && mySelectedIndex < myCurrentInventory.size()) {
            Item selectedItem = myCurrentInventory.get(mySelectedIndex);
            mySelectedItemTitleLabel.setText(selectedItem.getName());
            myItemDescriptionLabel.setText(selectedItem.getDescription());
        } else {
            mySelectedItemTitleLabel.setText("No Item Selected");
            if (myCurrentInventory != null && myCurrentInventory.isEmpty()) {
                myItemDescriptionLabel.setText("Your pack is empty. Find some loot!");
            } else {
                myItemDescriptionLabel.setText("Select an item to see its description.");
            }
        }
    }

    /**
     * Updates the currently selected item in the inventory list. This method
     * ensures the selection index is valid and then refreshes the display to
     * reflect the change.
     *
     * @param newIndex The new index to select.
     */
    public void updateSelection(int newIndex) {
        if (myCurrentInventory != null && !myCurrentInventory.isEmpty()) {
            mySelectedIndex = Math.max(0, Math.min(newIndex, myCurrentInventory.size() - 1));
            MY_CONTROLLER.getGameController().setInventoryIndex(mySelectedIndex);
            updateInventory(); // Refresh the whole display to update selection style
        }
    }

    private void useSelectedItem() {
        if (MY_CONTROLLER.getGameController() != null) {
            MY_CONTROLLER.getGameController().useSelectedItem();
            updateInventory();
        }
    }

    private void closeInventory() {
        // Close the inventory window first
        myInventoryStage.close();

        // Then notify the controller if available
        if (MY_CONTROLLER.getGameController() != null) {
            MY_CONTROLLER.getGameController().closeInventory();
        }
    }

    /**
     * Closes the inventory window.
     */
    public void closeScreen() {
        myInventoryStage.close();
    }

    private void setupEventHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.W || code == KeyCode.UP) {
                updateSelection(mySelectedIndex - 1);
            } else if (code == KeyCode.S || code == KeyCode.DOWN) {
                updateSelection(mySelectedIndex + 1);
            } else if (code == KeyCode.E || code == KeyCode.ENTER) {
                useSelectedItem();
            } else if (code == KeyCode.I || code == KeyCode.ESCAPE) {
                closeInventory();
            }
        });

        // Handler to close window via controller
        myInventoryStage.setOnCloseRequest(event -> {
            event.consume();
            closeInventory();
        });
    }
}