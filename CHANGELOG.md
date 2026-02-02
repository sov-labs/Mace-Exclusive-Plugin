# Changelog

## [1.0] - 2026-02-02
### Added
- Strict Mode implementation:
    - Blocks placing Mace in storage containers (Chests, Shulkers, etc.).
    - Prevents dropping Mace if strict mode is enabled.
    - Allows using Mace in Anvil and Enchanting Tables.
- Configuration for Strict Mode (`strict-mode`).
- **Effect Mace** features:
    - Glowing effect on first craft (5m).
    - Passive visual effects (Glowing, Soul Particles) when holding mace.
    - Ground Slam effect on combat hit.
    - Custom Kill Message.
- **Mace Chaos** implementation:
    - New "Mace of Chaos" item with corrupted lore.
    - Hard recipe (Heavy Core, Netherite, Wither Rose).
    - **Self-Curse**: Wither & Inventory Shuffle on craft/pickup.
    - **Combat**: 20% chance to shuffle victim inventory.
    - Glitch Kill Message.

### Fixed
- Fixed **Custom Recipe** conflicting with Vanilla recipe (Vanilla recipe key removed).
- Fixed **Shift-Click Mass Crafting** exploit (Shift-click crafting disabled for Mace).
