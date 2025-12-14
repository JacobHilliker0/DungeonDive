# DungeonDive

A procedurally-generated dungeon crawler built with JavaFX. Originally developed as a university project for TCSS 360 (Software Engineering) at UW Tacoma.

## What is this?

DungeonDive is a turn-based RPG where you explore randomly-generated dungeons, fight monsters, collect items, and activate four "Pillars of OOP" before facing a final boss. The project demonstrates MVC architecture, several Gang of Four design patterns, and persistent storage with SQLite.

The game generates a 10x10 dungeon using depth-first search, populates it with monsters and items, then lets you explore room-by-room with simple WASD controls. Combat is turn-based with attack/special attack/item/run options.

## Quick Start

**Prerequisites:**
- JDK 17 or higher
- JavaFX SDK 21+

**Running:**
```bash
# Clone
git clone https://github.com/glavavlada/DungeonDive.git
cd DungeonDive

# Run (adjust path to your JavaFX installation)
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls \
     -jar DungeonDive.jar
```

**Building from source:**
```bash
cd Program
javac --module-path /path/to/javafx-sdk/lib \
      --add-modules javafx.controls \
      -d out \
      src/Main.java src/main/**/*.java

java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls \
     -cp out \
     Main
```

## Features

**Dungeon Generation**
- Procedural maze generation using DFS algorithm
- Guaranteed path from entrance to exit
- Additional loops added for non-linear exploration
- Rooms contain monsters, items, traps, or pillars

**Combat**
- 6 monster types with varying stats (Goblin, Skeleton, Slime, Orc, Wizard, Giant boss)
- Critical hit system
- Special attacks with mana cost
- Item usage mid-combat
- Run away option (60% success rate)

**Hero Classes**
- Warrior: High HP (125), high damage, 20% crit
- Priestess: Healing special attack, low crit (5%)
- Thief: High crit (40%), low HP (75)

**Persistence**
- SQLite database stores game state
- JSON serialization for complex objects
- Multiple save slots
- Save/load anywhere

## Architecture

The project uses Model-View-Controller:

```
src/main/
├── Model/          # Game logic, data structures
│   ├── character/  # Hero, Monster, factories
│   ├── dungeon/    # Dungeon generation, Room
│   ├── element/    # Items, Pillars, Traps
│   └── Database.java
├── View/           # JavaFX UI
│   └── screen/     # Game screens (combat, inventory, etc.)
└── Controller/     # Input handling, state management
```

**Design Patterns:**
- **Factory**: `HeroFactory`, `MonsterFactory` for character creation
- **Builder**: Fluent API for constructing Heroes/Monsters
- **State**: `StateController` manages game states (exploring, combat, paused, etc.)
- **MVC**: Separation of concerns between model/view/controller

**Key Classes:**
- `Dungeon`: DFS maze generation, room grid
- `GameController`: Coordinates game loop, combat, room transitions (NOTE: This class is large and violates SRP - would benefit from splitting into separate managers)
- `Hero`: Player character with inventory, stats, movement
- `Database`: SQLite operations, save/load

## Known Issues & Limitations

This is an academic project with some technical debt:

1. **GameController is a God class** (1,141 lines) - handles too many responsibilities. In a production setting, I'd split this into CombatManager, InventoryManager, MovementController, etc.

2. **Magic numbers throughout** - Values like `200ms` combat cooldown, `0.6` run-away chance, inventory size `10` should be extracted to constants.

3. **Limited test coverage** - Basic JUnit tests exist but coverage could be improved.

4. **Hardcoded spawn rates** - Monster spawn chances are hardcoded in if-else chains rather than config-driven.

These are documented intentionally to show awareness of software engineering principles and what I'd improve in a refactor.

## Project Structure

```
DungeonDive/
├── Program/src/
│   ├── Main.java
│   ├── main/
│   │   ├── Controller/
│   │   ├── Model/
│   │   └── View/
│   └── test/
├── resources/sprites/
├── dungeondive.db
└── README.md
```

## How to Play

**Movement:** WASD or arrow keys
**Interact:** E (open chests, collect items)
**Inventory:** I
**Pause:** ESC

**Combat:**
- 1: Attack
- 2: Special Attack (costs 2 mana)
- 3: Use Item
- 4: Run Away

**Win condition:** Activate all 4 Pillars, reach the exit, defeat the boss.

## Technical Details

**Dependencies:**
- JavaFX 21 (UI framework)
- SQLite JDBC (persistence)
- Jackson 2.15+ (JSON serialization)
- JUnit 5 (testing)

**Database Schema:**
The game uses SQLite with tables for character types, monster types, room types, and save data. Character stats are loaded from the database on startup, making it easy to rebalance without recompiling.

**Save Format:**
Game state is serialized to JSON and stored in the `save_games` table. This includes:
- Hero state (position, health, inventory, pillars collected)
- Dungeon layout (room types, doors, visited status)
- Monster/item placement

## Development

**Running tests:**
```bash
java -cp "out/test:out/production:lib/*" \
     org.junit.platform.console.ConsoleLauncher \
     --scan-class-path
```

**Future improvements:**
- Refactor GameController into separate manager classes
- Extract magic numbers to configuration
- Add difficulty settings
- Improve test coverage
- Remove Hungarian notation

## Contributors

- **Jacob Hilliker** - Game logic, combat system
- **Emanuel Feria** - Dungeon generation, UI
- **Vladyslav Glavatskyi** - Database, persistence

Developed for TCSS 360: Software Engineering, Spring 2025, University of Washington Tacoma.
