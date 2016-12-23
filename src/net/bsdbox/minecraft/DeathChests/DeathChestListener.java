package net.bsdbox.minecraft.DeathChests;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DeathChestListener implements Listener
{
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		World world = player.getWorld();
		Inventory inventory = player.getInventory();
		Location deathLocation = player.getLocation();
		ItemStack drops[] = inventory.getContents();
		int numDrops = 0;

		for (int i = 0; i < inventory.getContents().length; i++) {
			if (drops[i] != null && ! drops[i].getType().equals(Material.AIR) )
				numDrops++;
		}

		player.sendMessage("§c§lYou died at " + deathLocation.getBlockX() + ", " + 
				deathLocation.getBlockY() + ", " + deathLocation.getBlockZ() +  ".");

		if (! blockIsChestable(deathLocation.getBlock()) ) {
			player.sendMessage("§cYour death location is NOT suitable for a chest.");
			player.sendMessage("§cAnything you were carrying is on the ground.");
			return;
		}


		if (numDrops > 0 ) {
			if (inventory.contains(Material.CHEST)) {
				if ( numDrops < 27 ) {
					// We have items, they fit in one chest.
					player.sendMessage("§cYou had a chest: Your items will be safely stored in it.");
					// We know the death block is Chestable, so drop a chest
					inventory.removeItem(new ItemStack(Material.CHEST,1));
					world.getBlockAt(deathLocation).setType(Material.CHEST);

					// Fill the chest (We know it's all gonna fit)
					Chest chest = (Chest)world.getBlockAt(deathLocation).getState();
					drops = inventory.getContents();
					for (int i=0 ; i < drops.length ; i++){
						if (drops[i] != null) {
							chest.getInventory().addItem(drops[i]);
						}
					}

					// Don't double-drop: Clear the PlayerDeathEvent's drops.
					event.getDrops().clear();
				} 
				else {
					// We have more items than fit in one chest.
					if (inventory.contains(Material.CHEST, 2) && 
							placeDoubleChest(deathLocation.getBlock())) {
						// We could place a double chest.
						player.sendMessage("§cYou had 2 chests.");
						player.sendMessage("§cyour items will be safely stored in them.");
						inventory.removeItem(new ItemStack(Material.CHEST,2));
						
						// Fill the chest (We know it's all gonna fit)
						Chest chest = (Chest)world.getBlockAt(deathLocation).getState();
						drops = inventory.getContents();
						for (int i=0 ; i < drops.length ; i++){
							if (drops[i] != null) {
								chest.getInventory().addItem(drops[i]);
							}
						}
						
						// Don't double-drop: Clear the PlayerDeathEvent's drops.
						event.getDrops().clear();
					}
					else {
						// Either the player only has one chest 
						// or there aren't two chestable blocks to place the double.
						// Place a single chest and stash what we can. 
						player.sendMessage("§cYou only had one chest, or there was only room to place one.");
						player.sendMessage("§cSome of your items will be safely stored in it.");
						player.sendMessage("§cThe rest are on the ground - Hurry up and get them!");
						
						// We know the death block is Chestable, so drop a chest
						inventory.removeItem(new ItemStack(Material.CHEST,1));
						world.getBlockAt(deathLocation).setType(Material.CHEST);
						
						// Fill the chest (removing from inventory as we go)
						Chest chest = (Chest)world.getBlockAt(deathLocation).getState();
						drops = inventory.getContents();
						player.sendMessage("Inventory is " + drops.toString());
						int chest_count = 0;
						for (int i=0 ; i < drops.length ; i++){
							if (drops[i] != null) {
								if (chest_count < 27) {
									chest.getInventory().addItem(drops[i]);
									chest_count++;
								}
								else {
									world.dropItemNaturally(deathLocation, drops[i]);
								}
								
							}
						}
						
						// Don't double-drop: Clear the PlayerDeathEvent drops.
						event.getDrops().clear();
					} 
				}
			}
			else {
				player.sendMessage("§cYou did not have a chest, so your items are on the ground.");
				player.sendMessage("§cHurry up and retrieve them!");
			}
		}
	}
	
private boolean blockIsChestable(Block block) {
	if (block.isEmpty() || block.getType().equals(Material.WATER) ||
			block.getType().equals(Material.STATIONARY_WATER)) {
		return true;
	}
	return false;
}

private boolean placeDoubleChest(Block block)
{
	if (blockIsChestable(block.getRelative(BlockFace.NORTH)))
	{
		block.setType(Material.CHEST);
		block.getRelative(BlockFace.NORTH).setType(Material.CHEST);
		return true;
	}
	if (blockIsChestable(block.getRelative(BlockFace.EAST)))
	{
		block.setType(Material.CHEST);
		block.getRelative(BlockFace.EAST).setType(Material.CHEST);
		return true;
	}
	if (blockIsChestable(block.getRelative(BlockFace.SOUTH)))
	{
		block.setType(Material.CHEST);
		block.getRelative(BlockFace.SOUTH).setType(Material.CHEST);
		return true;
	}
	if (blockIsChestable(block.getRelative(BlockFace.WEST)))
	{
		block.setType(Material.CHEST);
		block.getRelative(BlockFace.WEST).setType(Material.CHEST);
		return true;
	}
	return false;
}
}