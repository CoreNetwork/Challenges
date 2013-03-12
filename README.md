MCNSAChallenges
===============

Set up various challenges for players to complete every week. 

Each challenge can have multiple levels (players can only complete higher levels if they completed lower) and point rewards, which affect player’s rank. Each challenge entry is handled by moderators using a simple built-in ticketing system.

Soft dependencies: [WorldEdit](https://github.com/sk89q/worldedit), [WorldGuard](https://github.com/sk89q/worldguard) (see `/chm lock` below.)

# Commands

## Player commands

|**Command**|**Permission Node**|**Description**|
|:------|:--------------|:----------|
|`/ch help`||Display a list of commands available to the player.|
|`/ch`||Display current level of a challenge to complete.|
|`/ch <level>`||Display certain level of a challenge.|
|`/ch all`||Display all levels of a challenge.|
|`/ch done`||Submit challenge entry to staff to review your current level (the one displayed by `/ch`)|
|`/ch done <level>`||Submit challenge entry to staff to review a certain level of a challenge (so you don’t have to do `/ch done` three times if you completed levels 1, 2 and 3 before submitting).|
|`/points`||Display your points, rank and points needed for next rank.|

## Moderator commands

Moderators in this case are staff members who review challenge entry tickets (teleport to them), complete or reject them.

|**Command**|**Permission Node**|**Description**|
|:------|:--------------|:----------|
|`/chm help`||Display a list of commands available to a moderator (alias: `/chm`).|
|`/chm list`||Display a list of submitted challenge entries waiting for review.|
|`/chm tp <id>`||Teleport to submitted challenge entry. Teleporting also claims entry in your name, which is displayed in `/chm list`.|
|`/chm complete [<id>]`||Mark challenge entry as completed. It will automatically close the entry and award points. The `id` parameter is optional – by default it will use ID of entry you teleported to.|
|`/chm lock [<id>] [expand]`||Lock WorldEdit selection with WorldGuard region (region name syntax: `w{week}t{level}-{playername}`), to prevent cheating. Lock is auto-released when current weekly challenge ends. The `id` parameter is optional – by default it will use ID of entry you teleported to. The `expand` parameter is also optional and can be used to expand the selection 1 block in all directions but up – useful to prevent people from adding double chests or hoppers, taking items out (when locking a chest).|
|`/chm deny [<id>] <reason>`||Reject challenge entry with a reason. The `id` parameter is optional – by default it will use ID of entry you teleported to. If `id` is skipped, `reason` cannot start with a number.|
|`/chm points <player>`||Display player’s total amount of points, rank and points needed for next rank.|
|`/chm points <player> <value> <reason>`||Manually change the amount of points by `value` – it can be a positive number (to reward a player) or negative (to punish a player). Reason is optional – if skipped, player will not get a notification unless it causes a rank change.|
    
## Administrator commands

Administrators in this case are staff members who overseer adding new weekly challenges. Challenges can be added in advance and the plugin will just use next available challenge automatically. By default, challenges go live on Sunday, 6PM GMT (this can be changed in config by editing `SwitchTimeOffset` – amount of time in seconds since Monday, 00:00).

|**Command**|**Permission Node**|**Description**|
|:------|:--------------|:----------|
|`/cha help`||Display a list of commands available to an administrator (alias: `/cha`).|
|`/cha create`||Create a new weekly challenge. Enters challenge creation mode.|
|`/cha edit <number>`||Edit weekly challenge of certain number. Enters challenge creation mode.|
|`/cha list`||List all weekly challenges and their numbers.|
|`/cha list <number>`||List all levels for weekly challenge of certain number.|
|`/cha exit`||Exit challenge creation mode.|
|`/cha save`||Exit challenge creation mode.|
|`/cha createlevel`||Add a new level. Works only in challenge creation mode.|
|`/cha deletelevel <level>`||Delete a level. Works only in challenge creation mode.|
|`/cha editlevel <level>`||Edit a level. Works only in challenge creation mode.|
|`/cha stop`||Disable checking and submitting challenges by players. Can be used to add a missing challenge after it went live (empty, with no levels).|
|`/cha resume`||Re-enable checking and submitting challenges by players.|
|`/cha resume <id>`||Re-enable checking and submitting challenges by players and switch current week to `id`. Can be used to continue with challenges after a long break (waiting for bukkit to update for new Minecraft version, long server maintenance/outage).|    
|`/cha reload`||Reload configuration from file.|