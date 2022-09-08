[![Codacy Badge](https://app.codacy.com/project/badge/Grade/1d378b88c90147399d3b56db6984f0e4)](https://www.codacy.com/gh/tim-savage/SavageGraveyards/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=tim-savage/SavageGraveyards&amp;utm_campaign=Badge_Grade)

### Description

This plugin implements discoverable graveyards. Players will respawn at the nearest graveyard location that they have previously discovered, with temporary immunity from mob attack.

### Features

*   Create hidden graveyards that require discovery, or unhidden that are always available.
*   Enable or disable graveyards individually, without deleting them (to make a graveyard temporarily unavailable, for instance).
*   Custom discovery message per graveyard location, or use the default message in the customizable language file.
*   Custom respawn message per graveyard location, or use the default message in the customizable language file.
*   Safety cooldown prevents mobs from attacking after respawn for a configurable amount of time. Per graveyard setting overrides default in configuration file.
*   Customizable discovery range, can be set per graveyard location or use configured default.
*   Teleport command allows admins to instantly travel to any graveyard.
*   Group permissions setting per graveyard, so graveyards can be made available only to players within a certain group.
*   Intelligent tab completion for all commands.
*   Custom messages and language localization.
*   Per world enabled in configuration.
*   Uses sqlite for persistent storage.
*   A perfect compliment to [SavageDeathChest](https://github.com/tim-savage/SavageDeathChest) and [SavageDeathCompass](https://github.com/tim-savage/SavageDeathCompass).

### Permissions

Player's are granted all necessary permissions by default.  
All admin permissions are granted with `graveyard.admin`  

[Permission Nodes](https://github.com/tim-savage/SavageGraveyards/wiki/Permission-Nodes)

### Commands

[Command Reference](https://github.com/tim-savage/SavageGraveyards/wiki/Command-Summary)

### Installation

Put the jar in your plugins folder and restart your server. Edit the generated configuration file to your liking, then reload the plugin settings with the `/graveyard reload` command. No server restart necessary!
