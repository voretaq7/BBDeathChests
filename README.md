# BBDeathChests

## A simple "Death Chest" plugin for Spigot Minecraft Servers

This plugin places the contents of a player's inventory into a chest when
they die, and gives them their death coordinates.

The plugin will place a single chest if:

1. The player had a chest in their inventory.
2. The player's death location is a "suitable" block.  
Currently "suitable" is defined as air or water.

The plugin will attempt to place a double-chest if the player's inventory
will not fit in a single chest, subject to the following constraints:

1. The player must have *two* chests in their inventory.
2. The player's death location must be a suitable block.
3. The adjacent the North, South, East, or West must be a suitable block.
  
If the plugin is unable to place a double chest it will fall back to placing
a single chest, and excess items will be dropped on the ground.    
If the plugin is unable to place a single chest it will fall back to
dropping the items on the ground.


This plugin is dead simple, and I don't intend to complicate it: It does not
interact with any other plugins, check GriefPrevention/WorldGuard
permissions, provide a fancy "grave" or sign, lock the chest, clean up
excess chests, etc. - It just eliminates the anxiety of having your items
vanish from the ground in many common death scenarios.

----

# Servers using this plugin

* bsd-box.net


