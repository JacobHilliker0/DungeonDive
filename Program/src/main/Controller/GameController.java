package main.Controller;

import main.Model.Model;
import main.Model.character.Hero;
import main.Model.character.Monster;
import main.Model.dungeon.Room;
import main.Model.element.Item;
import main.Model.element.Pillar;
import main.Model.util.Direction;
import main.View.GameUI;
import main.Controller.StateController.GameState;

import main.Model.util.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * control main game logic
 */
public class GameController {
    private final Model myGameModel;
    private final GameUI myGameUI;
    private final StateController myStateController;

    private int selectedInventoryIndex = 0;

    /**
     * Constructor for GameController.
     *
     * @param theGameModel game model containing game data
     * @param theGameUI  game UI displaying game state
     * @param theStateController state controller tracking game state
     */
    public GameController(final Model theGameModel, final GameUI theGameUI, final StateController theStateController) {
        if (theGameModel == null) {
            throw new IllegalArgumentException("Game model cannot be null for GameController.");
        }
        if (theGameUI == null) {
            throw new IllegalArgumentException("Game UI cannot be null for GameController.");
        }
        if (theStateController == null) {
            throw new IllegalArgumentException("State controller cannot be null for GameController.");
        }

        this.myGameModel = theGameModel;
        this.myGameUI = theGameUI;
        this.myStateController = theStateController;
        System.out.println("GameController initialized with model, UI, and state controller");
    }

    public void startPlayerMovement(final Direction theDirection) {
        if (!myStateController.isInState(GameState.EXPLORING)) {
            return;
        }

        Hero player = myGameModel.getPlayer();
        player.startMoving(theDirection);
    }

    public void stopPlayerMovement() {
        Hero player = myGameModel.getPlayer();
        if (player != null) {
            player.stopMoving();
        }
    }

    /**
     * Moves the player north if possible.
     */
    public void movePlayerNorth() {
        if (!myStateController.isInState(GameState.EXPLORING)) {
            return; // Can only move during exploration
        }

        Hero player = myGameModel.getPlayer();
        Point currentPos = player.getPosition();
        Room currentRoom = myGameModel.getDungeon().getRoom(currentPos);

        // Check if there's a door to the north
        if (currentRoom.hasNorthDoor()) {
            // Move player to the north room
            Point newPos = new Point(currentPos.getX(), currentPos.getY() - 1);
            player.setPosition(newPos);

            // Enter the new room and handle room effects
            Room newRoom = myGameModel.getDungeon().getRoom(newPos);
            enterRoom(newRoom);

            // Update UI
            myGameUI.updatePlayerPosition();
            System.out.println("Player moved north to " + newPos);
        } else {
            System.out.println("Cannot move north - no door in that direction");
        }

    }

    /**
     * Moves the player south if possible.
     */
    public void movePlayerSouth() {
        if (!myStateController.isInState(GameState.EXPLORING)) {
            return; // Can only move during exploration
        }

        Hero player = myGameModel.getPlayer();
        Point currentPos = player.getPosition();
        Room currentRoom = myGameModel.getDungeon().getRoom(currentPos);

        // Check if there's a door to the south
        if (currentRoom.hasSouthDoor()) {
            // Move player to the south room
            Point newPos = new Point(currentPos.getX(), currentPos.getY() + 1);
            player.setPosition(newPos);

            // Enter the new room and handle room effects
            Room newRoom = myGameModel.getDungeon().getRoom(newPos);
            enterRoom(newRoom);

            // Update UI
            myGameUI.updatePlayerPosition();
            System.out.println("Player moved south to " + newPos);
        } else {
            System.out.println("Cannot move south - no door in that direction");
        }
    }

