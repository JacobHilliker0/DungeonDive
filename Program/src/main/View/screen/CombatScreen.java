package main.View.screen;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Controller.Controller;
import main.Model.character.Hero;
import main.Model.character.Monster;
import main.Model.util.HeroType;
import main.View.GameUI;

import java.util.ArrayList;
import java.util.List;

public class CombatScreen extends Screen {

    private Label myName;
    private Label myHealth;
    private Label myBaseDamage;
    private Label mySpecialDamage;
    private Label myBlockChance;

    private Label enemyNameLabel;
    private Label enemyHealthLabel;
    private Label enemyAttackLabel;

    private VBox myCombatMessages;
    private Monster currentMonster;

    public CombatScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
    }

    public void showScreen(final GameUI theUI, final List<Monster> theMonsters) {
        BorderPane root = new BorderPane();
        Scene CombatScene = new Scene(root, 800, 500);
        root.setStyle("-fx-padding: 10;");

        // This may not be good code, but helps with finding stats for printing later.
        HeroType player = getController().getPlayer().getType();

        HBox pauseAndHelpButtons = new HBox(10);
        Button helpButton = new Button("Help/Controls");
        Button pauseButton = new Button("Pause");
        setButtonSize(helpButton);
        setButtonSize(pauseButton);
        helpButton.setOnAction(event -> getController().helpMenu(event, theUI));
        pauseButton.setOnAction(event -> getController().pauseGame(event, theUI));
        pauseAndHelpButtons.getChildren().addAll(helpButton, pauseButton);
        pauseAndHelpButtons.setAlignment(Pos.TOP_RIGHT);
        pauseAndHelpButtons.setPadding(new Insets(0, 0, 5, 0));
        root.setTop(pauseAndHelpButtons);

        // Player stats vbox
        VBox playerStatsBox = new VBox(10);
        myName = new Label("Name: ");
        myHealth = new Label("Health: ");
        myBaseDamage = new Label("Base Attack: " + player.getBaseAttack());
        mySpecialDamage = new Label(player.getSpecialAttackName() + ": " + player.getSpecialAttackDamage());
        myBlockChance = new Label("Block Chance: ?");
        playerStatsBox.getChildren().addAll(new Label("--- Player ---"), myName, myHealth, myBaseDamage, mySpecialDamage, myBlockChance);
        playerStatsBox.setAlignment(Pos.CENTER_LEFT);
        playerStatsBox.setBackground(new Background(new BackgroundFill(Color.CYAN, CornerRadii.EMPTY, null)));

        // Monster stats
        VBox monsterStatsBox = new VBox(5);
        enemyNameLabel = new Label("Name: ");
        enemyHealthLabel = new Label("Health: ");
        enemyAttackLabel = new Label("Attack: "); // Example
        monsterStatsBox.getChildren().addAll(new Label("--- Monster ---"), enemyNameLabel, enemyHealthLabel, enemyAttackLabel);
        monsterStatsBox.setAlignment(Pos.CENTER_LEFT);
        monsterStatsBox.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, null)));

        VBox statsBox = new VBox(20, playerStatsBox, monsterStatsBox);
        statsBox.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
        statsBox.setAlignment(Pos.CENTER);
        root.setLeft(statsBox);

        // Combat Options on the right.
        VBox combatOptions = new VBox(10);
        combatOptions.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1;");
        Label combatOptionsLabel = new Label("--- Combat Options ---");
        Button attackButton = new Button("Base Attack");
        Button specialAttackButton = new Button(player.getSpecialAttackName());
        Button inventoryButton = new Button("Inventory");
        Button runButton = new Button("Run");
        setButtonSize(attackButton);
        setButtonSize(specialAttackButton);
        setButtonSize(inventoryButton);
        setButtonSize(runButton);

        attackButton.setOnAction(event -> getController().getGameController().playerAttack());
        specialAttackButton.setOnAction(event -> getController().getGameController().playerSpecialAttack());
        inventoryButton.setOnAction(event -> getController().getGameController().openInventory());
        runButton.setOnAction(event -> getController().getGameController().playerRun());

        combatOptions.getChildren().addAll(combatOptionsLabel, attackButton, specialAttackButton, inventoryButton, runButton);
        combatOptions.setAlignment(Pos.CENTER);
        root.setRight(combatOptions);

        // Combat message log at the bottom.
        myCombatMessages = new VBox(5);
        myCombatMessages.setStyle("-fx-padding: 10; -fx-border-color: silver; -fx-border-width: 1; -fx-background-color: #f0f0f0;");
        ScrollPane messageScrollPane = new ScrollPane(myCombatMessages);
        messageScrollPane.setPrefHeight(100);
        messageScrollPane.setFitToWidth(true);
        messageScrollPane.setPrefHeight(100);myCombatMessages.heightProperty().addListener((obs, oldVal, newVal) -> {
            messageScrollPane.setVvalue(1.0); // Set Vvalue to 1.0 (scroll to bottom)
        });
        root.setBottom(messageScrollPane);

        // Set current monster and update stats
        if (theMonsters != null && !theMonsters.isEmpty()) {
            this.currentMonster = theMonsters.get(0); // For now, focus on the first monster
        } else {
            this.currentMonster = null;
        }

        updateCombatStats();
        addGameMessage(getController().getGameController().getCombatDescription());
        getStage().setScene(CombatScene);
        getStage().setTitle("Combat Screen");
        getStage().show();
    }

