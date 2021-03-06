# BBDeathChests

## A simple "Death Chest" plugin for Spigot Minecraft Servers

This plugin places the contents of a player's inventory into a chest when
they die, and gives them their death coordinates.

The plugin will place a single chest if:

1. The player had a chest in their inventory.
2. One of the following blocks is "chestable"
  * The block at the player's death location
  * A block adjacent to the player's death location (North, South, East, or West - for fences, galss panes, etc.)
  * The block immediately above the player's death location (for farmland, carpet, slabs, stairs, etc.)
By default "chestable" block means air (empty) or water but this can be customized.

The plugin will attempt to place a double-chest if the player's inventory
will not fit in a single chest, subject to the following constraints:

1. The player must have *two* chests in their inventory.
2. The plugin found a "chestable" block as described in (2) above.
3. At least one adjacent block is (North, South, East, or West) is also chestable.
  
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


