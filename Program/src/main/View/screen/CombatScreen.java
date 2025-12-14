package main.View.screen;

import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Controller.Controller;
import main.Model.character.Hero;
import main.Model.character.Monster;
import main.View.GameUI;
import main.Controller.StateController.GameState;

import java.util.List;
import java.util.Objects;

/**
 * The CombatScreen class is responsible for managing and displaying the user interface for combat encounters.
 * It handles all combat-related UI interactions, animations, and responsive scaling of elements.
 * The screen visualizes the turn-based combat between the player's hero and a monster,
 * providing an immersive experience with dynamic layouts and visual effects.
 */
public class CombatScreen extends Screen {

    /**
     * Inner class to hold all responsive configuration constants.
     * This helps in managing UI scaling and layout adjustments based on the window size.
     */
    private static final class ResponsiveConfig {
        static final double BASE_WIDTH = 800.0;
        static final double BASE_HEIGHT = 600.0;
        static final double MIN_SCALE = 0.6;
        static final double MAX_SCALE = 2.5;

        // Layout ratios
        static final double BATTLEFIELD_HEIGHT_RATIO = 0.58;
        static final double INTERFACE_HEIGHT_RATIO = 0.42;
        static final double MESSAGE_HEIGHT_RATIO = 0.20;

        // Sprite size ratios
        static final double HERO_SPRITE_SIZE_RATIO = 0.15;
        static final double MONSTER_SPRITE_SIZE_RATIO = 0.175;
        static final double HERO_SPRITE_MIN_SIZE = 80.0;
        static final double HERO_SPRITE_MAX_SIZE = 160.0;
        static final double MONSTER_SPRITE_MIN_SIZE = 90.0;
        static final double MONSTER_SPRITE_MAX_SIZE = 180.0;

        // UI element ratios
        static final double HEALTH_BAR_WIDTH_RATIO = 0.15;
        static final double HEALTH_BAR_HEIGHT_RATIO = 0.016;
        static final double HEALTH_BAR_MIN_WIDTH = 80.0;
        static final double HEALTH_BAR_MAX_WIDTH = 180.0;
        static final double HEALTH_BAR_MIN_HEIGHT = 8.0;
        static final double HEALTH_BAR_MAX_HEIGHT = 20.0;

        // Font size ratios
        static final double NAME_FONT_SIZE_RATIO = 0.032;
        static final double SUBTITLE_FONT_SIZE_RATIO = 0.022;
        static final double BUTTON_FONT_SIZE_RATIO = 0.028;
        static final double MESSAGE_FONT_SIZE_RATIO = 0.02;

        // Spacing and dimensions
        static final double PADDING_RATIO = 0.025;
        static final double SPACING_RATIO = 0.035;
        static final double HERO_SPACING_RATIO = 0.26;
        static final double BUTTON_WIDTH_RATIO = 0.16;
        static final double BUTTON_HEIGHT_RATIO = 0.08;
        static final double BUTTON_MIN_WIDTH = 80.0;
        static final double BUTTON_MAX_WIDTH = 140.0;
        static final double BUTTON_MIN_HEIGHT = 40.0;
        static final double BUTTON_MAX_HEIGHT = 80.0;

        // Platform dimensions
        static final double PLATFORM_RADIUS_X_RATIO = 0.11;
        static final double PLATFORM_RADIUS_Y_RATIO = 0.075;
        static final double PLATFORM_MIN_RADIUS_X = 60.0;
        static final double PLATFORM_MAX_RADIUS_X = 120.0;
        static final double PLATFORM_MIN_RADIUS_Y = 35.0;
        static final double PLATFORM_MAX_RADIUS_Y = 70.0;

        // Constants
        static final int MAX_MESSAGES = 6;
    }

    /**
     * Inner class for animation duration constants, centralizing timing for all combat animations.
     */
    private static final class AnimationConfig {
        static final Duration ENTRANCE_DURATION = Duration.seconds(1);
        static final Duration ATTACK_DURATION = Duration.millis(500);
        static final Duration SPECIAL_DURATION = Duration.millis(600);
        static final Duration FLASH_DURATION = Duration.millis(100);
        static final Duration TURN_DELAY = Duration.seconds(1.5);
        static final Duration END_DELAY = Duration.seconds(3);
    }

    // ====== UI COMPONENTS ======
    private VBox battlefield;
    private VBox bottomInterface;
    private ImageView heroSprite;
    private ImageView monsterSprite;
    private ProgressBar heroHealthBar;
    private ProgressBar monsterHealthBar;
    private Label heroHealthNumbers;
    private Label heroManaNumbers;
    private Label monsterHealthNumbers;
    private Label heroNameDisplay;
    private Label monsterNameDisplay;
    private VBox combatMessages;

    // ====== COMBAT STATE ======
    private Hero currentHero;
    private Monster currentMonster;
    private boolean playerTurn = true;
    private boolean combatActive = true;

    // ====== RESPONSIVE BINDINGS ======
    private Scene scene;
    private final ResponsiveBindingManager bindingManager;

    /**
     * Constructs a new CombatScreen.
     *
     * @param thePrimaryStage The primary stage of the application.
     * @param theController   The main game controller.
     */
    public CombatScreen(final Stage thePrimaryStage, final Controller theController) {
        super(thePrimaryStage, theController);
        this.bindingManager = new ResponsiveBindingManager();
    }

    /**
     * Shows the combat screen with the default monster list from the current room.
     *
     * @param theUI The main GameUI instance.
     */
    @Override
    public void showScreen(GameUI theUI) {
        showScreen(theUI, null);
    }

    /**
     * Initializes and displays the combat screen.
     *
     * @param theUI    The main GameUI instance.
     * @param monsters A list of monsters for the combat; if null, fetches from the current room.
     */
    public void showScreen(GameUI theUI, List<Monster> monsters) {
        initializeCombatData();
        createScene();
        setupUI();
        finalizeScreen();
        playEntranceAnimation(heroSprite, monsterSprite, this::addWelcomeMessages);
    }

    /**
     * Initializes combat data, such as hero and monster stats.
     */
    private void initializeCombatData() {
        currentHero = getController().getPlayer();
        if (currentHero != null) {
            var currentRoom = getController().getDungeon().getRoom(currentHero.getPosition());
            var monsters = currentRoom.getMonsters();
            currentMonster = monsters.isEmpty() ? null : monsters.getFirst();
        }
        resetCombat();
    }

    /**
     * Creates the main scene for the combat screen.
     */
    private void createScene() {
        var root = new BorderPane();
        root.getStyleClass().add("combat-root");

        var stage = getStage();
        double width = stage.getScene() != null ? stage.getScene().getWidth() : ResponsiveConfig.BASE_WIDTH;
        double height = stage.getScene() != null ? stage.getScene().getHeight() : ResponsiveConfig.BASE_HEIGHT;

        scene = new Scene(root, width, height);
        bindingManager.initializeBindings(scene);
    }

