package main.Model.dungeon;

import main.Model.character.Monster;
import main.Model.element.Pillar;
import main.Model.element.Trap;
import main.Model.util.MonsterType;
import main.Model.util.PillarType;
import main.Model.util.Point;
import main.Model.util.RoomType;

import java.util.*;

/**
 * Represents the entire dungeon, composed of multiple rooms.
 * Uses DFS to generate a randomized layout.
 */
public class Dungeon {
    private final Room[][] myRooms;
    private final int myWidth;
    private final int myHeight;
    private Point myHeroSpawnPoint;
    private Point myExitPoint;
    private int myTotalPillars;
    private int myActivatedPillars; // This should be updated by GameController based on Pillar activation
    private boolean myBossSpawned;
    private String myDifficulty;

    public Dungeon(final int theWidth, final int theHeight, final String theDifficulty) {
        if (theWidth <= 0 || theHeight <= 0) {
            throw new IllegalArgumentException("Dungeon dimensions must be positive.");
        }
        this.myWidth = theWidth;
        this.myHeight = theHeight;
        this.myRooms = new Room[theHeight][theWidth];
        this.myDifficulty = theDifficulty;
        this.myTotalPillars = 0;
        this.myActivatedPillars = 0;
        this.myBossSpawned = false;
        generateLayout();
    }