    /**
     * Moves the player east if possible.
     */
    public void movePlayerEast() {
        if (!myStateController.isInState(GameState.EXPLORING)) {
            return; // Can only move during exploration
        }

        Hero player = myGameModel.getPlayer();
        Point currentPos = player.getPosition();
        Room currentRoom = myGameModel.getDungeon().getRoom(currentPos);

        // Check if there's a door to the east
        if (currentRoom.hasEastDoor()) {
            // Move player to the east room
            Point newPos = new Point(currentPos.getX()+1, currentPos.getY());
            player.setPosition(newPos);

            // Enter the new room and handle room effects
            Room newRoom = myGameModel.getDungeon().getRoom(newPos);
            enterRoom(newRoom);

            // Update UI
            myGameUI.updatePlayerPosition();
            System.out.println("Player moved east to " + newPos);
        } else {
            System.out.println("Cannot move east - no door in that direction");
        }
    }

    /**
     * Moves the player west if possible.
     */
    public void movePlayerWest() {
        if (!myStateController.isInState(GameState.EXPLORING)) {
            return; // Can only move during exploration
        }

        Hero player = myGameModel.getPlayer();
        Point currentPos = player.getPosition();
        Room currentRoom = myGameModel.getDungeon().getRoom(currentPos);

        // Check if there's a door to the west
        if (currentRoom.hasWestDoor()) {
            // Move player to the west room
            Point newPos = new Point(currentPos.getX() - 1, currentPos.getY());
            player.setPosition(newPos);

            // Enter the new room and handle room effects
            Room newRoom = myGameModel.getDungeon().getRoom(newPos);
            enterRoom(newRoom);

            // Update UI
            myGameUI.updatePlayerPosition();
            System.out.println("Player moved west to " + newPos);
        } else {
            System.out.println("Cannot move west - no door in that direction");
        }
    }

    /**
     * Handles room entry logic.
     *
     * @param room The room being entered
     */
    private void enterRoom(final Room room) {
        // Mark room as visited
        room.setVisited(true);

        // Check for monsters
        if (!room.getMonsters().isEmpty()) {
            // Enter combat mode
            myStateController.changeState(GameState.COMBAT);
            myGameUI.showCombatScreen(room.getMonsters());
            System.out.println("Entered combat with " + room.getMonsters().size() + " monsters");
            return;
        }

        // Check for chest
        if (room.hasChest()) {
            System.out.println("Room contains a chest that can be opened with 'E'");
            myGameUI.showChestPrompt();
        }

        // Check for pillar
        if (room.hasPillar()) {
            System.out.println("Room contains a pillar of OO!");
            myGameUI.showPillarFound(room.getPillar());
        }

        // Check for trap
        if (room.hasTrap()) {
            activateTrap(room);
        }

        // Update room description in UI
        myGameUI.updateRoomDescription(room);
    }

    /**
     * Activates a trap in the room and applies its effects.
     *
     * @param room The room containing the trap
     */
    private void activateTrap(final Room room) {
        Hero player = myGameModel.getPlayer();
        int damage = room.getTrap().getDamage();

        // Apply trap damage to player
        player.takeDamage(damage);
        System.out.println("Player triggered a trap and took " + damage + " damage!");

        // Update UI to show trap effect
        myGameUI.showTrapEffect(room.getTrap());

        // Check if player died from trap
        checkPlayerStatus();
    }

    /**
     *activates pillar in room
     *
     * @param room The room containing the pillar
     */
    private void activatePillar(final Room room) {
        if (room.hasPillar() && !room.getPillar().isActivated()) {
            Pillar pillar = room.getPillar();
            boolean activated = myGameModel.getPlayer().activatePillar(pillar);

            if (activated) {
                //update UI to show pillar activation and stat changes
                myGameUI.showPillarActivated(pillar);
                myGameUI.updatePlayerStats();
                myGameModel.getDungeon().recordPillarActivation();

                //check if all pillars activated (win condition)
                checkWinCondition();
            }
        }
    }

    /**
     * Checks if the player has met the win condition (activated all pillars).
     */
    private void checkWinCondition() {
        if (myGameModel.getPlayer().hasActivatedAllPillars()) {
            System.out.println("Player has activated all pillars! Victory!");
            myStateController.changeState(GameState.VICTORY);
            myGameUI.showVictoryScreen();
        }
    }

