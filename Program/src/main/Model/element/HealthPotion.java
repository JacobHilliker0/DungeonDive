package main.Model.element;

import main.Model.character.Hero;

/**
 * Represents a health potion that restores health to a hero.
 */
public class HealthPotion extends Item {
    private final int healingAmount;

    /**
     * Constructor for HealthPotion.
     *
     * @param name The name of the potion.
     * @param description The description of the potion.
     * @param healingAmount The amount of health this potion restores.
     */
    public HealthPotion(final String name, final String description, final int healingAmount) {
        super(name, description); // Call the constructor of the parent Item class
        this.healingAmount = healingAmount;
    }

    /**
     * Uses the health potion on the hero, restoring health.
     *
     * @param hero The hero using the potion.
     */
    @Override
    public void use(final Hero hero) {
        if (hero != null && hero.isAlive()) {
            hero.setHealth(Math.min(hero.getMaxHealth(), hero.getHealth() + this.healingAmount)); // Assuming Hero has getMaxHealth()
            System.out.println(hero.getName() + " used " + getName() + " and restored " + this.healingAmount + " health.");
        }
    }

    /**
     * Gets the amount of health this potion restores.
     *
     * @return The healing amount.
     */
    public int getHealingAmount() {
        return healingAmount;
    }

    /**
     * When a hero interacts with a HealthPotion on the ground, they pick it up.
     * This overrides the default interact method from Item if specific behavior is needed,
     * but the default Item.interact() already calls hero.pickupItem(this), which is suitable.
     * If you wanted different behavior (e.g., auto-use if health is low), you could change it here.
     * For now, relying on the parent's interact method is fine.
     */
    // @Override
    // public void interact(Hero hero) {
    //     super.interact(hero); // This will call hero.pickupItem(this)
    // }
}
