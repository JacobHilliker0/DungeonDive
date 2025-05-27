package main.Model.character;

import java.util.ArrayList;
import main.Model.element.Item;
import main.Model.util.MonsterType;
import main.Model.util.Point;

/**
 * Represents an enemy in the game.
 */
public class Monster extends Character {
    private boolean isElite;
    private String name;
    private MonsterType type;
    private ArrayList<Item> rewards;
    private final int myMaxHealth;

    /**
     * Constructor for Monster.
     * @param name The monster's name
     * @param type The monster's type
     * @param isElite Whether this is an elite monster
     * @param health Initial health points
     * @param position Starting position
     */
    public Monster(final String name, final MonsterType type, final boolean isElite,
                   final int health, final Point position) {
        super(health, position);
        this.myMaxHealth = health;
        this.name = name;
        this.type = type;
        this.isElite = isElite;
        this.rewards = new ArrayList<>();
    }

    /**
     * Attack implementation for Monster.
     * @param target The character to attack
     * @return The damage dealt
     */
    @Override
    public int attack(final Character target) {
        // Use base damage from MonsterType
        int baseDamage = this.type.getBaseAttack(); // <-- Use MonsterType for base damage

        // Elite monsters deal more damage
        if (isElite) {
            baseDamage *= 1.5; // Example: 50% increase
        }

        System.out.println(getName() + " attacks " + target.getClass().getSimpleName() + " for " + baseDamage + " damage!");
        target.takeDamage(baseDamage); // Apply the damage
        return baseDamage;
    }


    /**
     * Get rewards when monster is defeated.
     * @return List of items as rewards
     */
    public ArrayList<Item> getRewardOnDefeat() {
        return rewards;
    }

    /**
     * Add a reward item to this monster's drop list.
     * @param item The item to add as reward
     */
    public void addReward(final Item item) {
        rewards.add(item);
    }

    // Getters and setters
    public boolean isElite() {
        return isElite;
    }

    public void setElite(final boolean isElite) {
        this.isElite = isElite;
    }

    public String getName() {
        return name;
    }

    public MonsterType getType() {
        return type;
    }

    public int getMaxHealth() {
        return myMaxHealth;
    }
}