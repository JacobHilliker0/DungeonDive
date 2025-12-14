package main.View.util; // Assuming consistent main.* package structure

import main.View.GameUI;           // Assuming consistent main.* package structure
import main.Controller.Controller; // Import Controller
import javafx.stage.Stage;

/**
 * Holds constants and shared UI instances.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class UIConstants {

    public final GameUI MY_GAME_UI;
    // It's generally better if UIConstants doesn't own GameUI,
    // but rather GameUI is managed by Main and UIConstants just holds constants.
    // However, following the existing structure for now...

    /**
     * Constructor for UIConstants.
     * Initializes GameUI with the primary stage and the shared controller.
     *
     * @param thePrimaryStage The primary stage of the application.
     * @param theController   The main controller instance.
     */
    public UIConstants(final Stage thePrimaryStage, final Controller theController) {
        if (thePrimaryStage == null) {
            throw new IllegalArgumentException("Primary stage cannot be null for UIConstants.");
        }
        if (theController == null) {
            throw new IllegalArgumentException("Controller cannot be null for UIConstants.");
        }
        // Now GameUI constructor expects a Controller as well
        MY_GAME_UI = new GameUI(thePrimaryStage, theController);
    }

    /**
     * If MY_GAME_UI needs to be accessed from outside, provide a getter
     * @return The GameUI instance.
     */
    public GameUI getGameUI() {
        return MY_GAME_UI;
    }
}