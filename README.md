# ⚔️ CustomDungeons

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.x--1.21.4%2B-brightgreen.svg)](https://papermc.io)
[![Java Version](https://img.shields.io/badge/Java-21%2B-blue.svg)](https://www.oracle.com/java/)
[![Build Status](https://img.shields.io/badge/Build-Passing-success.svg)](https://github.com/lino9999/CustomDungeons)
[![Author](https://img.shields.io/badge/Author-Lino-orange.svg)](https://github.com/lino9999)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE)

**CustomDungeons** is a high-performance, fully modular, and scalable dungeon system engineered from the ground up for modern Minecraft Paper, Purpur, and Spigot servers (1.21.x+). It enables server administrators to construct immersive multi-stage dungeons, wave arenas, puzzles, custom loot drops, and class loadouts with zero external dependencies.

---

## 🌟 Key Features

- **Multi-Stage Progression:** Define dynamic stages with unique objectives (Mob elimination, Block interaction, Item retrieval, Survival timers).
- **In-World Interactive Structures:** Place interactive loot chests with custom cooldowns, mimic traps, spawner structures, and stage-linked barrier gates.
- **Arcane Puzzle Mechanics:** Integrated Sequence Puzzles and Dial-Code combination locks to test players' wits before proceeding to the boss chamber.
- **Built-in Smart Rollback Engine:** Tracks and instantly restores all placed or broken blocks upon dungeon conclusion, ensuring arena integrity without server lag.
- **Party & Queue Matchmaking:** Built-in team coordination with party invitations, leader delegation, and automatic queue matching.
- **Class / Loadout Selection:** Pre-configured combat classes (Warrior, Ranger, etc.) that kit players out with armor and items upon entry.
- **Custom Weighted Loot Tables:** Roll multi-item drop tables with chances, min/max count, custom item lore, names, and enchantments.
- **Native GUI System:** Completely independent menu engine with zero external plugin dependencies required.
- **Comprehensive PlaceholderAPI Support:** Full placeholder suite to display player status, party stats, and active dungeon timers in scoreboards or tablists.

---

## 📜 Commands & Permissions

### Player Commands
| Command | Alias | Description | Permission |
| :--- | :--- | :--- | :--- |
| `/cd menu` | `/dungeon menu` | Opens the graphical dungeon selection menu | `customdungeons.user.menu` |
| `/cd list` | `/dungeon list` | Lists all active and available dungeons in chat | `customdungeons.user.list` |
| `/cd join <dungeon>` | `/dungeon join` | Enters a dungeon or matchmaking queue | `customdungeons.user.join` |
| `/cd leave` | `/dungeon leave` | Forfeits and exits the current dungeon session | `customdungeons.user.leave` |
| `/cd party` | `/dungeon party` | Opens the party management interface | `customdungeons.party` |
| `/cd party invite <player>` | - | Invites a player to your active party | `customdungeons.party` |
| `/cd party accept` | - | Accepts a pending party invitation | `customdungeons.party` |
| `/cd queue leave` | - | Leaves the matchmaking queue | `customdungeons.queue` |
| `/cd loadout` | `/dungeon loadout` | Opens the combat class selector | `customdungeons.loadout` |
| `/cd loot` | `/dungeon loot` | Opens the unclaimed rewards vault | `customdungeons.loot` |

### Administrative Commands
| Command | Description | Permission |
| :--- | :--- | :--- |
| `/cd admin reload` | Reloads configurations, dungeons, loot, and classes | `customdungeons.admin.reload` |
| `/cd admin create <name>` | Generates a new template dungeon file | `customdungeons.admin.create` |
| `/cd admin delete <name>` | Deletes an existing dungeon configuration | `customdungeons.admin.delete` |
| `/cd admin setlobby <name>` | Sets dungeon lobby teleport position | `customdungeons.admin.setup` |
| `/cd admin setspawn <name>` | Sets dungeon starting battle spawn | `customdungeons.admin.setup` |
| `/cd admin setexit <name>` | Sets dungeon completion exit position | `customdungeons.admin.setup` |
| `/cd admin setpos1 <name>` | Sets arena selection corner 1 | `customdungeons.admin.setup` |
| `/cd admin setpos2 <name>` | Sets arena selection corner 2 | `customdungeons.admin.setup` |
| `/cd admin start <name>` | Forces an immediate dungeon run | `customdungeons.admin.manage` |
| `/cd admin stop <name>` | Forcefully terminates an active dungeon | `customdungeons.admin.manage` |
| `/cd admin stats` | Displays runtime memory & instance statistics | `customdungeons.admin.stats` |

---

## 🧩 Placeholders (PlaceholderAPI)

| Placeholder | Output |
| :--- | :--- |
| `%customdungeons_in_dungeon%` | `true` or `false` |
| `%customdungeons_current%` | Name of active dungeon |
| `%customdungeons_stage%` | Current stage index number |
| `%customdungeons_lives%` | Player's remaining dungeon lives |
| `%customdungeons_time%` | Remaining session time formatted as `MM:SS` |
| `%customdungeons_party_size%` | Number of members in player's party |
| `%customdungeons_party_leader%` | Name of player's party leader |

---

## 🛠️ Building From Source

```bash
# Clone the repository
git clone https://github.com/lino9999/CustomDungeons.git
cd CustomDungeons

# Build the project using Gradle
./gradlew build
```
The compiled, ready-to-deploy JAR will be located in `build/libs/CustomDungeons-1.0.0.jar`.

---

## 🏷️ Tags & Metadata
`minecraft-dungeons` `spigot-plugin` `paper-plugin` `dungeon-crawler` `rpg-system` `pve-arenas` `custom-loot` `party-system` `matchmaking-queue` `minecraft-pve`
