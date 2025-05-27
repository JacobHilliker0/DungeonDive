package main.View.screen;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.Controller.StateController;
import main.Model.character.Hero;
import main.Model.dungeon.Dungeon;
import main.Model.dungeon.Room;
import main.Model.util.Point;
import main.View.GameUI;



/**
 * Class for the GameScreen.
 * This is where the main gameplay (dungeon view, player stats, etc.) is displayed.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class GameScreen extends Screen {

    private Label myPlayerHealthLabel;
    private Label myPlayerAttackLabel;
    private Label myPlayerGoldLabel;
    private Label myPillarsLabel;
    private BorderPane myRoomViewPane;      // For displaying the current room
    private GridPane myMinimapPane;         // For displaying the minimap
    private VBox myMessagesArea;            // For displaying game messages

    private Canvas myRoomCanvas;
    private GraphicsContext myGraphicsContext;
    private AnimationTimer myGameLoop;
    //private GridPane myMinimapPane; // For the minimap
    private Label myMinimapLabel; // To show "Minimap" text
    // Room view styling constants
    private static final int ROOM_VIEW_SIZE = 400;
    private static final int MINIMAP_SIZE = 80;


    /**
     * Constructor for GameScreen.
     *
     * @param thePrimaryStage The primary Stage for the application.
     * @param theController   The main application Controller instance.
     */
    public GameScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController); // Pass both to the superclass (Screen)
    }

    /**
     * showScreen for the GameScreen.
     *
     * @param theUI GameUI used for Observer ActionEvent stuff and screen transitions.
     */
    @Override
    public void showScreen(final GameUI theUI) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 10;");
        Scene gameScene = new Scene(root, 800, 600); // Potentially larger screen for game

        // Top: Game title and pause button
        BorderPane topPane = new BorderPane();

        // Add title to the top pane
        Label gameTitle = new Label("Dungeon Dive");
        gameTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        topPane.setLeft(gameTitle);

        // Add pause button and help menu to top-right
        HBox pauseAndHelpButtons = new HBox(10);
        Button helpButton = new Button("Help/Controls");
        Button pauseButton = new Button("Pause");
        setButtonSize(helpButton);
        setButtonSize(pauseButton);
        helpButton.setOnAction(event -> getController().helpMenu(event, theUI));
        pauseButton.setOnAction(event -> getController().pauseGame(event, theUI));
        pauseAndHelpButtons.getChildren().addAll(helpButton, pauseButton);
        pauseAndHelpButtons.setPadding(new Insets(0, 0, 5, 0));
        topPane.setRight(pauseAndHelpButtons);

        // Add the topPane to the root
        root.setTop(topPane);

        // Center: room view with minimap using StackPane
        StackPane centerContainer = new StackPane(); // Use StackPane instead of VBox
        centerContainer.setAlignment(Pos.CENTER); // Center canvas by default

        // Main room canvas
        myRoomCanvas = new Canvas(ROOM_VIEW_SIZE, ROOM_VIEW_SIZE);
        myGraphicsContext = myRoomCanvas.getGraphicsContext2D();
        // Optional: Add a border to easily see the canvas area during layout
        // myRoomCanvas.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        // Minimap container
        VBox minimapContainer = new VBox(5);
        minimapContainer.setAlignment(Pos.CENTER);
        minimapContainer.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 5; -fx-background-color: #f0f0f0;");
        myMinimapLabel = new Label("--- Minimap ---");
        myMinimapLabel.setStyle("-fx-font-weight: bold;");

        // Create minimap grid
        myMinimapPane = new GridPane();
        myMinimapPane.setAlignment(Pos.CENTER);
        myMinimapPane.setHgap(2);
        myMinimapPane.setVgap(2);
        // Adjust these sizes for your desired minimap appearance
        myMinimapPane.setMaxSize(80, 80);
        myMinimapPane.setMinSize(80, 80);


        minimapContainer.getChildren().addAll(myMinimapLabel, myMinimapPane);
        // Constrain the VBox size to match the GridPane
        minimapContainer.setMaxSize(90, 110); // Slightly larger for padding/label

        // Add canvas first (bottom), then minimap (top)
        centerContainer.getChildren().addAll(myRoomCanvas, minimapContainer);

        // Position minimap in the top-right corner
        StackPane.setAlignment(minimapContainer, Pos.TOP_RIGHT);
        // Add some margin (Top, Right, Bottom, Left)
        StackPane.setMargin(minimapContainer, new Insets(10, 10, 0, 0));

        root.setCenter(centerContainer); // Set the StackPane as the center

        // Initialize minimap
        updateMinimap();

        // Left: Player Stats
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
        myPlayerHealthLabel = new Label("Health: ");
        myPlayerAttackLabel = new Label("Attack: "); // Or other relevant stats
        myPlayerGoldLabel = new Label("Gold: ");
        myPillarsLabel = new Label("Pillars: ");
        statsBox.getChildren().addAll(
                new Label("--- Player Stats ---"),
                myPlayerHealthLabel,
                myPlayerAttackLabel,
                myPlayerGoldLabel,
                myPillarsLabel
        );
        root.setLeft(statsBox);

        // Right: Inventory / Actions
        VBox actionsBox = new VBox(10);
        actionsBox.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");

        Button interactButton = new Button("Interact");
        setButtonSize(interactButton);
        interactButton.setOnAction(event -> {
            MY_CONTROLLER.getGameController().interact();
            updatePlayerStats();
        });

        Button InventoryButton = new Button("Inventory");
        setButtonSize(InventoryButton);
        InventoryButton.setOnAction(event -> getController().getGameController().openInventory());

        actionsBox.getChildren().addAll(new Label("--- Actions/Inventory ---"),
                interactButton, InventoryButton);

        actionsBox.setAlignment(Pos.TOP_CENTER);
        root.setRight(actionsBox);

        // Bottom: Message Log
        myMessagesArea = new VBox(20);
        myMessagesArea.setStyle("-fx-padding: 10; -fx-border-color: silver; -fx-border-width: 1; -fx-background-color: #f0f0f0;");
        ScrollPane messageScrollPane = new ScrollPane(myMessagesArea);
        messageScrollPane.setFitToWidth(true);
        messageScrollPane.setPrefHeight(100);myMessagesArea.heightProperty().addListener((obs, oldVal, newVal) -> {
            messageScrollPane.setVvalue(1.0); // Set Vvalue to 1.0 (scroll to bottom)
        });
        root.setBottom(messageScrollPane);

        // Initial update of UI elements
        updatePlayerStats();
        updateMinimap();
        startGameLoop();

        addGameMessage("Welcome to the dungeon!");
        addGameMessage(getController().getGameController().getCurrentRoomDescription());

        getStage().setScene(gameScene);
        getStage().setTitle("Dungeon Dive - In Progress");
        getStage().show();
    }

    /**
     * Creates the room view pane that shows the current room in detail
     */
    private BorderPane createRoomViewPane() {
        BorderPane roomView = new BorderPane();
        roomView.setPrefSize(ROOM_VIEW_SIZE, ROOM_VIEW_SIZE);
        roomView.setStyle("-fx-background-color: #d3d3d3;"); // Light gray background for the room

        return roomView;
    }

    /**
     * Creates the minimap pane
     */
    private GridPane createMinimapPane() {
        GridPane minimap = new GridPane();
        minimap.setAlignment(Pos.CENTER);
        minimap.setHgap(1);
        minimap.setVgap(1);
        minimap.setPrefSize(MINIMAP_SIZE, MINIMAP_SIZE);
        minimap.setMaxSize(MINIMAP_SIZE, MINIMAP_SIZE);
        minimap.setStyle("-fx-background-color: black;");

        return minimap;
    }

    /**
     * Updates room description in the message area.
     */
    public void displayRoomDescription() {
        if (getController() != null && getController().getGameController() != null &&
                getController().getGameController().getCurrentRoomDescription() != null) {
            //myMessagesArea.getChildren()
            addGameMessage(getController().getGameController().getCurrentRoomDescription());
        }
    }

    /**
     * Updates the player stats displayed on the screen.
     * This should be called by the GameController when stats change.
     */
    public void updatePlayerStats() {
        if (getController() != null && getController().getPlayer() != null) {
            main.Model.character.Hero player = getController().getPlayer();
            myPlayerHealthLabel.setText("Health: " + player.getHealth() + " / " + player.getMaxHealth());
            // Assuming Hero has getAttackPower() or similar after stats refactor
            myPlayerAttackLabel.setText("Attack: " + player.getType().getBaseAttack()); // Example
            myPlayerGoldLabel.setText("Gold: " + player.getGold());
            if (getController().getDungeon() != null) {
                myPillarsLabel.setText("Pillars: " + player.getPillarsActivated() + " / " + getController().getDungeon().getTotalPillars());
            } else {
                myPillarsLabel.setText("Pillars: " + player.getPillarsActivated() + " / ?");
            }
        }
    }

    /**
     * Updates the minimap to show visited rooms and current position.
     * This is called only when moving between rooms, not during movement within a room.
     */
    public void updateMinimap() {
        if (getController() == null || getController().getDungeon() == null || myMinimapPane == null) {
            return;
        }

        myMinimapPane.getChildren().clear(); // Clear old minimap

        Dungeon dungeon = getController().getDungeon();
        Hero player = getController().getPlayer();
        Point playerPos = player.getPosition();

        // Define minimap cell size
        final int CELL_SIZE = 10;

        // Determine visible range (e.g., 5x5 grid around player)
        final int VIEW_RANGE = 2; // Shows 5x5 grid (2 rooms in each direction from player)

        int startX = Math.max(0, playerPos.getX() - VIEW_RANGE);
        int endX = Math.min(dungeon.getWidth() - 1, playerPos.getX() + VIEW_RANGE);
        int startY = Math.max(0, playerPos.getY() - VIEW_RANGE);
        int endY = Math.min(dungeon.getHeight() - 1, playerPos.getY() + VIEW_RANGE);

        // Create minimap cells
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                Room room = dungeon.getRoom(x, y);
                if (room != null) {
                    Label cell = new Label();
                    cell.setMinSize(CELL_SIZE, CELL_SIZE);
                    cell.setMaxSize(CELL_SIZE, CELL_SIZE);
                    cell.setAlignment(Pos.CENTER);
                    cell.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-font-size: 8px;");

                    // Determine cell appearance
                    if (x == playerPos.getX() && y == playerPos.getY()) {
                        // Current position
                        cell.setText("@");
                        cell.setStyle(cell.getStyle() + "-fx-background-color: lightgreen; -fx-font-weight: bold;");
                    } else if (room.isVisited()) {
                        // Visited room - show room type
                        String symbol = getRoomSymbol(room);
                        cell.setText(symbol);
                        cell.setStyle(cell.getStyle() + "-fx-background-color: white;");
                    } else {
                        // Unvisited room
                        cell.setText("?");
                        cell.setStyle(cell.getStyle() + "-fx-background-color: darkgray;");
                    }

                    // Add to grid (adjust coordinates for grid positioning)
                    myMinimapPane.add(cell, x - startX, y - startY);
                }
            }
        }
    }

    /**
     * Gets a symbol representation for a room type.
     */
    private String getRoomSymbol(final Room room) {
        switch (room.getRoomType()) {
            case ENTRANCE: return "E";
            case EXIT: return "X";
            case BOSS: return "B";
            case PILLAR:
                return room.getPillar() != null && room.getPillar().isActivated() ? "✓" : "P";
            case MONSTER:
                return room.getMonsters().isEmpty() ? " " : "M";
            case TREASURE:
                return room.getItems().isEmpty() ? " " : "$";
            case TRAP:
                return room.getTrap() != null && room.getTrap().isSprung() ? " " : "!";
            case EMPTY:
            default:
                return " ";
        }
    }

    /**
     * Adds a message to the game's message log area.
     * @param message The message string to add.
     */
    public void addGameMessage(final String message) {
        if (myMessagesArea != null) {
            Label messageLabel = new Label(message);
            myMessagesArea.getChildren().add(messageLabel);
        }
    }

    private void startGameLoop() {
        if (myGameLoop == null) {
            System.out.println("GameScreen: Creating new AnimationTimer.");
            myGameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    // Check state *inside* the loop
                    if (getController() != null && getController().getGameController() != null &&
                            getController().getGameController().getStateController().isInState(StateController.GameState.EXPLORING)) {
                        updateGame();
                    }
                    renderRoom();
                }
            };
        }

        myGameLoop.start();
        System.out.println("GameScreen: AnimationTimer started/restarted.");
    }

    public void stopGameLoop() {
        if (myGameLoop != null) {
            myGameLoop.stop();
            System.out.println("GameScreen: AnimationTimer stopped.");
        }
    }

    private void updateGame() {
        Hero player = getController().getPlayer();
        if (player != null) {
            player.updatePosition();

            // Check for room transitions at boundaries
            checkRoomTransition();
        }
    }

    private void renderRoom() {
        // Clear canvas
        myGraphicsContext.clearRect(0, 0, myRoomCanvas.getWidth(), myRoomCanvas.getHeight());

        // Draw room background
        drawRoomBackground();

        // Draw room elements

        // Draw player
        drawPlayer();
    }

    private void drawRoomBackground() {
        // Draw floor tiles
        myGraphicsContext.setFill(Color.LIGHTGRAY);
        myGraphicsContext.fillRect(0, 0, myRoomCanvas.getWidth(), myRoomCanvas.getHeight());

        // Draw walls
        myGraphicsContext.setFill(Color.DARKGRAY);
        // Top wall
        myGraphicsContext.fillRect(0, 0, myRoomCanvas.getWidth(), 20);
        // Bottom wall
        myGraphicsContext.fillRect(0, myRoomCanvas.getHeight() - 20, myRoomCanvas.getWidth(), 20);
        // Left wall
        myGraphicsContext.fillRect(0, 0, 20, myRoomCanvas.getHeight());
        // Right wall
        myGraphicsContext.fillRect(myRoomCanvas.getWidth() - 20, 0, 20, myRoomCanvas.getHeight());

        // Draw doors based on current room
        Room currentRoom = getCurrentRoom();
        if (currentRoom != null) {
            myGraphicsContext.setFill(Color.BROWN);
            if (currentRoom.hasNorthDoor()) {
                myGraphicsContext.fillRect(myRoomCanvas.getWidth()/2 - 20, 0, 40, 20);
            }
            if (currentRoom.hasSouthDoor()) {
                myGraphicsContext.fillRect(myRoomCanvas.getWidth()/2 - 20, myRoomCanvas.getHeight() - 20, 40, 20);
            }
            if (currentRoom.hasEastDoor()) {
                myGraphicsContext.fillRect(myRoomCanvas.getWidth() - 20, myRoomCanvas.getHeight()/2 - 20, 20, 40);
            }
            if (currentRoom.hasWestDoor()) {
                myGraphicsContext.fillRect(0, myRoomCanvas.getHeight()/2 - 20, 20, 40);
            }
        }
    }

    private void drawPlayer() {
        Hero player = getController().getPlayer();
        if (player != null) {
            myGraphicsContext.setFill(Color.BLUE);
            myGraphicsContext.fillOval(
                    player.getPixelX() - 10,
                    player.getPixelY() - 10,
                    20, 20
            );
        }
    }

    /**
     * Gets the current room the player is in
     */
    private Room getCurrentRoom() {
        if (getController() == null || getController().getPlayer() == null || getController().getDungeon() == null) {
            return null;
        }

        Point playerPos = getController().getPlayer().getPosition();
        return getController().getDungeon().getRoom(playerPos);
    }

    private void checkRoomTransition() {
        Hero player = getController().getPlayer();
        Room currentRoom = getCurrentRoom();
        double px = player.getPixelX();
        double py = player.getPixelY();
        boolean moved = false;

        if (currentRoom == null) {
            return; // Exit if player or room is null
        }

        // Define boundary/door threshold (e.g., 30 pixels)
        int boundary = 30;
        double canvasWidth = myRoomCanvas.getWidth();
        double canvasHeight = myRoomCanvas.getHeight();

        // Check North
        if (py <= boundary) {
            if (currentRoom.hasNorthDoor()) {
                getController().getGameController().movePlayerNorth(); // Attempt move
                player.setPixelY((int)canvasHeight - (boundary + 10)); // Reset position (south side)
                moved = true; // Set flag
            } else {
                player.setPixelY(boundary);
            }
        }
        // Check South
        else if (py >= canvasHeight - boundary) {
            if (currentRoom.hasSouthDoor()) {
                getController().getGameController().movePlayerSouth();
                player.setPixelY(boundary + 10); // Reset position (north side)
                moved = true;
            } else {
                player.setPixelY((int)canvasHeight - boundary);
            }
        }
        // Check West
        else if (px <= boundary) {
            if (currentRoom.hasWestDoor()) {
                getController().getGameController().movePlayerWest();
                player.setPixelX((int)canvasWidth - (boundary + 10)); // Reset position (east side)
                moved = true;
            } else {
                player.setPixelX(boundary);
            }
        }
        // Check East
        else if (px >= canvasWidth - boundary) {
            if (currentRoom.hasEastDoor()) {
                getController().getGameController().movePlayerEast();
                player.setPixelX(boundary + 10); // Reset position (west side)
                moved = true;
            } else {
                player.setPixelX((int)canvasWidth - boundary);
            }
        }

        // If a room transition occurred, update everything
        if (moved) {
            updateMinimap();
            updatePlayerStats();
            displayRoomDescription();
        }
    }
}
