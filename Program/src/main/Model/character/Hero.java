package main.Model.character;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import main.Model.element.Item;
import main.Model.element.Pillar;
import main.Model.util.Direction;
import main.Model.util.HeroType;
import main.Model.dungeon.Room;
import main.Model.util.Point;

/**
 * Represents the player character.
 * This class extends Character and includes hero-specific attributes like inventory,
 * hero type, and interaction with game elements.
 */
public class Hero extends Character { // Make sure Character is in main.Model.character
    private final String myName;
    private final HeroType myType;
    private int myMaxHealth;
    private final ArrayList<Item> myInventory;
    private int myPillarsActivated;
    private int myGold;
    // Replace the pillar collection counter with stat tracking
    private int myStrengthBonus = 0;
    private int myDefenseBonus = 0;
    private int myAgilityBonus = 0;
    private int myHealthBonus = 0;
    // Add fields for other stats derived from HeroType if they can be modified by game events (buffs, items)
    // example:
    // private int myCurrentAttackPower;
    // private double myCurrentCritChance;
    // private double myCurrentCritMultiplier;

    private double myPixelX; // Actual pixel position
    private double myPixelY;
    private double myMoveSpeed = 2.0; // Pixels per frame
    private boolean myIsMoving = false;
    private Direction myFacingDirection = Direction.SOUTH;
    private Direction myMoveDirection = null;

    /**
     * Constructor for Hero.
     * Initializes the hero with a name, type, and starting position.
     * Base stats like health, attack, crit chance, etc., are derived from the HeroType enum.
     *
     * @param theName The hero's name.
     * @param theType The hero's type (e.g., WARRIOR, PRIESTESS, THIEF).
     * @param theStartPosition The hero's starting position in the dungeon.
     */
    public Hero(final String theName, final HeroType theType, final Point theStartPosition) {
        // Call the superclass (Character) constructor with base health from HeroType
        super(theType.getBaseHealth(), theStartPosition); // Pass initial health and position

        if (theName == null || theName.trim().isEmpty()) {
            throw new IllegalArgumentException("Hero name cannot be null or empty.");
        }
        if (theType == null) {
            throw new IllegalArgumentException("HeroType cannot be null.");
        }
        // theStartPosition null check is handled by superclass if it does so, or add here.

        this.myName = theName;
        this.myType = theType;
        this.myMaxHealth = theType.getBaseHealth(); // Max health is initially base health
        this.myInventory = new ArrayList<>();
        this.myPillarsActivated = 0;
        this.myGold = 0;

        // Initialize other modifiable stats from HeroType's base values
        // this.myCurrentAttackPower = theType.getBaseAttack();
        // this.myCurrentCritChance = theType.getCritChance();
        // this.myCurrentCritMultiplier = theType.getCritMultiplier();
        // etc.
    }

    public void startMoving(final Direction direction) {
        myMoveDirection = direction;
        myFacingDirection = direction;
        myIsMoving = true;
    }

    public void stopMoving() {
        myIsMoving = false;
        myMoveDirection = null;
    }

    public void updatePosition() {
        if (myIsMoving && myMoveDirection != null) {
            switch (myMoveDirection) {
                case NORTH:
                    myPixelY -= myMoveSpeed;
                    break;
                case SOUTH:
                    myPixelY += myMoveSpeed;
                    break;
                case EAST:
                    myPixelX += myMoveSpeed;
                    break;
                case WEST:
                    myPixelX -= myMoveSpeed;
                    break;
            }
        }
    }

    /**
     * Performs an attack on a target character.
     * Damage calculation can be based on hero's type, equipped items, and other factors.
     *
     * @param theTarget The Character to attack.
     * @return The amount of damage dealt.
     */
    @Override
    public int attack(final Character theTarget) {
        if (theTarget == null || !theTarget.isAlive()) {
            return 0; // Cannot attack null or dead target
        }

        // Base damage from HeroType
        int damageDealt = myType.getBaseAttack();

        // TODO: Implement more complex damage calculation:
        // 1. Add weapon damage if applicable.
        // 2. Check for critical hit based on myType.getCritChance()
        //    If crit: damageDealt *= myType.getCritMultiplier();
        // 3. Apply special attack logic if conditions are met.
        //    (e.g., if a special attack is charged or randomly procs)
        //    damageDealt += myType.getSpecialAttackDamage(); (This might be a separate action)

        System.out.println(myName + " attacks " + ((theTarget instanceof Monster) ? ((Monster)theTarget).getName() : "target") + " for " + damageDealt + " damage.");
        theTarget.takeDamage(damageDealt);
        return damageDealt;
    }

    /**
     * Uses an item from the hero's inventory.
     * The item's effect is applied to the hero, and the item is removed from inventory.
     *
     * @param theItem The Item to use.
     */
    public void useItem(final Item theItem) {
        if (theItem != null && myInventory.contains(theItem)) {
            System.out.println(myName + " uses " + theItem.getName() + ".");
            theItem.use(this); // Item's use method will affect the hero
            myInventory.remove(theItem);
        } else if (theItem != null) {
            System.out.println(myName + " tried to use " + theItem.getName() + " but it's not in inventory.");
        }
    }

