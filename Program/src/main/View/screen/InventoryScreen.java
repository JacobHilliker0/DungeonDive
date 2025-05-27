package main.View.screen;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.Controller.StateController;
import main.Model.element.Item;
import main.View.GameUI;

public class InventoryScreen extends Screen {
    /**
     * A stage field because inventory screen is a pop-up separate from
     * the primary stage.
     */
    private Stage myInventoryStage;

    /**
     * Constructor for InventoryScreen.
     *
     * @param thePrimaryStage primary stage.
     * @param theController controller.
     */
    public InventoryScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
        myInventoryStage = new Stage();
    }

    /**
     * Shows Inventory Screen.
     *
     * @param theUI The GameUI instance, used for managing screen transitions and UI updates.
     */
    public void showScreen(final GameUI theUI) {

        VBox items = new VBox(10);
        Scene inventoryScene = new Scene(items, 150, 400);

        Button closeInventoryButton = new Button("Close Inventory");
        setButtonSize(closeInventoryButton);
        Button useSelectedItemButton = new Button("Use Item");
        setButtonSize(useSelectedItemButton);

        Button scrollUpButton = new Button("\u2191");
        Button scrollDownButton = new Button("\u2193");
        setButtonSize(scrollUpButton);
        setButtonSize(scrollDownButton);

        Label inventoryDescription = new Label(MY_CONTROLLER.getGameController().getInventoryDescription());

        closeInventoryButton.setOnAction(event -> MY_CONTROLLER.getGameController().closeInventory());
        myInventoryStage.setOnCloseRequest(event -> MY_CONTROLLER.getGameController().closeInventory());
        useSelectedItemButton.setOnAction(event -> MY_CONTROLLER.getGameController().useSelectedItem());
        scrollUpButton.setOnAction(event -> MY_CONTROLLER.getGameController().scrollInventoryUp());
        scrollDownButton.setOnAction(event -> MY_CONTROLLER.getGameController().scrollInventoryDown());

        items.getChildren().addAll(closeInventoryButton, useSelectedItemButton, inventoryDescription,
                                   scrollUpButton, scrollDownButton);
        items.setAlignment(Pos.TOP_CENTER);

        myInventoryStage.setTitle("Inventory");
        myInventoryStage.setScene(inventoryScene);
        myInventoryStage.show();
    }

    /**
     * Helps close myInventoryStage. Needed because this class does not use
     * primary stage.
     */
    public void closeScreen() {
        myInventoryStage.close();
    }
}