//    /**
//     * Updates stats during combat.
//     */
//    public void updateCombatStats() {
//        Hero player = getController().getPlayer();
//        if (player != null) {
//            myName.setText("Name: " + player.getName());
//            myHealth.setText("Health: " + player.getHealth() + " / " + player.getMaxHealth());
//            myBaseDamage.setText("Damage: " + player.getType().getBaseAttack());
//            mySpecialDamage.setText("Damage: " + player.getType().getSpecialAttackDamage());
//            // Add block chance if available in Hero/HeroType
//            // myBlockChance.setText("Block Chance: " + player.getBlockChance() + "%");
//        }
//
//        // Update monster stats
//        if (currentMonster != null && currentMonster.isAlive()) {
//            enemyNameLabel.setText("Name: " + currentMonster.getName());
//            enemyHealthLabel.setText("Health: " + currentMonster.getHealth() + " / " + currentMonster.getMaxHealth());
//            enemyAttackLabel.setText("Attack: " + currentMonster.getType().getBaseAttack()); // Assuming Monster has getType() and MonsterType has getBaseAttack()
//        } else {
//            enemyNameLabel.setText("Name: - DEFEATED -");
//            enemyHealthLabel.setText("Health: -");
//            enemyAttackLabel.setText("Attack: -");
//            this.currentMonster = null; // Clear if dead
//        }
//    }

    public void addGameMessage(final String message) {
        if (myCombatMessages != null) {
            Label messageLabel = new Label(message);
            myCombatMessages.getChildren().add(messageLabel);
        }
    }

    /**
     * Updates which monster is being displayed (if you have multiple).
     * @param monster The new monster to display.
     */
    public void updateCurrentMonster(final Monster monster) {
        this.currentMonster = monster;
        updateCombatStats();
    }

    /**
     * Updates the display with the current state of monsters.
     * Call this when monster health changes or a new monster is targeted.
     * @param monsters The current list of monsters.
     */
    public void updateDisplay(final List<Monster> monsters) {
        System.out.println("CombatScreen: Updating display."); // Log to see if it's called
        if (monsters != null && !monsters.isEmpty()) {
            // Update to the first monster (or implement logic for multiple)
            this.currentMonster = monsters.get(0);
        } else {
            // No monsters left, set to null
            this.currentMonster = null;
        }
        updateCombatStats(); // Call the method that redraws labels

    }

    public void updateCombatStats() {
        Hero player = getController().getPlayer();
        if (player != null) {
            myName.setText("Name: " + player.getName());
            myHealth.setText("Health: " + player.getHealth() + " / " + player.getMaxHealth());
            myBaseDamage.setText("Base Attack: " + player.getType().getBaseAttack());
            mySpecialDamage.setText(player.getType().getSpecialAttackName() + ": " + player.getType().getSpecialAttackDamage());
        }

        // Update monster stats
        if (currentMonster != null && currentMonster.isAlive()) {
            enemyNameLabel.setText("Name: " + currentMonster.getName());
            enemyHealthLabel.setText("Health: " + currentMonster.getHealth() + " / " + currentMonster.getMaxHealth()); // Uses getMaxHealth()
            enemyAttackLabel.setText("Attack: " + currentMonster.getType().getBaseAttack());
        } else {
            enemyNameLabel.setText("Name: - DEFEATED -");
            enemyHealthLabel.setText("Health: -");
            enemyAttackLabel.setText("Attack: -");
        }
    }


    // You need to override the abstract showScreen(GameUI theUI) from Screen.
    // Since we now have showScreen(GameUI theUI, List<Monster> theMonsters),
    // we can provide a default implementation or throw an error.
    @Override
    public void showScreen(final GameUI theUI) {
        // This shouldn't be called directly for CombatScreen anymore.
        // Or, you could show it with an empty monster list / error.
        System.err.println("CombatScreen.showScreen(GameUI) called without monster list! Showing empty screen.");
        showScreen(theUI, new ArrayList<>()); // Show with empty list as fallback.
    }
}