    /**
     * Generates a randomized dungeon layout using Depth-First Search (DFS).
     * Ensures connectivity and places special rooms.
     */
    private void generateLayout() {
        Random random = new Random();

        // 1. Initialize all rooms with all walls up (no doors)
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                myRooms[y][x] = new Room(new Point(x, y), RoomType.EMPTY);
            }
        }

        // 2. DFS for Maze Generation
        Stack<Point> stack = new Stack<>();
        boolean[][] visited = new boolean[myHeight][myWidth];
        myHeroSpawnPoint = new Point(0, 0); // Define start
        myExitPoint = new Point(myWidth - 1, myHeight - 1); // Define end

        Point current = myHeroSpawnPoint;
        visited[current.getY()][current.getX()] = true;
        stack.push(current);

        while (!stack.isEmpty()) {
            current = stack.peek();
            List<Point> neighbors = getUnvisitedNeighbors(current, visited);

            if (!neighbors.isEmpty()) {
                Point next = neighbors.get(random.nextInt(neighbors.size()));
                removeWall(current, next); // Carve path (add doors)
                visited[next.getY()][next.getX()] = true;
                stack.push(next);
            } else {
                stack.pop(); // Backtrack
            }
        }

        // Optional: Add some loops by removing a few extra walls
        int extraDoors = (myWidth * myHeight) / 10; // e.g., 10% extra doors
        for (int i = 0; i < extraDoors; i++) {
            int randX = random.nextInt(myWidth);
            int randY = random.nextInt(myHeight);
            Point roomPoint = new Point(randX, randY);
            List<Point> allNeighbors = getAllNeighbors(roomPoint);
            if(!allNeighbors.isEmpty()) {
                removeWall(roomPoint, allNeighbors.get(random.nextInt(allNeighbors.size())));
            }
        }


        // 3. Set Start/Exit Types
        getRoom(myHeroSpawnPoint).setRoomType(RoomType.ENTRANCE);
        getRoom(myExitPoint).setRoomType(RoomType.EXIT);

        // 4. Place Pillars (Ensure 4 are placed and reachable)
        myTotalPillars = 0;
        PillarType[] pillarTypes = PillarType.values();
        List<Point> availableSpots = new ArrayList<>();
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                Point p = new Point(x, y);
                // Add spots that are not start/exit
                if (!p.equals(myHeroSpawnPoint) && !p.equals(myExitPoint)) {
                    availableSpots.add(p);
                }
            }
        }
        Collections.shuffle(availableSpots, random); // Randomize potential spots

        int pillarsToPlace = 4;
        for (int i = 0; i < pillarsToPlace && !availableSpots.isEmpty(); i++) {
            Point pillarPoint = availableSpots.remove(0); // Pick a random spot
            getRoom(pillarPoint).setPillar(new Pillar(pillarTypes[i % pillarTypes.length]));
            myTotalPillars++;
        }

        // 5. Place Other Rooms (Monsters, Traps)
        int monsterCount = (myWidth * myHeight) / 5; // Adjust density as needed
        int trapCount = (myWidth * myHeight) / 10;

        while ((monsterCount > 0 || trapCount > 0) && !availableSpots.isEmpty()) {
            Point spot = availableSpots.remove(0); // Pick another random spot
            Room room = getRoom(spot);
            // Only place if it's currently empty (not Start, Exit, or Pillar)
            if (room.getRoomType() == RoomType.EMPTY) {
                if (monsterCount > 0 && random.nextBoolean()) { // Alternate placing monsters/traps
                    room.setRoomType(RoomType.MONSTER);
                    // Add an actual monster (using constructor like in spawnBoss)
                    room.addMonster(new Monster(
                            MonsterType.GOBLIN.getName(),
                            MonsterType.GOBLIN,
                            false,
                            MonsterType.GOBLIN.getBaseHealth(),
                            spot));
                    monsterCount--;
                } else if (trapCount > 0) {
                    room.setTrap(new Trap("Floor Spikes", "Sharp spikes emerge from the floor.", 5 + random.nextInt(10)));
                    trapCount--;
                }
                // Could add Treasure rooms here too
            }
        }

        System.out.println("Randomized Dungeon generated. Pillars: " + myTotalPillars);
    }

    /**
     * Helper to find all *unvisited* neighbors for DFS.
     */
    private List<Point> getUnvisitedNeighbors(final Point current, final boolean[][] visited) {
        List<Point> neighbors = new ArrayList<>();
        int x = current.getX();
        int y = current.getY();

        // North
        if (y > 0 && !visited[y - 1][x]) neighbors.add(new Point(x, y - 1));
        // South
        if (y < myHeight - 1 && !visited[y + 1][x]) neighbors.add(new Point(x, y + 1));
        // West
        if (x > 0 && !visited[y][x - 1]) neighbors.add(new Point(x - 1, y));
        // East
        if (x < myWidth - 1 && !visited[y][x + 1]) neighbors.add(new Point(x + 1, y));

        return neighbors;
    }

    /**
     * Helper to find *all* neighbors (for adding loops).
     */
    private List<Point> getAllNeighbors(final Point current) {
        List<Point> neighbors = new ArrayList<>();
        int x = current.getX();
        int y = current.getY();

        if (y > 0) neighbors.add(new Point(x, y - 1));
        if (y < myHeight - 1) neighbors.add(new Point(x, y + 1));
        if (x > 0) neighbors.add(new Point(x - 1, y));
        if (x < myWidth - 1) neighbors.add(new Point(x + 1, y));

        return neighbors;
    }


    /**
     * Helper to "remove a wall" between two adjacent rooms by setting their door flags.
     */
    private void removeWall(final Point current, final Point next) {
        Room currentRoom = getRoom(current);
        Room nextRoom = getRoom(next);

        // Moving Right (East)
        if (current.getX() < next.getX()) {
            currentRoom.setEastDoor(true);
            nextRoom.setWestDoor(true);
        }
        // Moving Left (West)
        else if (current.getX() > next.getX()) {
            currentRoom.setWestDoor(true);
            nextRoom.setEastDoor(true);
        }
        // Moving Down (South)
        else if (current.getY() < next.getY()) {
            currentRoom.setSouthDoor(true);
            nextRoom.setNorthDoor(true);
        }
        // Moving Up (North)
        else if (current.getY() > next.getY()) {
            currentRoom.setNorthDoor(true);
            nextRoom.setSouthDoor(true);
        }
    }

    public Room getRoom(final int x, final int y) {
        if (x >= 0 && x < myWidth && y >= 0 && y < myHeight) {
            return myRooms[y][x];
        }
        return null;
    }

    public Room getRoom(final Point thePoint) {
        if (thePoint == null) return null;
        return getRoom(thePoint.getX(), thePoint.getY());
    }

    public int getWidth() {
        return myWidth;
    }

    public int getHeight() {
        return myHeight;
    }

    public Point getHeroSpawnPoint() {
        return myHeroSpawnPoint;
    }

    public Point getExitPoint() {
        return myExitPoint;
    }

    public int getTotalPillars() {
        return myTotalPillars;
    }

    public int getActivatedPillars() {
        return myActivatedPillars;
    }

    /**
     * Called by the GameController when a pillar is confirmed activated by a hero.
     */
    public void recordPillarActivation() {
        this.myActivatedPillars++;
        System.out.println("Dungeon records pillar activation. Total now: " + myActivatedPillars + "/" + myTotalPillars);
        if (areAllPillarsActivated() && !myBossSpawned) {
            spawnBoss();
        }
    }

    public boolean areAllPillarsActivated() {
        // Ensure there are pillars to activate for this condition to be meaningful
        return myTotalPillars > 0 && myActivatedPillars >= myTotalPillars;
    }

    public boolean isBossSpawned() {
        return myBossSpawned;
    }

    private void spawnBoss() {
        if (myBossSpawned) return;

        Room bossRoom = getRoom(myExitPoint);
        if (bossRoom != null) {
            bossRoom.setRoomType(RoomType.BOSS);
            bossRoom.getMonsters().clear();

            // Assuming MonsterType.GIANT is defined as a boss type
            MonsterType bossType = MonsterType.GIANT;
            Monster boss = new Monster(
                    bossType.getName(),
                    bossType,
                    bossType.isElite(), // Or specific logic for boss elite status
                    bossType.getBaseHealth(),
                    bossRoom.getPosition()
            );
            bossRoom.addMonster(boss);
            this.myBossSpawned = true;
            System.out.println("All pillars activated! The " + boss.getName() + " has appeared in the " + bossRoom.getRoomType().getDisplayName() + " room!");
        } else {
            System.err.println("Error: Could not find designated boss room at " + myExitPoint + " to spawn boss.");
        }
    }

    public String getDifficulty() {
        return myDifficulty;
    }

    public String getMapString(final Point heroCurrentPosition) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < myHeight; y++) {
            for (int x = 0; x < myWidth; x++) {
                Room room = myRooms[y][x];
                if (room.getPosition().equals(heroCurrentPosition)) {
                    sb.append("[H]");
                } else if (room.isVisited()) {
                    switch (room.getRoomType()) {
                        case ENTRANCE: sb.append("[E]"); break;
                        case EXIT: sb.append("[X]"); break;
                        case BOSS: sb.append("[B]"); break;
                        case PILLAR: sb.append(room.getPillar() != null && room.getPillar().isActivated() ? "[A]" : "[P]"); break;
                        case MONSTER: sb.append(room.getMonsters().isEmpty() ? "[ ]" : "[M]"); break;
                        case TREASURE: sb.append(room.getItems().isEmpty() ? "[ ]" : "[$]"); break;
                        case TRAP: sb.append(room.getTrap() != null && room.getTrap().isSprung() ? "[_]" : "[!]"); break;
                        case EMPTY: sb.append("[ ]"); break;
                        default: sb.append("[?]"); break;
                    }
                } else {
                    sb.append("[#]");
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