    /**
     * Checks player's health status and handles death if necessary.
     */
    private void checkPlayerStatus() {
        if (myGameModel.getPlayer().getHealth() <= 0) {
            System.out.println("Player has died!");
            myStateController.changeState(GameState.GAME_OVER);
            myGameUI.showGameOverScreen();
        }
    }

    /**
     *handles player's attempt to interact with objects in current room
     */
    public void interact() {
        if (!myStateController.isInState(GameState.EXPLORING)) {
            return; //can only interact during exploration
        }

        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());

        // Check for chest interaction
        if (currentRoom.hasChest()) {
            myStateController.changeState(GameState.CHEST);
            myGameUI.showChestInteraction();
            System.out.println("Interacting with chest");
        }
        // Check for pillar interaction
        else if (currentRoom.hasPillar() && !currentRoom.getPillar().isActivated()) {
            activatePillar(currentRoom);
            System.out.println("Activated pillar in room");
        }
        // Check for items on the ground
        else if (!currentRoom.getItems().isEmpty()) {
            collectItems(currentRoom);
        }
        else {
            System.out.println("Nothing to interact with in this room");
        }
    }

    /**
     * Collects all items from the current room.
     *
     * @param room The room containing items
     */
    private void collectItems(final Room room) {
        List<Item> items = room.getItems();
        if (!items.isEmpty()) {
            for (Item item : items) {
                myGameModel.getPlayer().addItem(item);
                System.out.println("Collected item: " + item.getName());
            }

            // Clear items from room
            room.clearItems();

            // Update UI
            myGameUI.updateInventory();
            myGameUI.showItemCollectionMessage(items);
        }
    }

    /**
     * Opens the player's inventory.
     */
    public void openInventory() {
        selectedInventoryIndex = 0; // Reset selection to first item
        myStateController.changeState(GameState.INVENTORY);
        myGameUI.showInventoryScreen();
        System.out.println("Opened inventory");
    }

    /**
     * Closes the inventory and returns to previous state.
     */
    public void closeInventory() {
        // Return to previous state (usually EXPLORING or COMBAT)
        if (myGameModel.getDungeon().getRoom(myGameModel.getPlayer().getPosition()).getMonsters().isEmpty()) {
            myStateController.changeState(GameState.EXPLORING);
        } else {
            myStateController.changeState(GameState.COMBAT);
        }

        myGameUI.hideInventoryScreen();
        System.out.println("Closed inventory");
    }

    /**
     * Scrolls the inventory selection up.
     */
    public void scrollInventoryUp() {
        if (!myStateController.isInState(GameState.INVENTORY)) {
            return;
        }

        List<Item> inventory = myGameModel.getPlayer().getInventory();
        if (inventory.isEmpty()) {
            return;
        }

        selectedInventoryIndex--;
        if (selectedInventoryIndex < 0) {
            selectedInventoryIndex = inventory.size() - 1; // Wrap to bottom
        }

        myGameUI.updateInventorySelection(selectedInventoryIndex);
        System.out.println("Selected inventory item: " + inventory.get(selectedInventoryIndex).getName());
    }

    /**
     *scrolls inventory selection down
     */
    public void scrollInventoryDown() {
        if (!myStateController.isInState(GameState.INVENTORY)) {
            return;
        }

        List<Item> inventory = myGameModel.getPlayer().getInventory();
        if (inventory.isEmpty()) {
            return;
        }

        selectedInventoryIndex++;
        if (selectedInventoryIndex >= inventory.size()) {
            selectedInventoryIndex = 0; // Wrap to top
        }

        myGameUI.updateInventorySelection(selectedInventoryIndex);
        System.out.println("Selected inventory item: " + inventory.get(selectedInventoryIndex).getName());
    }

    /**
     *uses the currently selected item in the inventory
     */
    public void useSelectedItem() {
        if (!myStateController.isInState(GameState.INVENTORY)) {
            return;
        }

        List<Item> inventory = myGameModel.getPlayer().getInventory();
        if (inventory.isEmpty() || selectedInventoryIndex >= inventory.size()) {
            System.out.println("No item selected or inventory is empty");
            return;
        }

        Item selectedItem = inventory.get(selectedInventoryIndex);

        // Call the void useItem method
        myGameModel.getPlayer().useItem(selectedItem);

        // Since we can't check the return value, we'll assume the item was used successfully
        System.out.println("Used item: " + selectedItem.getName());

        // Update UI
        myGameUI.updateInventory();
        myGameUI.updatePlayerStats();

        // If in combat, update monster display after using item
        if (myStateController.getCurrentState() == GameState.COMBAT) {
            Room currentRoom = myGameModel.getDungeon().getRoom(myGameModel.getPlayer().getPosition());
            myGameUI.updateCombatScreen(currentRoom.getMonsters());
        }
    }

    /**
     *performs regular attack against monsters in current room
     */
    public void playerAttack() {
        if (!myStateController.isInState(GameState.COMBAT)) {
            return; // Can only attack during combat
        }

        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());
        List<Monster> monsters = currentRoom.getMonsters();

        if (monsters.isEmpty()) {
            endCombat();
            return;
        }

        // Get monster as target
        Monster target = monsters.get(0);

        // Call the attack method with target monster as argument
        int damage = player.attack(target);

        // takeDamage() is already called in attack(), it was causing double hits.
        // I'll just keep it as comment for now.
        //target.takeDamage(damage);
        System.out.println("Player attacked " + target.getName() + " for " + damage + " damage!");
        myGameUI.getCombatScreen().addGameMessage("Player attacked " + target.getName() +
                " for " + damage + " damage!");
        myGameUI.getCombatScreen().addGameMessage(target.getName() + " health: " + target.getHealth() +
                "/" + target.getMaxHealth());

        //check if monster is defeated
        if (target.getHealth() <= 0) {
            System.out.println("Defeated " + target.getName() + "!");
            currentRoom.removeMonster(target);

            // Check if all monsters are defeated
            if (monsters.isEmpty()) {
                endCombat();
                return;
            }
        }

        // Monsters' turn to attack (only if combat continues)
        if (myStateController.isInState(GameState.COMBAT)) {
            monsterAttacks();
            myGameUI.updateCombatScreen(currentRoom.getMonsters());
        }
    }


    /**
     * Performs a special attack against monsters in the current room.
     */
    public void playerSpecialAttack() {
        if (!myStateController.isInState(GameState.COMBAT)) {
            return; // Can only attack during combat
        }

        Hero player = myGameModel.getPlayer();

        // Check if player has enough mana/energy for special attack
        if (!player.canUseSpecialAttack()) {
            System.out.println("Not enough mana/energy for special attack!");
            myGameUI.showNotEnoughManaMessage();
            return;
        }

        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());
        List<Monster> monsters = currentRoom.getMonsters();

        if (currentRoom.getMonsters().isEmpty()) {
            endCombat();
            return;
        }

        Monster target = monsters.get(0);

        // Perform special attack
        int damage = player.specialAttack();


        target.takeDamage(damage);
        System.out.println("Player used special attack on " + target.getName() + " for " + damage + " damage!");
        myGameUI.getCombatScreen().addGameMessage("Player used special attack on " + target.getName() + " for " +
                damage + " damage!");
        myGameUI.getCombatScreen().addGameMessage(target.getName() + " health: " + target.getHealth() + "/" +
                target.getMaxHealth());

        // Check if monster is defeated
        if (target.getHealth() <= 0) {
            System.out.println("Defeated " + target.getName() + "!");
            currentRoom.removeMonster(target);

            // Check if all monsters are defeated
            if (currentRoom.getMonsters().isEmpty()) {
                endCombat();
                return;
            }
        }

        // Monsters' turn to attack (only if combat continues)
        if (myStateController.isInState(GameState.COMBAT)) {
            monsterAttacks();
            myGameUI.updateCombatScreen(currentRoom.getMonsters());
            myGameUI.updatePlayerStats(); // Update mana/energy display
        }
    }

    /**
     *handles monster attacks against player
     */
    private void monsterAttacks() {
        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());
        List<Monster> monsters = currentRoom.getMonsters();

        // Create a copy to iterate over, as monsters might die and be removed
        List<Monster> attackers = new ArrayList<>(monsters);

        for (Monster monster : attackers) {
            if (monster.isAlive()) { // Only living monsters attack
                int damage = monster.attack(player); // attack should call takeDamage
                System.out.println(monster.getName() + " attacked player for " + damage + " damage!");
                myGameUI.getCombatScreen().addGameMessage(monster.getName() + " attacked for " + damage +
                        " damage!");
                myGameUI.getCombatScreen().addGameMessage(player.getName() + " health: " +
                        player.getHealth() + "/" + player.getMaxHealth());
                myGameUI.showMonsterAttackEffect(monster, damage);
            }
        }

        myGameUI.updatePlayerStats();
        myGameUI.updateCombatScreen(currentRoom.getMonsters());
        checkPlayerStatus();
    }

    /**
     *attempts to run from combat.
     */
    public void playerRun() {
        if (!myStateController.isInState(GameState.COMBAT)) {
            return; // Can only run during combat
        }

        // Chance to successfully run away (could be based on player stats)
        double runChance = 0.6; // 60% chance to run

        if (Math.random() < runChance) {
            System.out.println("Successfully ran away from combat!");
            endCombat();
        } else {
            System.out.println("Failed to run away!");
            myGameUI.getCombatScreen().addGameMessage("Failed to run away!");
            myGameUI.showRunFailedMessage();

            // Monsters get a free attack when run fails
            monsterAttacks();
        }
    }

    /**
     * Ends combat and returns to exploration mode.
     */
    private void endCombat() {
        myStateController.changeState(GameState.EXPLORING);
        myGameUI.hideCombatScreen();
        System.out.println("Combat ended, returning to exploration");
    }

    /**
     * Opens a chest in the current room.
     */
    public void openChest() {
        if (!myStateController.isInState(GameState.CHEST)) {
            return;
        }

        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());

        if (currentRoom.hasChest()) {
            // Get items from chest
            List<Item> chestItems = currentRoom.openChest();

            // Add items to player inventory
            for (Item item : chestItems) {
                player.addItem(item);
                System.out.println("Found item in chest: " + item.getName());
            }

            // Update UI
            myGameUI.showChestContents(chestItems);
            myGameUI.updateInventory();

            // Return to exploration mode
            myStateController.changeState(GameState.EXPLORING);
        }
    }

    /**
     * Cancels chest interaction.
     */
    public void cancelChestInteraction() {
        if (myStateController.isInState(GameState.CHEST)) {
            myStateController.changeState(GameState.EXPLORING);
            myGameUI.hideChestInteraction();
            System.out.println("Cancelled chest interaction");
        }
    }

    /**
     *pauses game
     */
    public void pauseGame() {
        myStateController.changeState(GameState.PAUSED);
        myGameUI.showPauseMenu(); // Changed from showPauseScreen() to showPauseMenu()
        System.out.println("Game paused");
    }

    /**
     *resumes game from pause
     */
    public void resumeGame() {
        // Return to previous state
        Room currentRoom = myGameModel.getDungeon().getRoom(myGameModel.getPlayer().getPosition());

        if (!currentRoom.getMonsters().isEmpty()) {
            myStateController.changeState(GameState.COMBAT);
            myGameUI.showCombatScreen(currentRoom.getMonsters());
        } else {
            myStateController.changeState(GameState.EXPLORING);
            myGameUI.hideAllOverlays();
        }

        System.out.println("Game resumed");
    }

    /**
     *saves the current game state
     */
    public void saveGame() {
        // Delegate to model to save game data
        // Assuming saveGame method expects save slot or file name
        boolean saved = myGameModel.saveGame("save1");

        if (saved) {
            System.out.println("Game saved successfully");
            myGameUI.showSaveSuccessMessage();
        } else {
            System.out.println("Failed to save game");
            myGameUI.showSaveFailureMessage();
        }
    }


    /**
     * Loads saved game state
     *
     * @param theSaveId ID of save to load
     * @return True if game was loaded successfully
     */
    public boolean loadGame(final String theSaveId) {
        boolean loaded = myGameModel.loadGame(theSaveId);

        if (loaded) {
            // Update state based on loaded game
            Room currentRoom = myGameModel.getDungeon().getRoom(myGameModel.getPlayer().getPosition());

            if (!currentRoom.getMonsters().isEmpty()) {
                myStateController.changeState(GameState.COMBAT);
                myGameUI.showCombatScreen(currentRoom.getMonsters());
            } else {
                myStateController.changeState(GameState.EXPLORING);
            }

            // Update UI with loaded game data
            myGameUI.updatePlayerStats();
            myGameUI.updateInventory();
            myGameUI.updateRoomDescription(currentRoom);
            myGameUI.updatePillarCollection();

            System.out.println("Game loaded successfully");
            return true;
        } else {
            System.out.println("Failed to load game");
            return false;
        }
    }


    /**
     *gets current room description
     *
     * @return A string description of the current room
     */
    public String getCurrentRoomDescription() {
        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());

        StringBuilder description = new StringBuilder();
        description.append("You are in a ");
        description.append(currentRoom.getDescription());

        // Add information about doors
        description.append("\nExits: ");
        if (currentRoom.hasNorthDoor()) description.append("North ");
        if (currentRoom.hasEastDoor()) description.append("East ");
        if (currentRoom.hasSouthDoor()) description.append("South ");
        if (currentRoom.hasWestDoor()) description.append("West ");

        // Add information about contents
        if (currentRoom.hasChest()) {
            description.append("\nThere is a chest in this room.");
        }

        if (currentRoom.hasPillar()) {
            description.append("\nA Pillar of OO stands in the center of the room!");
        }

        if (!currentRoom.getItems().isEmpty()) {
            description.append("\nItems on the ground: ");
            for (Item item : currentRoom.getItems()) {
                description.append(item.getName()).append(" ");
            }
        }

        return description.toString();
    }

    /**
     *gets current combat status description
     *
     * @return string description of current combat
     */
    public String getCombatDescription() {
        Hero player = myGameModel.getPlayer();
        Room currentRoom = myGameModel.getDungeon().getRoom(player.getPosition());
        List<Monster> monsters = currentRoom.getMonsters();

        if (monsters.isEmpty()) {
            return "No monsters present.";
        }

        StringBuilder description = new StringBuilder();
        description.append("Combat with: ");

        for (Monster monster : monsters) {
            description.append("\n").append(monster.getName())
                    .append(" (HP: ").append(monster.getHealth()).append(")");
        }

        return description.toString();
    }

    /**
     *gets the player's inventory as a formatted string.
     *
     * @return string representation of player's inventory
     */
    public String getInventoryDescription() {
        List<Item> inventory = myGameModel.getPlayer().getInventory();

        if (inventory.isEmpty()) {
            return "Your inventory is empty.";
        }

        StringBuilder description = new StringBuilder();
        description.append("Inventory:\n");

        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            description.append(i == selectedInventoryIndex ? "→ " : "  ");
            description.append(item.getName()).append(" - ").append(item.getDescription()).append("\n");
        }

        return description.toString();
    }

    /**
     * Gets the StateController associated with this GameController.
     * @return The StateController instance.
     */
    public StateController getStateController() {
        return myStateController;
    }

    /**
     * temporary method to demonstrate GameController is working.
     */
    public void printStatus() {
        System.out.println("Game is working");
        System.out.println("Current state: " + myStateController.getCurrentState());
        System.out.println("Player: " + myGameModel.getPlayer().getName() +
                " (HP: " + myGameModel.getPlayer().getHealth() + "/" +
                myGameModel.getPlayer().getMaxHealth() + ")");
    }
}