    /**
     * Sets up the entire UI layout, including the battlefield and interface panels.
     */
    private void setupUI() {
        battlefield = createBattlefield();
        bottomInterface = createBottomInterface();

        var root = (BorderPane) scene.getRoot();
        root.setTop(battlefield);
        root.setBottom(bottomInterface);

        setupCSS(scene);
        setupResponsiveBindings();
    }

    /**
     * Finalizes the screen setup and displays it on the stage.
     */
    private void finalizeScreen() {
        var stage = getStage();
        boolean wasFullScreen = stage.isFullScreen();

        stage.setScene(scene);
        stage.setTitle("Combat - " + getHeroName() + " vs " + getMonsterName());

        if (wasFullScreen) {
            stage.setFullScreen(true);
        }

        if (!stage.isShowing()) {
            stage.show();
        }
    }

    /**
     * Creates the battlefield area where character sprites are displayed.
     *
     * @return The VBox containing the battlefield layout.
     */
    private VBox createBattlefield() {
        var vBox = new VBox();
        vBox.getStyleClass().add("battlefield");

        var battleArea = new HBox();
        battleArea.setAlignment(Pos.CENTER);

        bindingManager.bindBattleAreaProperties(battleArea);

        var heroSide = createHeroSide();
        var monsterSide = createMonsterSide();

        bindingManager.bindSidePositions(heroSide, monsterSide);

        battleArea.getChildren().addAll(heroSide, monsterSide);
        vBox.getChildren().add(battleArea);

        return vBox;
    }

    /**
     * Creates the layout for the hero's side of the battlefield.
     *
     * @return A VBox containing the hero's sprite and info panel.
     */
    private VBox createHeroSide() {
        var heroSide = new VBox();
        heroSide.setAlignment(Pos.CENTER);
        heroSide.spacingProperty().bind(bindingManager.getSpacingBinding().divide(2));

        heroSprite = createHeroSprite(currentHero);
        var heroInfo = createHeroInfo();

        heroSide.getChildren().addAll(heroInfo, heroSprite);
        return heroSide;
    }

    /**
     * Creates the info panel for the hero, including name, health, and mana.
     *
     * @return An HBox containing the hero's information.
     */
    private HBox createHeroInfo() {
        var heroInfo = new HBox();
        heroInfo.getStyleClass().add("combat-info-panel");
        heroInfo.setAlignment(Pos.CENTER_RIGHT);
        heroInfo.spacingProperty().bind(bindingManager.getSpacingBinding().divide(2));

        var nameSection = createNameSection(true);
        var healthSection = createHealthSection(true);
        var manaSection = createManaSection();

        heroInfo.getChildren().addAll(nameSection, healthSection, manaSection);
        return heroInfo;
    }

    /**
     * Creates the layout for the monster's side of the battlefield.
     *
     * @return A VBox containing the monster's sprite and info panel.
     */
    private VBox createMonsterSide() {
        var monsterSide = new VBox();
        monsterSide.setAlignment(Pos.CENTER_RIGHT);
        monsterSide.spacingProperty().bind(bindingManager.getSpacingBinding().divide(2));

        monsterSprite = createMonsterSprite(currentMonster);
        var monsterInfo = createMonsterInfo();

        monsterSide.getChildren().addAll(monsterInfo, monsterSprite);
        return monsterSide;
    }

    /**
     * Creates the info panel for the monster, including name and health.
     *
     * @return An HBox containing the monster's information.
     */
    private HBox createMonsterInfo() {
        var monsterInfo = new HBox();
        monsterInfo.getStyleClass().add("combat-info-panel");
        monsterInfo.setAlignment(Pos.CENTER);

        var nameSection = createNameSection(false);
        var healthSection = createHealthSection(false);

        monsterInfo.getChildren().addAll(nameSection, healthSection);
        return monsterInfo;
    }

