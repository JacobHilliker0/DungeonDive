package main.Model.dungeon;

import main.Model.character.Hero;
import main.Model.character.Monster;
import main.Model.element.DungeonElement;
import main.Model.element.Item;
import main.Model.element.Pillar;
import main.Model.element.Trap;
import main.Model.util.Point;
import main.Model.util.RoomType;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Represents a single room within a dungeon.
 * A room has a type, position, and can contain various dungeon elements
 * like monsters, items, pillars, and traps.
 */
public class Room {
    private final Point myPosition;
    private RoomType myRoomType;
    private boolean myIsVisited;
    private final List<Monster> myMonsters;
    private final List<Item> myItems;
    private Pillar myPillar; // A room can have at most one pillar
    private Trap myTrap;     // A room can have at most one trap (or a list if multiple are allowed)

    // Door states (true if a door exists in that direction)
    private boolean myHasNorthDoor;
    private boolean myHasEastDoor;
    private boolean myHasSouthDoor;
    private boolean myHasWestDoor;

    /**
     * Constructs a new Room.
     *
     * @param thePosition The (x,y) coordinates of this room in the dungeon.
     * @param theRoomType The type of this room (e.g., EMPTY, MONSTER, TREASURE).
     */
    public Room(final Point thePosition, final RoomType theRoomType) {
        if (thePosition == null) {
            throw new IllegalArgumentException("Room position cannot be null.");
        }
        if (theRoomType == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
        this.myPosition = thePosition;
        this.myRoomType = theRoomType;
        this.myIsVisited = false;
        this.myMonsters = new ArrayList<>();
        this.myItems = new ArrayList<>();
        this.myPillar = null;
        this.myTrap = null;

        // Doors are initially closed/non-existent until explicitly set
        this.myHasNorthDoor = false;
        this.myHasEastDoor = false;
        this.myHasSouthDoor = false;
        this.myHasWestDoor = false;
    }

    /**
     * Called when a hero enters this room.
     * Sets the room as visited and triggers interactions with elements in the room.
     *
     * @param theHero The hero entering the room.
     */
    public void enter(final Hero theHero) {
        this.myIsVisited = true;
        System.out.println(theHero.getName() + " enters room at " + myPosition + " of type " + myRoomType.getDisplayName());

        // Trigger trap if present and not sprung
        if (myTrap != null && !myTrap.isSprung()) {
            myTrap.interact(theHero); // Trap's interact method will call trigger
        }

        // For other elements like items or pillars, interaction might be more explicit
        // (e.g., player chooses to pick up an item or activate a pillar).
        // Monsters would typically engage in combat.
        if (!myMonsters.isEmpty()) {
            System.out.println("Monsters are in this room!");
            // Combat logic would be initiated by the GameController
        }
    }

    public Point getPosition() {
        return myPosition;
    }

    public RoomType getRoomType() {
        return myRoomType;
    }

    public boolean isVisited() {
        return myIsVisited;
    }

    public List<Monster> getMonsters() {
        return Collections.unmodifiableList(myMonsters); // Return a read-only view
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(myItems); // Return a read-only view
    }

    public Pillar getPillar() {
        return myPillar;
    }

    public Trap getTrap() {
        return myTrap;
    }

    public void setVisited(final boolean theVisited) {
        this.myIsVisited = theVisited;
    }

    public void setRoomType(final RoomType theRoomType) {
        if (theRoomType == null) {
            throw new IllegalArgumentException("Room type cannot be null.");
        }
        this.myRoomType = theRoomType;
    }

    public void addMonster(final Monster theMonster) {
        if (theMonster != null) {
            myMonsters.add(theMonster);
        }
    }

    public void removeMonster(final Monster theMonster) {
        myMonsters.remove(theMonster);
    }

    public void addItem(final Item theItem) {
        if (theItem != null) {
            myItems.add(theItem);
        }
    }

    public void removeItem(final Item theItem) {
        myItems.remove(theItem);
    }

    public void setPillar(final Pillar thePillar) {
        this.myPillar = thePillar;
        if (thePillar != null) {
            this.myRoomType = RoomType.PILLAR; // Automatically set room type if pillar is added
        }
    }

    public void setTrap(final Trap theTrap) {
        this.myTrap = theTrap;
        if (theTrap != null) {
            this.myRoomType = RoomType.TRAP; // Automatically set room type
        }
    }

    //  Door Management
    public boolean hasNorthDoor() { return myHasNorthDoor; }
    public void setNorthDoor(final boolean theHasDoor) { myHasNorthDoor = theHasDoor; }
    public boolean hasEastDoor() { return myHasEastDoor; }
    public void setEastDoor(final boolean theHasDoor) { myHasEastDoor = theHasDoor; }
    public boolean hasSouthDoor() { return myHasSouthDoor; }
    public void setSouthDoor(final boolean theHasDoor) { myHasSouthDoor = theHasDoor; }
    public boolean hasWestDoor() { return myHasWestDoor; }
    public void setWestDoor(final boolean theHasDoor) { myHasWestDoor = theHasDoor; }


    /**
     * Returns a list of all interactive dungeon elements currently in the room.
     * @return A list of DungeonElement objects.
     */
    public List<DungeonElement> getDungeonElements() {
        List<DungeonElement> elements = new ArrayList<>();
        if (myPillar != null) elements.add(myPillar);
        if (myTrap != null) elements.add(myTrap);
        elements.addAll(myItems);
        // Monsters are Characters, handled separately for combat usually
        return Collections.unmodifiableList(elements);
    }


    @Override
    public String toString() {
        return "Room at " + myPosition +
                ", Type: " + myRoomType.getDisplayName() +
                ", Visited: " + myIsVisited +
                ", Monsters: " + myMonsters.size() +
                ", Items: " + myItems.size() +
                (myPillar != null ? ", Pillar: " + myPillar.getType().getDisplayName() : "") +
                (myTrap != null ? ", Trap: " + myTrap.getName() : "");
    }

    @Override
    public boolean equals(final Object theO) {
        if (this == theO) return true;
        if (theO == null || getClass() != theO.getClass()) return false;
        Room room = (Room) theO;
        return Objects.equals(myPosition, room.myPosition); // Rooms are unique by position in a dungeon
    }

    @Override
    public int hashCode() {
        return Objects.hash(myPosition);
    }

    /**
     *checks if this room has chest
     *
     * @return true if the room has chest false otherwise
     */
    public boolean hasChest() {
        //check if this room contains chest
        return false; //replace with actual implementation
    }

    /**
     *opens chest in this room and returns its contents
     *
     * @return list of items from chest
     */
    public List<Item> openChest() {
        //open chest and return contents
        //add chest items to the list
        //remove chest from room or mark as opened
        return new ArrayList<>(); //replace with actual implementation
    }

    /**
     *checks if this room has pillar
     *
     * @return true if room has pillar, false otherwise
     */
    public boolean hasPillar() {
        return myPillar != null;
    }

    /**
     *removes pillar from this room
     */
    public void removePillar() {
        myPillar = null;
    }

    /**
     *checks if this room has a trap
     *
     * @return true if room has trap, false otherwise
     */
    public boolean hasTrap() {
        return myTrap != null;
    }

    /**
     *clears all items from this room
     */
    public void clearItems() {
        myItems.clear();
    }

    /**
     *get description of this room
     *
     * @return string description of room
     */
    public String getDescription() {
        return myRoomType.getDisplayName() + " room";
    }





}
