package main.Model.element;

import main.Model.character.Hero;
import main.Model.util.PillarType;
import java.util.Objects;

/**
 * Represents a Pillar of OO in the dungeon.
 * Activating a pillar grants the hero a permanent buff.
 */
public class Pillar extends DungeonElement {

    private final PillarType myType;
    private boolean myIsActivated;

    /**
     * Constructs a new Pillar.
     *
     * @param theType The type of this pillar (Abstraction, Encapsulation, etc.).
     * This parameter must be of type main.Model.util.PillarType.
     */
    public Pillar(final PillarType theType) {
        if (theType == null) {
            throw new IllegalArgumentException("PillarType cannot be null.");
        }
        this.myType = theType;
        this.myIsActivated = false;
    }

    /**
     * Activates the pillar if it's not already activated.
     *
     * @param theHero The hero activating the pillar.
     */
    public void activate(final Hero theHero) {
        if (theHero == null) {
            // Or throw IllegalArgumentException
            System.err.println("Cannot activate pillar with a null hero.");
            return;
        }
        if (!myIsActivated) {
            myIsActivated = true;
            System.out.println(theHero.getName() + " activated the Pillar of " + myType.getDisplayName() + "!");
            applyBuff(theHero);
            // Added this here to increment pillar count, myPillarsActivated was staying zero.
            theHero.setPillarsActivated(theHero.getPillarsActivated() + 1);
        } else {
            System.out.println("The Pillar of " + myType.getDisplayName() + " is already activated.");
        }
    }

    /**
     * Applies the specific buff of this pillar to the hero.
     * This logic would depend on how buffs are implemented.
     *
     * @param theHero The hero to apply the buff to.
     */
    private void applyBuff(final Hero theHero) {
        System.out.println("Applying buff for: " + myType.getDisplayName() + " - " + myType.getDescription());

        //SOME ROUGH IDEAS plz change this

        // Apply stat bonuses based on pillar type
        switch (myType) {
            case ABSTRACTION:
                // Increase strength
                System.out.println(theHero.getName() + "'s strength increased!");
                break;
            case ENCAPSULATION:
                // Increase Attack
                System.out.println(theHero.getName() + "'s attack increased!");
                break;
            case INHERITANCE:
                // Increase max health
                System.out.println(theHero.getName() + "'s maximum health increased!");
                break;
            case POLYMORPHISM:
                // Increase Special Attack Damage
                System.out.println(theHero.getName() + "'s special Attack Damage increased!");
                break;
            default:
                System.out.println("Unknown pillar type, no buff applied.");
                break;
        }
    }

    public boolean isActivated() {
        return myIsActivated;
    }

    public PillarType getType() {
        return myType;
    }

    @Override
    public void interact(final Hero hero) {
        if (hero != null) {
            System.out.println(hero.getName() + " approaches the Pillar of " + myType.getDisplayName() + ".");
            activate(hero);
        }
    }

    @Override
    public String toString() {
        return "Pillar of " + myType.getDisplayName() + (myIsActivated ? " (Activated)" : " (Inactive)");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pillar pillar = (Pillar) o;
        return myIsActivated == pillar.myIsActivated && myType == pillar.myType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(myType, myIsActivated);
    }
}