    /**
     * Creates a name section for either the hero or the monster.
     *
     * @param isHero True if creating for the hero, false for the monster.
     * @return A VBox containing the character's name and subtitle (class or level).
     */
    private VBox createNameSection(boolean isHero) {
        var nameSection = new VBox();
        nameSection.setAlignment(isHero ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        nameSection.spacingProperty().bind(bindingManager.getSpacingBinding().divide(6));

        if (isHero) {
            heroNameDisplay = createNameLabel(getHeroName());
            var classText = createSubtitleLabel(getHeroClass());
            nameSection.getChildren().addAll(heroNameDisplay, classText);
        } else {
            monsterNameDisplay = createNameLabel(getMonsterName());
            var levelText = createSubtitleLabel(getMonsterLevel());
            nameSection.getChildren().addAll(monsterNameDisplay, levelText);
        }

        return nameSection;
    }

    /**
     * Creates a styled name label.
     *
     * @param text The text for the label.
     * @return A configured Label for a character's name.
     */
    private Label createNameLabel(String text) {
        var label = new Label(text);
        label.getStyleClass().add("combat-name");
        label.fontProperty().bind(bindingManager.createNameFontBinding());
        return label;
    }

    /**
     * Creates a styled subtitle label (e.g., for class, level, or HP/Mana labels).
     *
     * @param text The text for the label.
     * @return A configured subtitle Label.
     */
    private Label createSubtitleLabel(String text) {
        var label = new Label(text);
        label.getStyleClass().add("combat-subtitle");
        label.fontProperty().bind(bindingManager.createSubtitleFontBinding());
        return label;
    }

    /**
     * Creates a health section (HP label, health bar, and numeric display) for a character.
     *
     * @param isHero True if creating for the hero, false for the monster.
     * @return A VBox containing health-related UI components.
     */
    private VBox createHealthSection(boolean isHero) {
        var healthSection = new VBox();
        healthSection.setAlignment(isHero ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        healthSection.spacingProperty().bind(bindingManager.getSpacingBinding().divide(6));

        var hpLabel = createSubtitleLabel("HP");
        var healthBar = createHealthBar();
        var healthNumbers = createSubtitleLabel("");

        if (isHero) {
            heroHealthBar = healthBar;
            heroHealthNumbers = healthNumbers;
            updateHeroHealthDisplay();
        } else {
            monsterHealthBar = healthBar;
            monsterHealthNumbers = healthNumbers;
            updateMonsterHealthDisplay();
        }

        healthSection.getChildren().addAll(hpLabel, healthBar, healthNumbers);
        return healthSection;
    }

    /**
     * Creates the mana display section for the hero.
     *
     * @return A VBox containing mana-related UI components.
     */
    private VBox createManaSection() {
        var manaSection = new VBox();
        manaSection.setAlignment(Pos.CENTER_LEFT);
        manaSection.spacingProperty().bind(bindingManager.getSpacingBinding().divide(6));

        var manaLabel = createSubtitleLabel("MANA");
        heroManaNumbers = createSubtitleLabel("");
        updateHeroManaDisplay();

        manaSection.getChildren().addAll(manaLabel, heroManaNumbers);
        return manaSection;
    }

    /**
     * Creates a health bar ProgressBar.
     *
     * @return A configured ProgressBar for health display.
     */
    private ProgressBar createHealthBar() {
        var healthBar = new ProgressBar();
        healthBar.getStyleClass().add("progress-bar");
        bindingManager.bindHealthBarProperties(healthBar);
        return healthBar;
    }

    /**
     * Creates the bottom interface containing the message log and action buttons.
     *
     * @return A VBox containing the bottom interface layout.
     */
    private VBox createBottomInterface() {
        var bottomInterface = new VBox();
        bottomInterface.getStyleClass().add("bottom-interface");
        bindingManager.bindBottomInterfaceProperties(bottomInterface);

        combatMessages = createMessageArea();
        var messageScroll = createMessageScrollPane();
        var buttonArea = createButtonArea();

        bottomInterface.getChildren().addAll(messageScroll, buttonArea);
        return bottomInterface;
    }

    /**
     * Creates the area where combat messages are displayed.
     *
     * @return A VBox configured to hold message labels.
     */
    private VBox createMessageArea() {
        var messageArea = new VBox();
        messageArea.getStyleClass().add("combat-message-area");
        messageArea.setAlignment(Pos.BOTTOM_LEFT);
        messageArea.spacingProperty().bind(bindingManager.getSpacingBinding().divide(4));
        bindingManager.bindMessageAreaProperties(messageArea);
        return messageArea;
    }

    /**
     * Creates a ScrollPane to contain the combat message area, allowing for scrolling.
     *
     * @return A configured ScrollPane.
     */
    private ScrollPane createMessageScrollPane() {
        var messageScroll = new ScrollPane(combatMessages);
        messageScroll.setFitToWidth(true);
        messageScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        messageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageScroll.setStyle("-fx-background-color: transparent;");
        return messageScroll;
    }

    /**
     * Creates the area containing the player's action buttons (Attack, Special, etc.).
     *
     * @return An HBox containing combat action buttons.
     */
    private HBox createButtonArea() {
        var buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        bindingManager.bindButtonAreaProperties(buttonBox);

        var attackBtn = createCombatButton("ATTACK", "attack-button", this::performPlayerAttack);
        var specialBtn = createCombatButton(getSpecialAttackName(), "special-button", this::performSpecialAttack);
        var itemBtn = createCombatButton("ITEM", "item-button", this::openInventory);
        var runBtn = createCombatButton("RUN", "run-button", this::attemptRun);

        buttonBox.getChildren().addAll(attackBtn, specialBtn, itemBtn, runBtn);
        return buttonBox;
    }

    /**
     * Creates a styled and configured button for combat actions.
     *
     * @param text       The text to display on the button.
     * @param styleClass The CSS style class for the button.
     * @param action     The action to perform when the button is clicked.
     * @return A configured Button.
     */
    private Button createCombatButton(String text, String styleClass, Runnable action) {
        var button = new Button(text);
        button.getStyleClass().addAll("combat-button", styleClass);

        bindingManager.bindButtonProperties(button);

        button.setOnAction(_ -> {
            if (playerTurn && combatActive) {
                action.run();
            }
        });

        HBox.setHgrow(button, Priority.ALWAYS);
        return button;
    }

    /**
     * Executes the player's standard attack action and animations.
     */
    private void performPlayerAttack() {
        if (canPlayerAct()) return;

        var attackedMonster = currentMonster;
        startPlayerTurn();
        updateTurnIndicator();
        addCombatMessage(currentHero.getName() + " uses Attack!");

        playAttackAnimation(heroSprite, monsterSprite, () -> {
            getController().getGameController().playerAttack();
            updateCombatDisplay();
            handlePostAttack(attackedMonster);
        });
    }

    /**
     * Executes the player's special attack action and animations.
     */
    private void performSpecialAttack() {
        if (canPlayerAct()) return;

        if (!currentHero.canUseSpecialAttack()) {
            addCombatMessage("Cannot use special attack right now!");
            return;
        }

        var attackedMonster = currentMonster;
        startPlayerTurn();
        updateTurnIndicator();
        addCombatMessage(currentHero.getName() + " uses " +
                currentHero.getType().getSpecialAttackName() + "!");

        playSpecialAttackAnimation(heroSprite, monsterSprite, () -> {
            getController().getGameController().playerSpecialAttack();
            updateCombatDisplay();
            handlePostAttack(attackedMonster);
        });
    }

    /**
     * Opens the player's inventory.
     */
    private void openInventory() {
        addCombatMessage("Item menu - opening inventory...");
        getController().getGameController().openInventory();
    }

    /**
     * Initiates an attempt to run from combat.
     */
    private void attemptRun() {
        addCombatMessage(currentHero.getName() + " tries to run away!");
        getController().getGameController().playerRun();
    }

    /**
     * Handles the logic after a player's attack, determining if the monster is defeated or if it's the monster's turn.
     *
     * @param attackedMonster The monster that was attacked.
     */
    private void handlePostAttack(Monster attackedMonster) {
        refreshMonsterState();

        if (currentMonster != null && currentMonster.isAlive()) {
            var delay = new Timeline(new KeyFrame(AnimationConfig.TURN_DELAY, _ -> performMonsterTurn()));
            delay.play();
        } else {
            endCombat(true, attackedMonster);
        }
    }

    /**
     * Executes the monster's turn, including its attack and animations.
     */
    private void performMonsterTurn() {
        if (!combatActive || currentMonster == null) return;

        addCombatMessage(currentMonster.getName() + " attacks!");

        playMonsterAttackAnimation(monsterSprite, heroSprite, () -> {
            getController().getGameController().monsterAttacks();
            boolean isGameOver = getController().getGameController().getStateController().isInState(GameState.GAME_OVER);
            updateCombatDisplay();

            if (isGameOver) {
                endCombat(false);
            } else if (combatActive) {
                startMonsterTurn();
                updateTurnIndicator();
            }
        });
    }

    /**
     * Public method to update all combat-related UI elements.
     */
    public void updateCombatDisplay() {
        updateCombatStats();
        updateHealthBars();
    }

    /**
     * Updates character stat displays (name, health/mana numbers).
     */
    private void updateCombatStats() {
        if (currentHero != null) {
            heroNameDisplay.setText(currentHero.getName());
            updateHeroHealthDisplay();
            updateHeroManaDisplay();
        }

        if (currentMonster != null) {
            monsterNameDisplay.setText(currentMonster.getName());
            updateMonsterHealthDisplay();
        }
    }

    /**
     * Updates the progress and color of both hero and monster health bars.
     */
    private void updateHealthBars() {
        if (currentHero != null) {
            double heroHealthRatio = currentHero.getHealthPercentage();
            animateHealthBar(heroHealthBar, heroHealthRatio);
            updateHealthBarColor(heroHealthBar, heroHealthRatio);
        }

        if (currentMonster != null) {
            double monsterHealthRatio = currentMonster.getHealthPercentage();
            animateHealthBar(monsterHealthBar, monsterHealthRatio);
            updateHealthBarColor(monsterHealthBar, monsterHealthRatio);
        }
    }

    /**
     * Animates a health bar to its new progress value.
     *
     * @param healthBar   The ProgressBar to animate.
     * @param targetRatio The target progress value (0.0 to 1.0).
     */
    private void animateHealthBar(ProgressBar healthBar, double targetRatio) {
        var healthAnimation = new Timeline(
                new KeyFrame(Duration.seconds(0.8), new KeyValue(healthBar.progressProperty(), targetRatio))
        );
        healthAnimation.play();
    }

    /**
     * Updates the color of a health bar based on the health ratio.
     *
     * @param healthBar The ProgressBar to update.
     * @param ratio     The current health ratio (0.0 to 1.0).
     */
    private void updateHealthBarColor(ProgressBar healthBar, double ratio) {
        healthBar.getStyleClass().removeAll("health-bar-green", "health-bar-orange", "health-bar-red");

        String colorClass = ratio > 0.6 ? "health-bar-green" :
                ratio > 0.3 ? "health-bar-orange" : "health-bar-red";
        healthBar.getStyleClass().add(colorClass);
    }

    /**
     * Updates the hero's health text display and health bar color.
     */
    private void updateHeroHealthDisplay() {
        if (currentHero != null && heroHealthNumbers != null) {
            heroHealthNumbers.setText(currentHero.getHealthDisplay());
            updateHealthBarColor(heroHealthBar, currentHero.getHealthPercentage());
        }
    }

    /**
     * Updates the hero's mana text display.
     */
    private void updateHeroManaDisplay() {
        if (currentHero != null && heroManaNumbers != null) {
            heroManaNumbers.setText(currentHero.getSpecialMana() + "/4");
        }
    }

    /**
     * Updates the monster's health text display and health bar color.
     */
    private void updateMonsterHealthDisplay() {
        if (currentMonster != null && monsterHealthNumbers != null) {
            monsterHealthNumbers.setText(currentMonster.getHealthDisplay());
            updateHealthBarColor(monsterHealthBar, currentMonster.getHealthPercentage());
        }
    }

    /**
     * Updates the turn indicator visual effect (glow) on the active character's sprite.
     */
    private void updateTurnIndicator() {
        if (playerTurn) {
            heroSprite.setEffect(new javafx.scene.effect.DropShadow(10, Color.CYAN));
            monsterSprite.setEffect(null);
        } else {
            monsterSprite.setEffect(new javafx.scene.effect.DropShadow(10, Color.RED));
            heroSprite.setEffect(null);
        }
    }

    /**
     * Ends combat and triggers victory or defeat scenarios.
     * @param victory True if the player won, false otherwise.
     */
    private void endCombat(boolean victory) {
        endCombat(victory, currentMonster);
    }

    /**
     * Ends the combat and processes the outcome.
     *
     * @param victory           True if the player won, otherwise false.
     * @param monsterForMessage The monster to reference in the victory message.
     */
    private void endCombat(boolean victory, Monster monsterForMessage) {
        endCombat();

        if (victory) {
            handleVictory(monsterForMessage);
        } else {
            handleDefeat();
        }
    }

    /**
     * Handles the victory sequence, including animations and messages.
     * @param monsterForMessage The defeated monster to name in the message.
     */
    private void handleVictory(Monster monsterForMessage) {
        String monsterName = (monsterForMessage != null) ? monsterForMessage.getName() : "The monster";
        addCombatMessage(monsterName + " was defeated!");
        addCombatMessage(currentHero.getName() + " wins the battle!");

        playVictoryAnimation(heroSprite);

        var endDelay = new Timeline(new KeyFrame(AnimationConfig.END_DELAY, _ ->
                getController().resumeCurrentGame(getController().getGameController().getGameUI())
        ));
        endDelay.play();
    }

    /**
     * Handles the defeat sequence, including animations and game over messages.
     */
    private void handleDefeat() {
        addCombatMessage(currentHero.getName() + " was defeated!");
        addCombatMessage("Game Over!");
        playDefeatAnimation(heroSprite);
    }

    /**
     * Adds a message to the combat log with a typing animation.
     *
     * @param message The message string to add.
     */
    private void addCombatMessage(String message) {
        var messageLabel = createMessageLabel(message);
        combatMessages.getChildren().add(messageLabel);

        if (combatMessages.getChildren().size() > ResponsiveConfig.MAX_MESSAGES) {
            combatMessages.getChildren().removeFirst();
        }

        animateTyping(messageLabel, message);
    }

    /**
     * Creates a Label for a combat message.
     *
     * @param message The text of the message.
     * @return A configured Label.
     */
    private Label createMessageLabel(String message) {
        var messageLabel = new Label(message);
        messageLabel.getStyleClass().add("combat-message");
        messageLabel.setWrapText(true);
        bindingManager.bindMessageLabelProperties(messageLabel);
        return messageLabel;
    }

    /**
     * Animates the display of text in a label to simulate typing.
     *
     * @param label    The Label to animate.
     * @param fullText The full text to be "typed" out.
     */
    private void animateTyping(Label label, String fullText) {
        var typing = new Timeline();
        label.setText("");

        for (int i = 0; i <= fullText.length(); i++) {
            final int index = i;
            typing.getKeyFrames().add(
                    new KeyFrame(Duration.millis(index * 25), _ -> label.setText(fullText.substring(0, index)))
            );
        }
        typing.play();
    }

    /**
     * Sets up all responsive property bindings for UI elements.
     */
    private void setupResponsiveBindings() {
        battlefield.prefHeightProperty().bind(bindingManager.getBattlefieldHeightBinding());
        bottomInterface.prefHeightProperty().bind(bindingManager.getInterfaceHeightBinding());

        heroSprite.fitWidthProperty().bind(bindingManager.getHeroSpriteBinding());
        heroSprite.fitHeightProperty().bind(bindingManager.getHeroSpriteBinding());
        monsterSprite.fitWidthProperty().bind(bindingManager.getMonsterSpriteBinding());
        monsterSprite.fitHeightProperty().bind(bindingManager.getMonsterSpriteBinding());

        // Add listeners for platform updates
        setupPlatformUpdateListeners();
        createBattlePlatforms();

        scene.widthProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
    }

    /**
     * Sets up listeners to trigger platform position updates when sprites move.
     */
    private void setupPlatformUpdateListeners() {
        heroSprite.layoutXProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        heroSprite.layoutYProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        heroSprite.translateXProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        heroSprite.translateYProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        monsterSprite.layoutXProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        monsterSprite.layoutYProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        monsterSprite.translateXProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
        monsterSprite.translateYProperty().addListener((obs, oldVal, newVal) -> updatePlatformPositions());
    }

    /**
     * Schedules an update for the battle platforms on the JavaFX application thread.
     */
    private void updatePlatformPositions() {
        if (battlefield != null && heroSprite != null && monsterSprite != null) {
            javafx.application.Platform.runLater(this::createBattlePlatforms);
        }
    }

    /**
     * Creates and positions the glowing platforms underneath the character sprites.
     */
    private void createBattlePlatforms() {
        if (battlefield == null || heroSprite == null || monsterSprite == null) return;

        battlefield.getChildren().removeIf(node ->
                node.getUserData() != null && node.getUserData().equals("platform-layer"));

        var platformLayer = new Pane();
        platformLayer.setUserData("platform-layer");
        platformLayer.setMouseTransparent(true);

        var heroBounds = battlefield.sceneToLocal(heroSprite.localToScene(heroSprite.getBoundsInLocal()));
        var monsterBounds = battlefield.sceneToLocal(monsterSprite.localToScene(monsterSprite.getBoundsInLocal()));

        double heroPlatformX = heroBounds.getCenterX();
        double heroPlatformY = heroBounds.getCenterY() + (heroBounds.getHeight() / 4);

        double monsterPlatformX = monsterBounds.getCenterX();
        double monsterPlatformY = monsterBounds.getCenterY() + (monsterBounds.getHeight() / 4);

        double radiusX = bindingManager.getPlatformRadiusXBinding().getValue().doubleValue();
        double radiusY = bindingManager.getPlatformRadiusYBinding().getValue().doubleValue();

        var heroPlatform = createPlatform(heroPlatformX, heroPlatformY, radiusX * 0.9, radiusY);
        var monsterPlatform = createPlatform(monsterPlatformX, monsterPlatformY, radiusX, radiusY * 1.1);

        platformLayer.getChildren().addAll(heroPlatform, monsterPlatform);
        platformLayer.prefHeightProperty().bind(bindingManager.getBattlefieldHeightBinding());
        battlefield.getChildren().add(0, platformLayer);
    }

    /**
     * Loads and applies the CSS stylesheet for the combat screen.
     *
     * @param scene The scene to apply the stylesheet to.
     */
    private void setupCSS(Scene scene) {
        try {
            String cssPath = "/main/View/css/combat.css";
            var cssUrl = getClass().getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Could not find combat.css at: " + cssPath);
            }
        } catch (Exception e) {
            System.err.println("Could not load combat.css: " + e.getMessage());
        }
    }

    /**
     * Adds the initial welcome messages to the combat log.
     */
    private void addWelcomeMessages() {
        addCombatMessage("A wild " + getMonsterName() + " appeared!");
        addCombatMessage("What will " + getHeroName() + " do?");
    }

    /**
     * Checks if the player is able to perform an action.
     *
     * @return True if the player can act, false otherwise.
     */
    private boolean canPlayerAct() {
        return !playerTurn || !combatActive || currentMonster == null;
    }

    /**
     * Refreshes the state of the current monster from the game model.
     */
    private void refreshMonsterState() {
        var currentRoom = getController().getDungeon().getRoom(currentHero.getPosition());
        var monsters = currentRoom.getMonsters();
        currentMonster = monsters.isEmpty() ? null : monsters.get(0);
    }

    /**
     * Gets the current hero's name.
     *
     * @return The hero's name, or "Hero" as a fallback.
     */
    private String getHeroName() {
        return currentHero != null ? currentHero.getName() : "Hero";
    }

    /**
     * Gets the display name of the current hero's class.
     *
     * @return The hero's class name, or "Unknown" as a fallback.
     */
    private String getHeroClass() {
        return currentHero != null ? currentHero.getType().getDisplayName() : "Unknown";
    }

    /**
     * Gets the current monster's name.
     *
     * @return The monster's name, or "Unknown Monster" as a fallback.
     */
    private String getMonsterName() {
        return currentMonster != null ? currentMonster.getName() : "Unknown Monster";
    }

    /**
     * Gets the monster's level description (Normal or Elite).
     *
     * @return The monster's level string.
     */
    private String getMonsterLevel() {
        return "Lv. " + (currentMonster != null && currentMonster.isElite() ? "Elite" : "Normal");
    }

    /**
     * Gets the name of the hero's special attack.
     *
     * @return The special attack name in uppercase, or "SPECIAL" as a fallback.
     */
    private String getSpecialAttackName() {
        return currentHero != null ?
                currentHero.getType().getSpecialAttackName().toUpperCase() : "SPECIAL";
    }

    /**
     * Checks if the combat is currently active.
     *
     * @return True if combat is active, false otherwise.
     */
    public boolean isCombatActive() {
        return combatActive;
    }

    /**
     * Checks if it is currently the player's turn.
     *
     * @return True if it is the player's turn, false otherwise.
     */
    public boolean isPlayerTurn() {
        return playerTurn;
    }

    /**
     * Resets the combat state to its initial values.
     */
    private void resetCombat() {
        playerTurn = true;
        combatActive = true;
    }

    /**
     * Sets the turn state to indicate the monster's turn is next.
     */
    private void startPlayerTurn() {
        playerTurn = false;
    }

    /**
     * Sets the turn state to indicate the player's turn is next.
     */
    private void startMonsterTurn() {
        playerTurn = true;
    }

    /**
     * Sets the combat state to inactive.
     */
    private void endCombat() {
        combatActive = false;
        playerTurn = false;
    }

    /**
     * Plays the entrance animation for hero and monster sprites.
     *
     * @param heroSprite    The hero's ImageView.
     * @param monsterSprite The monster's ImageView.
     * @param onComplete    A Runnable to execute when the animation finishes.
     */
    private void playEntranceAnimation(ImageView heroSprite, ImageView monsterSprite, Runnable onComplete) {
        var heroEntrance = new TranslateTransition(AnimationConfig.ENTRANCE_DURATION, heroSprite);
        heroEntrance.setFromX(-400);
        heroEntrance.setToX(0);

        var monsterEntrance = new TranslateTransition(AnimationConfig.ENTRANCE_DURATION, monsterSprite);
        monsterEntrance.setFromX(400);
        monsterEntrance.setToX(0);

        var entrance = new ParallelTransition(heroEntrance, monsterEntrance);
        entrance.setOnFinished(e -> onComplete.run());
        entrance.play();
    }

    /**
     * Plays a standard attack animation.
     *
     * @param attacker   The attacking character's sprite.
     * @param target     The target character's sprite.
     * @param onComplete A Runnable to execute after the animation.
     */
    private void playAttackAnimation(ImageView attacker, ImageView target, Runnable onComplete) {
        var attack = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(attacker.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(200), new KeyValue(attacker.translateXProperty(), 20)),
                new KeyFrame(Duration.millis(250), new KeyValue(attacker.translateXProperty(), 15)),
                new KeyFrame(Duration.millis(300), new KeyValue(attacker.translateXProperty(), 25)),
                new KeyFrame(Duration.millis(350), new KeyValue(attacker.translateXProperty(), 15)),
                new KeyFrame(AnimationConfig.ATTACK_DURATION, new KeyValue(attacker.translateXProperty(), 0))
        );

        attack.setOnFinished(e -> playFlashEffect(target, onComplete));
        attack.play();
    }

