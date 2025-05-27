package main.Model.element;

import main.Model.character.Hero;
import java.util.Objects;

/**
 * Abstract base class for items that can be found and used in the dungeon.
 * Items are DungeonElements that can typically be picked up and used by a Hero.
 */
public abstract class Item extends DungeonElement {
    private final String myName;
    private final String myDescription;

    /**
     * Constructor for Item.
     *
     * @param theName The name of the item.
     * @param theDescription The description of the item.
     */
    public Item(final String theName, final String theDescription) {
        if (theName == null || theName.isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty.");
        }
        this.myName = theName;
        this.myDescription = theDescription == null ? "" : theDescription;
    }

    /**
     * Defines the action of using the item on a hero.
     * This method is called when a hero decides to use this item from their inventory.
     *
     * @param theHero The hero that is using the item.
     */
    public abstract void use(final Hero theHero);

    /**
     * Implementation of the interact method from DungeonElement.
     * This defines what happens when a hero encounters this item in a room.
     * By default, the hero will attempt to pick up the item.
     *
     * @param theHero The hero that is interacting with this item.
     */
    @Override
    public void interact(final Hero theHero) {
        if (theHero != null) {
            boolean pickedUp = theHero.pickupItem(this);
            if (pickedUp) {
                System.out.println(theHero.getName() + " picked up " + myName + ".");
                // Optionally, remove the item from the room after pickup.
                // This logic might be better handled in the Room class or GameController.
            } else {
                System.out.println(theHero.getName() + " couldn't pick up " + myName + ".");
            }
        }
    }

    public String getName() {
        return myName;
    }

    public String getDescription() {
        return myDescription;
    }

    @Override
    public String toString() {
        return myName + " (" + myDescription + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(myName, item.myName) &&
                Objects.equals(myDescription, item.myDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myName, myDescription);
    }
}
