# Mace-Exclusive.

<div align="center">

![Mace-Exclusive](https://img.shields.io/badge/Mace--Exclusive-Plugin-E84C3D?style=for-the-badge&logo=minecraft&logoColor=white)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://jdk.java.net/21/)
[![Spigot](https://img.shields.io/badge/Spigot-1.21+-F7CF0C?style=for-the-badge&logo=spigotmc&logoColor=white)](https://www.spigotmc.org/)
[![Gradle](https://img.shields.io/badge/Gradle-8.1-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](./LICENSE)

**Standalone Powerful Mace Plugin** 🛠️

A unique singleton weapon mechanic with custom effects, strict inventory tracking, and fully configurable settings.
Originally part of **SabíSMP**, now a dedicated plugin.

[Features](#features) • [Installation](#installation) • [Commands](#commands) • [Permissions](#permissions) • [Building](#building-from-source) • [Support](#support)

</div>

---

## Features

### 🔨 Limits the Power of the MACE
A legendary weapon with unique mechanics:
*   **Singleton Existence**: Only **ONE** Mace can exist on the server at a time
*   **Efffect Mace**:  When you first own a Mace, you are the most wanted player on the server
*   **Strict Mode** (can be changed in config.yml):
    *   **No Storing**: You cannot store the Mace in your inventory
    *   **No Dropping**: You cannot drop the Mace
    *   **Inventory Locking**: You cannot use the inventory while holding the Mace

### 📜 Custom Recipe of the Mace
*   **Custom Stats**: Configurable name, lore, and Custom Model Data.
*   **Shape**: Vertical alignment by default (Heavy Core + Breeze Rods).
*   **Fully Configurable**: Change ingredients and shape in `config.yml`.
---

## Installation

1.  **Download**: Get the latest JAR from [Nodrinth here](https://nodrinth.com/plugin/mace-exclusive).
2.  **Install**: Drop the file into your server's `plugins/` folder.
3.  **Restart**: Start your server to generate config files.
4.  **Configure**: Edit files in `plugins/Mace-Exclusive/`:
    *   `config.yml`: Feature toggles (Strict mode, Recipe, Item stats)
    *   `lang_en.yml` / `lang_vi.yml`: Localization
5.  **Reload**: Use `/macee reload` to apply changes.

---

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/macee help` | Show help menu | `mace.use` |
| `/macee info` | View current Mace holder & location | `mace.use` |
| `/macee give <player>` | **Admin**: Force give the Mace to a player | `mace.admin` |
| `/macee reset` | **Admin**: Reset Mace status (allows crafting again) | `mace.admin` |
| `/macee reload` | **Admin**: Reload configuration | `mace.admin` |

---

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `mace.use` | true | Access to basic info/help commands |
| `mace.admin` | op | Full access to admin commands & bypasses |

---

## Building from Source

### Prerequisites
*   [JDK 21](https://jdk.java.net/21/) or newer
*   Git

### Build Steps

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/sov-labs/Mace-Exclusive-Plugin.git
    cd Mace-Exclusive-Plugin
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
    `build/libs/Mace-Exclusive-1.0.0.jar`

> **Note**: We skip tests (`-x test`) during build usually, but you can run them with `./gradlew test`.

---

## License

Distributed under the MIT License. See `LICENSE` for more information.

Copyright © 2026 **NirussVn0** and **SOV Labs**.

---

## Support

If you find any issues or have suggestions, please open an issue on [GitHub](https://github.com/sov-labs/Mace-Exclusive-Plugin/issues).

donate: [paypal](https://www.paypal.com/paypalme/nirussvn0)