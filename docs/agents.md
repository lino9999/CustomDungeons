# CustomDungeons — AI Agent & Developer Architecture Guide

This document is optimized for AI coding agents and human contributors to quickly navigate and modify the **CustomDungeons** codebase without consuming unnecessary tokens.

---

## 1. Project Overview & Identity
- **Name:** CustomDungeons
- **Author:** Lino
- **Package:** `com.lino.customdungeons`
- **Target Platform:** Paper / Purpur / Spigot (1.21.x up to modern releases, Java 21+)
- **Build System:** Gradle (ShadowJar plugin)
- **Primary Command:** `/customdungeons` (Aliases: `/cd`, `/dungeon`, `/dungeons`)
- **Main Permissions Root:** `customdungeons.*`

---

## 2. Directory & Package Structure

```
src/main/java/com/lino/customdungeons/
├── CustomDungeons.java               # Main JavaPlugin entry point & service locator
├── api/                              # Public developer API & events
│   ├── CustomDungeonsAPI.java
│   └── events/                       # DungeonJoinEvent, DungeonCompleteEvent, etc.
├── config/                           # YAML config loaders & typed configuration models
│   ├── ConfigManager.java            # Manages config.yml, messages.yml, dungeons.yml
│   └── Messages.java                 # Centralized localized message dispatch
├── core/                             # Core dungeon domain models
│   ├── Dungeon.java                  # Dungeon blueprint/definition
│   ├── DungeonInstance.java          # Active running dungeon session
│   ├── DungeonStage.java             # Individual stage/wave with objectives & triggers
│   ├── DungeonState.java             # State enum (IDLE, STARTING, ACTIVE, ENDING, RESETTING)
│   └── DungeonManager.java           # Instance manager & lifecycle coordinator
├── party/                            # Party matchmaking & invite system
│   ├── Party.java
│   └── PartyManager.java
├── queue/                            # Matchmaking queue system
│   ├── QueueEntry.java
│   └── QueueManager.java
├── objective/                        # Stage objectives / quests
│   ├── Objective.java                # Base abstract objective
│   ├── ObjectiveType.java            # KILL_MOBS, COLLECT_ITEMS, INTERACT_BLOCK, etc.
│   └── types/                        # Concrete objective implementations
├── structure/                        # In-world interactive structures & puzzles
│   ├── Structure.java                # Base structure (Chests, Doors, Spawners, Puzzles)
│   ├── StructureType.java
│   └── types/                        # SpawnerStructure, LootChestStructure, DoorStructure, etc.
├── puzzle/                           # Interactive in-world puzzle mechanics
│   ├── Puzzle.java
│   └── types/                        # SequencePuzzle, DialCodePuzzle, BlinkMemoryPuzzle
├── loot/                             # Loot tables, rewards, drops & roll engine
│   ├── LootTable.java
│   ├── LootItem.java
│   └── LootManager.java
├── loadout/                          # Player kits & class selections
│   ├── Loadout.java
│   └── LoadoutManager.java
├── rollback/                         # Arena snapshot, block changes & rollback engine
│   └── RollbackManager.java
├── gui/                              # Built-in inventory GUI system (No external dependencies)
│   ├── Menu.java
│   ├── MenuItem.java
│   └── menus/                        # DungeonJoinMenu, PartyMenu, EditorMenu, etc.
├── hooks/                            # Optional third-party plugin integrations
│   ├── PlaceholderAPIHook.java
│   ├── VaultHook.java
│   └── MythicMobsHook.java
└── commands/                         # Command routing & tab completion
    ├── CommandDispatcher.java
    └── subcommands/                  # Clean modular subcommand handlers
```

---

## 3. Key Design Principles & Conventions
1. **Zero External GUI Dependency:** An internal, ultra-lightweight Menu framework handles all inventories with click callbacks. No external `Menus` plugin is needed.
2. **Clean Separation of Concerns:** Blueprints (`Dungeon`) represent static templates. Active runs (`DungeonInstance`) represent isolated running games.
3. **Rollback & State Safety:** Blocks altered or broken during a dungeon session are logged in `RollbackManager` and reverted instantly upon dungeon completion or reset.
4. **No Hardcoded Strings:** All chat messages, titles, action bars, and item lores originate from `messages.yml` with color codes (`&` or MiniMessage) and placeholder support.
5. **No Code Comments:** In compliance with repository requirements, code is clean, self-documenting, and concise without inline noise comments.

---

## 4. How to Extend

### Adding a New Objective Type
1. Add new enum value to `ObjectiveType`.
2. Create `XObjective.java` implementing `Objective`.
3. Register the deserializer in `ObjectiveRegistry`.
4. Listen to relevant Spigot events in `ObjectiveListener` and notify active instances via `instance.checkObjectives()`.

### Adding a New Structure / Puzzle
1. Create `XStructure.java` extending `Structure`.
2. Implement `onInteract`, `onReset`, and `onStageTrigger`.
3. Register in `StructureRegistry`.

### Adding a New Command
1. Create `XSubCommand.java` implementing `SubCommand`.
2. Register in `CommandDispatcher.java`.
3. Define permission in `plugin.yml`.

---

## 5. Token Saving Rules for AI Agents
- Do not inspect decompiled JAR files unless explicitly asked.
- Target only specific files in `src/main/java/` when making changes.
- Use `ConfigManager` and `Messages` for text alterations rather than modifying hardcoded Java files.
