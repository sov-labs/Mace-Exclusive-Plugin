# Changelog

All notable changes to this project will be documented in this file.

## [1.0.1] - 2026-02-03
### Fixed
- Fixed critical startup issue where plugin failed to load due to YAML syntax error in `plugin.yml`.
- Added robust error handling in `onEnable` to catch and log startup crashes.
- Fixed `ConfigManager` not loading languages properly on initial startup.

### Changed
- Removed duplicate config loading in `ConfigManager`.
- Improved error messages for missing 'macee' command.
