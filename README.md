# ⚔️ SabiSMP

<div align="center">

![SabiSMP](https://img.shields.io/badge/SabiSMP-Plugin-E84C3D?style=for-the-badge&logo=minecraft&logoColor=white)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://jdk.java.net/21/)
[![Spigot](https://img.shields.io/badge/Spigot-1.21+-F7CF0C?style=for-the-badge&logo=spigotmc&logoColor=white)](https://www.spigotmc.org/)
[![Gradle](https://img.shields.io/badge/Gradle-8.1-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](./LICENSE)

**Comprehensive SMP Utility Plugin** 🛠️

Advanced features for modern SMPs: Ego/Tier progression, Custom Recipes, Vaults, and Combat Protections.
Developed by **NirussVn0** and **SOV Team**.

[Features](#features) • [Installation](#installation) • [Commands](#commands) • [Permissions](#permissions) • [Building](#building-from-source)

</div>

---

## Features

### 🔥 EgoSMP System
A comprehensive progression and chaos system:
* **Ego Items**: Dropped when killing players (with cooldown). Bound to victim.
* **Ego State**: Consume an Ego item to enter a chaos state with debuffs and inventory shuffling.
* **Tier Progression**: Survive the Ego State to "Awaken" and increase your Tier.
* **Bonuses**: Higher tiers grant permanent Health and Damage bonuses.
* **Curses**: High-tier players emit glowing effects and particles.
* **Soul Keeper**: A rare item that prevents Tier loss upon death.
* **Universal Ego**: An unbound Ego item usable by anyone.
* **Ego Item Protection** *(v0.1.2)*: Ego/Soul Keeper items cannot be dropped and are kept on death (disabled after tier-up).
* **Dark Ego** *(v0.1.2)*: High-tier players (Tier 5+) have 50% chance to drop a "Darkened Ego" on death. Grants +2-3 Tiers but applies Wither II and debuffs.
* **End Region Protection** *(v0.1.3)*: Instantly kills players below Tier 9 who enter the Dragon Fight area in The End.
* **Combat Protection**: PvP disabled in spawn and for a short duration after death.

### 🎒 Player Vaults
- Personal storage accessible via `/sabi vault`

### 📜 Custom Recipes
- Define custom recipes in `recipes.yml`
- **Craft Limits**: Set per-player or global crafting limits for powerful items

### 🚫 Blocked Items
- Prevent usage of specific blocks/items
- Configure easily in `blocked.yml`


---

## Installation

1.  **Download**: Get the latest JAR from [Releases](../../releases).
2.  **Install**: Drop the file into your server's `plugins/` folder.
3.  **Restart**: Start your server to generate config files.
4.  **Configure**: Edit files in `plugins/SabiSMP/`:
    *   `config.yml`: Feature toggles (Ego, Combat, etc.)
    *   `lang_en.yml` / `lang_vi.yml`: Localization
    *   `recipes.yml` & `blocked.yml`: Item management
5.  **Reload**: Use `/sabi recipe reload` or restart.

---

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/sabi ego` | View your Ego/Tier status | None |
| `/sabi ego <player>` | View another player's status | `sabismp.admin` |
| `/sabi ego set <player> <tier>` | Set a player's tier | `sabismp.admin` |
| `/sabi ego reset <player>` | Reset a player's tier | `sabismp.admin` |
| `/sabi lang all <lang>` | Set global default language | `sabismp.admin` |
| `/sabi reload` | Reload configuration | `sabismp.admin` |
| `/sabi items` | Open admin item manager | `sabismp.admin` |
| `/sabi items ego [player]` | Give Universal Ego Item | `sabismp.admin` |
| `/sabi items soul-keeper [player]` | Give Soul Keeper Item | `sabismp.admin` |
| `/sabi help` | Show help menu | None |
| `/sabi vault` | Open your vault | `sabi.vault.use` |
| `/sabi recipe ui` | Open recipe editor GUI | `sabi.recipe.admin` |
| `/sabi block <material>` | Block a material | `sabi.block.admin` |


---

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `sabi.admin` | op | Full admin access |
| `sabi.vault.use` | true | Use personal vault |
| `sabi.recipe.admin` | op | Manage recipes and limits |
| `sabi.block.admin` | op | Manage blocked items |

---

## Building from Source

### Prerequisites
*   [JDK 21](https://jdk.java.net/21/) or newer
*   Git

### Build Steps

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/NirussVn0/SabiSMP-Plugin.git
    cd SabiSMP-Plugin
    ```

2.  **Build with Gradle:**
    We wrap Gradle, so you don't need it installed globally.
    
    *   **Windows (PowerShell):**
        ```powershell
        ./gradlew build -x test
        ```
    *   **Linux/macOS:**
        ```bash
        ./gradlew build -x test
        ```

3.  **Locate the Artifact:**
    The compiled JAR file will be located at:
    `build/libs/SabiSMP-0.1.0.jar`

> **Note**: We skip tests (`-x test`) during build because some tests require a specific environment setup. Use `./gradlew test` if you want to run the test suite.

---

## License

Distributed under the MIT License. See `LICENSE` for more information.

Copyright © 2026 **NirussVn0** and **SOV Team**.
