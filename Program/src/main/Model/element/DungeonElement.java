package main.Model.element;

import main.Model.character.Hero;

/**
 * Abstract base class for all elements that can exist in a dungeon room
 * and with which a Hero can interact.
 * This includes items, traps, pillars, and potentially other interactive objects.
 */
public abstract class DungeonElement {

    /**
     * Defines how this element interacts with a hero when the hero
     * enters the same tile or actively interacts with the element.
     * Each subclass must implement this method to specify its unique behavior.
     *
     * @param theHero The hero that is interacting with this element.
     */
    public abstract void interact(final Hero theHero);

    /**
     * A textual representation of the dungeon element, often for display or logging.
     * @return A string describing the element.
     */
    @Override
    public abstract String toString();
}
