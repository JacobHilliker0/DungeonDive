package main.Controller;

import main.Model.Model;
import main.Model.character.Hero;
import main.Model.dungeon.Dungeon;
import main.Model.util.HeroType;
import main.View.GameUI;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 * Controls how user input is passed from view to model.
 *
 * @author Jacob Hilliker
 * @author Emanuel Feria
 * @author Vladyslav Glavatskyi
 * @version 5/13/2025
 */
public class Controller {

    private boolean myNameSet = false;
    private String myHeroName = "Player";
    private final Model myGameModel;
    private TextField myHeroNameField;

    // Game-specific controllers, initialized when game starts
    private GameController myGameController;
    private InputController myInputController;
    private StateController myStateController;


    public Controller() {
        this.myGameModel = new Model();
    }

    public Model getGameModel() {
        return myGameModel;
    }

    public void newGame(final ActionEvent theEvent, final GameUI theUI) {
        theUI.showHeroSelection();
    }

    public void savedGames(final ActionEvent theEvent, final GameUI theUI) {
        theUI.showSavesScreen();
    }

    public void exitProgram(final ActionEvent theEvent) {
        System.exit(0);
    }

    public void setHeroName(final String theHeroNameInput) {
        if (theHeroNameInput != null && !theHeroNameInput.trim().isEmpty()) {
            this.myHeroName = theHeroNameInput.trim();
            this.myNameSet = true;
            System.out.println("Hero name set to: " + this.myHeroName);
        } else {
            this.myHeroName = "Player";
            this.myNameSet = false; // Or true if you allow default name
            System.out.println("Hero name is empty or invalid; will use default or prompt.");
        }
    }

    public void linkHeroNameField(final TextField theHeroNameField) {
        this.myHeroNameField = theHeroNameField;
        if (this.myHeroNameField != null) {
            this.myHeroNameField.textProperty().addListener((observable, oldValue, newValue) -> {
                setHeroName(newValue);
            });
            setHeroName(this.myHeroNameField.getText());
        }
    }

    public void heroDescription(final int theHeroType, final Label theDescription) {
        HeroType selectedHeroType = null;
        switch (theHeroType) {
            case 1: selectedHeroType = HeroType.WARRIOR; break;
            case 2: selectedHeroType = HeroType.PRIESTESS; break;
            case 3: selectedHeroType = HeroType.THIEF; break;
            default:
                theDescription.setText("Select a hero to see their description.");
                return;
        }
        // Use the description from the enum
        theDescription.setText(selectedHeroType.getDescription());
    }

    public void startGame(final ActionEvent theEvent, final GameUI theUI,
                          final ToggleGroup theHeroes) {

        if (myHeroNameField != null) {
            setHeroName(myHeroNameField.getText());
        }

        if (theHeroes.getSelectedToggle() == null) {
            System.err.println("Start Game Error: No hero type selected.");
            // TODO: Show an error message to the user via theUI
            return;
        }
        if (!myNameSet || myHeroName.trim().isEmpty()) {
            if (myHeroNameField != null && !myHeroNameField.getText().trim().isEmpty()) {
                setHeroName(myHeroNameField.getText());
            } else {
                System.err.println("Start Game Error: Hero name not set or is empty.");
                // TODO: Show an error message to the user
                return;
            }
        }

        HeroType selectedType = getHeroTypeFromToggle(theHeroes);
        if (selectedType == null) {
            System.err.println("Start Game Error: Could not determine selected hero type.");
            return;
        }

        myGameModel.initializeNewGame(selectedType, myHeroName);
        initializeGameControllers(theUI); // Initialize controllers AFTER model is ready
        theUI.showGameScreen(); // Show game screen AFTER controllers are set up, especially input
    }

    // This maybe should go in GameController, but pauseGame was here so I put
    // this here as well (it also pauses the game). Not sure if that was the best choice.
    public void helpMenu(final ActionEvent theEvent, final GameUI theUI) {
        theUI.showHelpMenu();
    }

    public void pauseGame(final ActionEvent theEvent, final GameUI theUI) {
        theUI.showPauseMenu();
    }

    public void resumeCurrentGame(final GameUI theUI) {
        // This checks if the player was previously in combat, so the proper
        // screen is shown.
        if (myStateController.isInState(StateController.GameState.COMBAT)) {
            // This line of code I wrote might be horrible, but because showCombatScreen
            // has special parameters I had to use a ton of get methods to find all
            // the data needed for it to work.
            theUI.showCombatScreen(myGameModel.getDungeon().
                    getRoom(myGameModel.getPlayer().getPosition()).getMonsters());
        } else {
            theUI.showGameScreen(); // This will re-attach key listeners via GameUI.showGameScreen()
        }
    }

    public void quitToMenu(final GameUI theUI) {
        myGameModel.resetGame();
        // Nullify game-specific controllers as they are tied to a game session
        myGameController = null;
        myInputController = null;
        myStateController = null;
        theUI.showIntroScreen();
    }

    private HeroType getHeroTypeFromToggle(final ToggleGroup theHeroes) {
        if (theHeroes.getSelectedToggle() == null) return null;

        RadioButton selectedButton = (RadioButton) theHeroes.getSelectedToggle();
        if (selectedButton.getUserData() instanceof HeroType) {
            return (HeroType) selectedButton.getUserData();
        }

        String heroNameString = selectedButton.getText().trim();
        try {
            return HeroType.valueOf(heroNameString.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: Could not map radio button text '" + heroNameString + "' to HeroType. Check UserData or button text. Defaulting to WARRIOR.");
        }
        return HeroType.WARRIOR; // Fallback
    }

    /**
     * Initializes game-specific controllers.
     * This should be called when a new game starts or a game is loaded.
     * @param theUI GameUI reference.
     */
    private void initializeGameControllers(final GameUI theUI) {
        // Initialize StateController first
        myStateController = new StateController();

        // Then initialize GameController with Model, UI, and StateController
        myGameController = new GameController(myGameModel, theUI, myStateController);

        // Initialize InputController with GameController and StateController
        myInputController = new InputController(myGameController, myStateController);

        System.out.println("Game controllers initialized.");
        theUI.setInputController(myInputController);


        // For testing - remove later
        // myGameController.printStatus();
    }

    // Delegate methods to access model data
    public Hero getPlayer() {
        return myGameModel.getPlayer();
    }

    public Dungeon getDungeon() {
        return myGameModel.getDungeon();
    }

    public GameController getGameController() {
        return myGameController;
    }

    public StateController getStateController() {return myStateController; }

}