    /**
     * Picks up an item and adds it to the hero's inventory if there's space.
     *
     * @param theItem The Item to pick up.
     * @return true if the item was successfully picked up, false otherwise.
     */
    public boolean pickupItem(final Item theItem) {
        if (theItem == null) return false;

        // Example: Limit inventory size
        final int maxInventorySize = 10; // Make this a constant or configurable
        if (myInventory.size() < maxInventorySize) {
            myInventory.add(theItem);
            System.out.println(myName + " picked up " + theItem.getName() + ".");
            return true;
        } else {
            System.out.println(myName + "'s inventory is full. Cannot pick up " + theItem.getName() + ".");
            return false;
        }
    }

    public String getName() {
        return myName;
    }

    public HeroType getType() {
        return myType;
    }

    public int getMaxHealth() {
        return myMaxHealth;
    }

    public List<Item> getInventory() {
        return Collections.unmodifiableList(myInventory);
    }

    public int getPillarsActivated() {
        return myPillarsActivated;
    }

    public int getGold() {
        return myGold;
    }

    /**
     * Sets the hero's current health, ensuring it doesn't exceed maxHealth or go below 0.
     * Overrides Character.setHealth to incorporate maxHealth.
     * @param theHealth The new health value.
     */
    @Override
    public void setHealth(final int theHealth) {
        super.setHealth(Math.min(theHealth, this.myMaxHealth));
    }


    public void setMaxHealth(final int theMaxHealth) {
        if (theMaxHealth > 0) {
            this.myMaxHealth = theMaxHealth;
            if (getHealth() > this.myMaxHealth) { // Ensure current health doesn't exceed new max
                setHealth(this.myMaxHealth);
            }
        }
    }

    public void setPillarsActivated(final int thePillarsActivated) {
        this.myPillarsActivated = Math.max(0, thePillarsActivated);
    }

    public void addGold(final int amount) {
        if (amount > 0) {
            this.myGold += amount;
            System.out.println(myName + " gained " + amount + " gold. Total: " + myGold);
        }
    }

    public boolean spendGold(final int amount) {
        if (amount > 0 && this.myGold >= amount) {
            this.myGold -= amount;
            System.out.println(myName + " spent " + amount + " gold. Remaining: " + myGold);
            return true;
        }
        System.out.println(myName + " does not have enough gold to spend " + amount + ".");
        return false;
    }

    /**
     * Overrides Character's takeDamage to provide hero-specific feedback or effects.
     * @param damageAmount The amount of damage to take.
     */
    @Override
    public void takeDamage(final int damageAmount) {
        super.takeDamage(damageAmount); // Calls Character's takeDamage logic
        System.out.println(myName + " takes " + damageAmount + " damage. Current health: " + getHealth() + "/" + myMaxHealth);
        if (!isAlive()) {
            System.out.println(myName + " has been defeated!");
            // Game over logic would typically be handled by a GameController observing this state.
        }
    }

    /**
     *add item to hero's inventory
     *
     * @param item item to add
     * @return true if item was added successfully, false otherwise
     */
    public boolean addItem(final Item item) {
        return pickupItem(item); // Use existing pickupItem method
    }

    /**
     * Activates a pillar and receives its stat bonus.
     *
     * @param pillar The pillar to activate
     * @return true if the pillar was successfully activated
     */
    public boolean activatePillar(final Pillar pillar) {
        if (pillar == null || pillar.isActivated()) {
            return false;
        }

        // Mark the pillar as activated - pass this hero as the parameter
        pillar.activate(this);

        return true;
    }

    /**
     * Checks if the hero has activated all pillars.
     *
     * @return true if all pillars have been activated, false otherwise
     */
    public boolean hasActivatedAllPillars() {
        // Assuming there are 4 pillars in total
        final int TOTAL_PILLARS = 4;
        return myPillarsActivated >= TOTAL_PILLARS;
    }



    /**
     * Performs a special attack, now affected by strength and agility bonuses.
     *
     * @return The amount of damage dealt
     */
    public int specialAttack() {
        int baseDamage = myType.getSpecialAttackDamage();
        int bonusDamage = myStrengthBonus + myAgilityBonus;
        int totalDamage = baseDamage + bonusDamage;

        System.out.println(myName + " performs a special attack!");
        return totalDamage;
    }

    /**
     *checks if hero can use special attack
     * This is simple implementation that always returns true
     *modify this later to add limitations as needed
     *
     * @return true if hero can use special attack
     */
    public boolean canUseSpecialAttack() {
        // For now, always allow special attacks
        // You can modify this later to add limitations like:
        // - Once per combat
        // - Every other turn
        // - Based on health percentage
        return true;
    }


    public double getPixelX() {
        return myPixelX;
    }

    public double getPixelY() {
        return myPixelY;
    }

    public void setPixelX(int i) {
        myPixelX = i;
    }

    public void setPixelY(int i) {
        myPixelY = i;
    }
}