    /**
     * Plays a special attack animation.
     *
     * @param attacker   The attacking character's sprite.
     * @param target     The target character's sprite.
     * @param onComplete A Runnable to execute after the animation.
     */
    private void playSpecialAttackAnimation(ImageView attacker, ImageView target, Runnable onComplete) {
        var spin = new RotateTransition(AnimationConfig.SPECIAL_DURATION, attacker);
        spin.setByAngle(360);

        var grow = new ScaleTransition(AnimationConfig.SPECIAL_DURATION, attacker);
        grow.setToX(1.3);
        grow.setToY(1.3);
        grow.setAutoReverse(true);
        grow.setCycleCount(2);

        var special = new ParallelTransition(spin, grow);
        special.setOnFinished(e -> playIntenseFlashEffect(target, onComplete));
        special.play();
    }

    /**
     * Plays the monster's attack animation.
     *
     * @param attacker   The monster's sprite.
     * @param target     The hero's sprite.
     * @param onComplete A Runnable to execute after the animation.
     */
    private void playMonsterAttackAnimation(ImageView attacker, ImageView target, Runnable onComplete) {
        var attack = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(attacker.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(300), new KeyValue(attacker.translateXProperty(), -40)),
                new KeyFrame(Duration.millis(600), new KeyValue(attacker.translateXProperty(), 0))
        );

        attack.setOnFinished(e -> playFlashEffect(target, onComplete));
        attack.play();
    }

    /**
     * Plays a victory animation for the given sprite.
     *
     * @param sprite The sprite to animate.
     */
    private void playVictoryAnimation(ImageView sprite) {
        var victory = new RotateTransition(Duration.seconds(1), sprite);
        victory.setByAngle(360);
        victory.play();
    }

    /**
     * Plays a defeat animation for the given sprite.
     *
     * @param sprite The sprite to animate.
     */
    private void playDefeatAnimation(ImageView sprite) {
        var defeat = new FadeTransition(Duration.seconds(1), sprite);
        defeat.setToValue(0.3);
        defeat.play();
    }

    /**
     * Plays a flashing effect on a target sprite to indicate damage.
     *
     * @param target     The sprite to flash.
     * @param onComplete A Runnable to execute after the animation.
     */
    private void playFlashEffect(ImageView target, Runnable onComplete) {
        var flash = new FadeTransition(AnimationConfig.FLASH_DURATION, target);
        flash.setFromValue(1.0);
        flash.setToValue(0.3);
        flash.setCycleCount(4);
        flash.setAutoReverse(true);
        flash.setOnFinished(e -> onComplete.run());
        flash.play();
    }

    /**
     * Plays a more intense flashing effect for special attacks.
     *
     * @param target     The sprite to flash.
     * @param onComplete A Runnable to execute after the animation.
     */
    private void playIntenseFlashEffect(ImageView target, Runnable onComplete) {
        var flash = new FadeTransition(Duration.millis(80), target);
        flash.setFromValue(1.0);
        flash.setToValue(0.1);
        flash.setCycleCount(4);
        flash.setAutoReverse(true);
        flash.setOnFinished(e -> onComplete.run());
        flash.play();
    }

    /**
     * Creates an ImageView for the hero's sprite.
     *
     * @param hero The hero data object.
     * @return An ImageView containing the hero's sprite.
     */
    private static ImageView createHeroSprite(Hero hero) {
        var sprite = new ImageView();
        sprite.getStyleClass().add("hero-sprite");

        try {
            String spritePath = getHeroSpritePath(hero);
            var heroImage = new Image(Objects.requireNonNull(CombatScreen.class.getResourceAsStream(spritePath)));
            sprite.setImage(heroImage);
        } catch (Exception e) {
            System.err.println("Could not load hero sprite, using fallback");
            sprite.setStyle("-fx-background-color: steelblue; -fx-background-radius: 10px;");
        }

        return sprite;
    }

    /**
     * Creates an ImageView for the monster's sprite.
     *
     * @param monster The monster data object.
     * @return An ImageView containing the monster's sprite.
     */
    private static ImageView createMonsterSprite(Monster monster) {
        var sprite = new ImageView();
        sprite.getStyleClass().add("monster-sprite");

        try {
            String spritePath = getMonsterSpritePath(monster);
            var monsterImage = new Image(Objects.requireNonNull(CombatScreen.class.getResourceAsStream(spritePath)));
            sprite.setImage(monsterImage);
        } catch (Exception e) {
            System.err.println("Could not load monster sprite, using fallback");
            sprite.setStyle("-fx-background-color: darkred; -fx-background-radius: 10px;");
        }

        return sprite;
    }

    /**
     * Gets the resource path for the hero's sprite image.
     *
     * @param hero The hero data object.
     * @return The string path to the sprite image.
     */
    private static String getHeroSpritePath(Hero hero) {
        if (hero == null) return "/sprites/heroes/default.png";

        return switch (hero.getType()) {
            case WARRIOR -> "/sprites/heroes/warrior.png";
            case PRIESTESS -> "/sprites/heroes/priestess.png";
            case THIEF -> "/sprites/heroes/thief.png";
        };
    }

    /**
     * Gets the resource path for the monster's sprite image.
     *
     * @param monster The monster data object.
     * @return The string path to the sprite image.
     */
    private static String getMonsterSpritePath(Monster monster) {
        if (monster == null) return "/sprites/monsters/default.png";

        return "/sprites/monsters/" +
                monster.getType().getName().toLowerCase().replace(" ", "_") + ".png";
    }

    /**
     * Creates a glowing platform effect.
     *
     * @param centerX The center X coordinate of the platform.
     * @param centerY The center Y coordinate of the platform.
     * @param radiusX The horizontal radius of the platform.
     * @param radiusY The vertical radius of the platform.
     * @return A Group containing the platform's visual components.
     */
    private static Group createPlatform(double centerX, double centerY, double radiusX, double radiusY) {
        var platformGroup = new Group();

        var baseEllipse = new Ellipse(centerX, centerY, radiusX, radiusY);
        baseEllipse.setFill(Color.rgb(150, 150, 220, 0.3));
        baseEllipse.setStroke(Color.rgb(200, 200, 255, 0.7));
        baseEllipse.setStrokeWidth(2);

        var bloomEffect = new Bloom();
        bloomEffect.setThreshold(0.6);
        baseEllipse.setEffect(bloomEffect);

        var innerAccent = new Ellipse(centerX, centerY, radiusX * 0.7, radiusY * 0.7);
        innerAccent.setFill(Color.TRANSPARENT);
        innerAccent.setStroke(Color.rgb(200, 200, 255, 0.4));
        innerAccent.setStrokeWidth(1.5);
        innerAccent.getStrokeDashArray().addAll(5d, 5d);

        platformGroup.getChildren().addAll(baseEllipse, innerAccent);
        return platformGroup;
    }

    /**
     * Manages all responsive bindings for UI elements, recalculating sizes and positions
     * when the scene dimensions change.
     */
    private static class ResponsiveBindingManager {
        private NumberBinding scaleBinding;
        private NumberBinding paddingBinding;
        private NumberBinding spacingBinding;
        private NumberBinding heroSpriteBinding;
        private NumberBinding monsterSpriteBinding;
        private NumberBinding healthBarWidthBinding;
        private NumberBinding healthBarHeightBinding;
        private NumberBinding nameFontSizeBinding;
        private NumberBinding subtitleFontSizeBinding;
        private NumberBinding buttonFontSizeBinding;
        private NumberBinding messageFontSizeBinding;
        private NumberBinding buttonWidthBinding;
        private NumberBinding buttonHeightBinding;
        private NumberBinding platformRadiusXBinding;
        private NumberBinding platformRadiusYBinding;
        private NumberBinding battlefieldHeightBinding;
        private NumberBinding interfaceHeightBinding;
        private NumberBinding messageHeightBinding;
        private NumberBinding heroSpacingBinding;

        /**
         * Initializes all property bindings based on the provided scene.
         *
         * @param scene The main scene whose properties will be observed.
         */
        void initializeBindings(Scene scene) {
            scaleBinding = Bindings.createDoubleBinding(() -> {
                double widthScale = scene.getWidth() / ResponsiveConfig.BASE_WIDTH;
                double heightScale = scene.getHeight() / ResponsiveConfig.BASE_HEIGHT;
                double scale = Math.min(widthScale, heightScale);
                return Math.max(ResponsiveConfig.MIN_SCALE, Math.min(ResponsiveConfig.MAX_SCALE, scale));
            }, scene.widthProperty(), scene.heightProperty());

            paddingBinding = Bindings.createDoubleBinding(() ->
                            Math.max(10.0, Math.min(scene.getWidth(), scene.getHeight()) * ResponsiveConfig.PADDING_RATIO),
                    scene.widthProperty(), scene.heightProperty());

            spacingBinding = Bindings.createDoubleBinding(() ->
                            Math.max(15.0, Math.min(scene.getWidth(), scene.getHeight()) * ResponsiveConfig.SPACING_RATIO),
                    scene.widthProperty(), scene.heightProperty());

            heroSpacingBinding = Bindings.createDoubleBinding(() ->
                            Math.max(150.0, scene.getWidth() * ResponsiveConfig.HERO_SPACING_RATIO),
                    scene.widthProperty());

            battlefieldHeightBinding = Bindings.createDoubleBinding(() ->
                            scene.getHeight() * ResponsiveConfig.BATTLEFIELD_HEIGHT_RATIO,
                    scene.heightProperty());

            interfaceHeightBinding = Bindings.createDoubleBinding(() ->
                            scene.getHeight() * ResponsiveConfig.INTERFACE_HEIGHT_RATIO,
                    scene.heightProperty());

            messageHeightBinding = Bindings.createDoubleBinding(() ->
                            scene.getHeight() * ResponsiveConfig.MESSAGE_HEIGHT_RATIO,
                    scene.heightProperty());

            heroSpriteBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getWidth() * ResponsiveConfig.HERO_SPRITE_SIZE_RATIO;
                return Math.max(ResponsiveConfig.HERO_SPRITE_MIN_SIZE,
                        Math.min(ResponsiveConfig.HERO_SPRITE_MAX_SIZE, calculated));
            }, scene.widthProperty());

            monsterSpriteBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getWidth() * ResponsiveConfig.MONSTER_SPRITE_SIZE_RATIO;
                return Math.max(ResponsiveConfig.MONSTER_SPRITE_MIN_SIZE,
                        Math.min(ResponsiveConfig.MONSTER_SPRITE_MAX_SIZE, calculated));
            }, scene.widthProperty());

            healthBarWidthBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getWidth() * ResponsiveConfig.HEALTH_BAR_WIDTH_RATIO;
                return Math.max(ResponsiveConfig.HEALTH_BAR_MIN_WIDTH,
                        Math.min(ResponsiveConfig.HEALTH_BAR_MAX_WIDTH, calculated));
            }, scene.widthProperty());

            healthBarHeightBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getHeight() * ResponsiveConfig.HEALTH_BAR_HEIGHT_RATIO;
                return Math.max(ResponsiveConfig.HEALTH_BAR_MIN_HEIGHT,
                        Math.min(ResponsiveConfig.HEALTH_BAR_MAX_HEIGHT, calculated));
            }, scene.heightProperty());

            nameFontSizeBinding = Bindings.createDoubleBinding(() ->
                            Math.max(14.0, scene.getHeight() * ResponsiveConfig.NAME_FONT_SIZE_RATIO),
                    scene.heightProperty());

            subtitleFontSizeBinding = Bindings.createDoubleBinding(() ->
                            Math.max(10.0, scene.getHeight() * ResponsiveConfig.SUBTITLE_FONT_SIZE_RATIO),
                    scene.heightProperty());

            buttonFontSizeBinding = Bindings.createDoubleBinding(() ->
                            Math.max(12.0, scene.getHeight() * ResponsiveConfig.BUTTON_FONT_SIZE_RATIO),
                    scene.heightProperty());

            messageFontSizeBinding = Bindings.createDoubleBinding(() ->
                            Math.max(11.0, scene.getHeight() * ResponsiveConfig.MESSAGE_FONT_SIZE_RATIO),
                    scene.heightProperty());

            buttonWidthBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getWidth() * ResponsiveConfig.BUTTON_WIDTH_RATIO;
                return Math.max(ResponsiveConfig.BUTTON_MIN_WIDTH,
                        Math.min(ResponsiveConfig.BUTTON_MAX_WIDTH, calculated));
            }, scene.widthProperty());

            buttonHeightBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getHeight() * ResponsiveConfig.BUTTON_HEIGHT_RATIO;
                return Math.max(ResponsiveConfig.BUTTON_MIN_HEIGHT,
                        Math.min(ResponsiveConfig.BUTTON_MAX_HEIGHT, calculated));
            }, scene.heightProperty());

            platformRadiusXBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getWidth() * ResponsiveConfig.PLATFORM_RADIUS_X_RATIO;
                return Math.max(ResponsiveConfig.PLATFORM_MIN_RADIUS_X,
                        Math.min(ResponsiveConfig.PLATFORM_MAX_RADIUS_X, calculated));
            }, scene.widthProperty());

            platformRadiusYBinding = Bindings.createDoubleBinding(() -> {
                double calculated = scene.getHeight() * ResponsiveConfig.PLATFORM_RADIUS_Y_RATIO;
                return Math.max(ResponsiveConfig.PLATFORM_MIN_RADIUS_Y,
                        Math.min(ResponsiveConfig.PLATFORM_MAX_RADIUS_Y, calculated));
            }, scene.heightProperty());
        }

        /**
         * Creates a font binding for primary name labels.
         * @return An ObservableValue wrapping the dynamically sized Font.
         */
        ObservableValue<? extends Font> createNameFontBinding() {
            return Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", FontWeight.BOLD, nameFontSizeBinding.getValue().doubleValue()),
                    nameFontSizeBinding);
        }

        /**
         * Creates a font binding for subtitle labels.
         * @return An ObservableValue wrapping the dynamically sized Font.
         */
        ObservableValue<? extends Font> createSubtitleFontBinding() {
            return Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", subtitleFontSizeBinding.getValue().doubleValue()),
                    subtitleFontSizeBinding);
        }

        /**
         * Creates a font binding for button text.
         * @return An ObjectBinding wrapping the dynamically sized Font.
         */
        ObjectBinding<Font> createButtonFontBinding() {
            return Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", FontWeight.BOLD, buttonFontSizeBinding.getValue().doubleValue()),
                    buttonFontSizeBinding);
        }

        /**
         * Creates a font binding for combat message text.
         * @return An ObjectBinding wrapping the dynamically sized Font.
         */
        ObjectBinding<Font> createMessageFontBinding() {
            return Bindings.createObjectBinding(() ->
                            Font.font("PixelFont", messageFontSizeBinding.getValue().doubleValue()),
                    messageFontSizeBinding);
        }

        /**
         * Binds padding and spacing properties for the main battle area.
         * @param battleArea The HBox to bind properties to.
         */
        void bindBattleAreaProperties(HBox battleArea) {
            battleArea.paddingProperty().bind(Bindings.createObjectBinding(() ->
                            new Insets(paddingBinding.getValue().doubleValue()),
                    paddingBinding));
            battleArea.spacingProperty().bind(heroSpacingBinding);
        }

        /**
         * Binds positioning properties for the hero and monster sides.
         * @param heroSide    The hero's VBox container.
         * @param monsterSide The monster's VBox container.
         */
        void bindSidePositions(VBox heroSide, VBox monsterSide) {
            heroSide.translateYProperty().bind(paddingBinding.multiply(-0.5));
            heroSide.translateXProperty().bind(paddingBinding.multiply(-1.5));
            monsterSide.translateYProperty().bind(paddingBinding.multiply(-2));
            monsterSide.translateXProperty().bind(paddingBinding.multiply(-2));
        }

        /**
         * Binds size properties for a health bar.
         * @param healthBar The ProgressBar to bind.
         */
        void bindHealthBarProperties(ProgressBar healthBar) {
            healthBar.prefWidthProperty().bind(healthBarWidthBinding);
            healthBar.minWidthProperty().bind(healthBarWidthBinding);
            healthBar.prefHeightProperty().bind(healthBarHeightBinding);
            healthBar.minHeightProperty().bind(healthBarHeightBinding);
        }

        /**
         * Binds padding for the bottom interface container.
         * @param bottomInterface The VBox to bind.
         */
        void bindBottomInterfaceProperties(VBox bottomInterface) {
            bottomInterface.paddingProperty().bind(Bindings.createObjectBinding(() ->
                            new Insets(paddingBinding.getValue().doubleValue()),
                    paddingBinding));
        }

        /**
         * Binds the preferred height for the message area.
         * @param messageArea The VBox to bind.
         */
        void bindMessageAreaProperties(VBox messageArea) {
            messageArea.prefHeightProperty().bind(messageHeightBinding);
        }

        /**
         * Binds spacing and padding for the button area container.
         * @param buttonBox The HBox to bind.
         */
        void bindButtonAreaProperties(HBox buttonBox) {
            buttonBox.spacingProperty().bind(spacingBinding);
            buttonBox.paddingProperty().bind(Bindings.createObjectBinding(() ->
                            new Insets(paddingBinding.getValue().doubleValue(), 0, 0, 0),
                    paddingBinding));
        }

        /**
         * Binds size and font properties for a combat button.
         * @param button The Button to bind.
         */
        void bindButtonProperties(Button button) {
            button.fontProperty().bind(createButtonFontBinding());
            button.minWidthProperty().bind(buttonWidthBinding);
            button.setMaxWidth(Double.MAX_VALUE);
            button.prefHeightProperty().bind(buttonHeightBinding);
        }

        /**
         * Binds font and max width properties for a message label.
         * @param messageLabel The Label to bind.
         */
        void bindMessageLabelProperties(Label messageLabel) {
            messageLabel.fontProperty().bind(createMessageFontBinding());
            messageLabel.maxWidthProperty().bind(scaleBinding.multiply(800)); // Scale with scene
        }

        // Getter methods
        /** @return The spacing binding. */
        NumberBinding getSpacingBinding() { return spacingBinding; }
        /** @return The battlefield height binding. */
        NumberBinding getBattlefieldHeightBinding() { return battlefieldHeightBinding; }
        /** @return The interface height binding. */
        NumberBinding getInterfaceHeightBinding() { return interfaceHeightBinding; }
        /** @return The hero sprite size binding. */
        NumberBinding getHeroSpriteBinding() { return heroSpriteBinding; }
        /** @return The monster sprite size binding. */
        NumberBinding getMonsterSpriteBinding() { return monsterSpriteBinding; }
        /** @return The platform X-radius binding. */
        NumberBinding getPlatformRadiusXBinding() { return platformRadiusXBinding; }
        /** @return The platform Y-radius binding. */
        NumberBinding getPlatformRadiusYBinding() { return platformRadiusYBinding; }
    }
}