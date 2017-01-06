package net.bsdbox.minecraft.DeathChests;

import java.util.*;
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
	private List<Material> chestable_blocks;
	private List<Material> low_blocks;

	
	public DeathChestListener(List<Material> cb, List<Material> lb) {
		chestable_blocks=cb;
		low_blocks=lb;
	}
	
	public DeathChestListener() {
		// This default constructor should never be used
		// If it is there are no chestable block types
		// (chests can only be placed in empty/air blocks)
		chestable_blocks = new ArrayList<Material>();
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		World world = player.getWorld();
		Inventory inventory = player.getInventory();
		Location deathLocation = player.getLocation();
		ItemStack drops[] = inventory.getContents();
		int numDrops = 0;

		// Count the number of items in the player's inventory: This will be useful later.
		for (int i = 0; i < drops.length; i++) {
			if (drops[i] != null && ! drops[i].getType().equals(Material.AIR) )
				numDrops++;
		}

		player.sendMessage("§c§lYou died at §e" + deathLocation.getBlockX() + ",  " + 
				deathLocation.getBlockY() + ",  " + deathLocation.getBlockZ());
		
		if (numDrops > 0 ) {
			// Stuff to drop, so do work.
			
			// If we're in a "low block" look at the block above us.
			if (blockIsLow(deathLocation.getBlock()))
				deathLocation.add(0,1,0);
			
			if (! blockIsChestable(deathLocation.getBlock()) ) {
				// If the player died in an unsuitable location (Lava, inside a wall, etc.)
				// then they're not getting a chest. Sorry.
				player.sendMessage("§cYour death location was §lNOT§r§c suitable for a chest.");
				return;
			}
			
			if (inventory.contains(Material.CHEST)) {
				if ( numDrops < 27 ) {
					// We have items, they fit in one chest.

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
					player.sendMessage("§cYour items have be safely stored in a chest at your death site.");


					// Don't double-drop: Clear the PlayerDeathEvent's drops.
					event.getDrops().clear();
				} 
				else {
					// We have more items than fit in one chest.
					if (inventory.contains(Material.CHEST, 2) && 
							placeDoubleChest(deathLocation.getBlock())) {
						// We could place a double chest.
						inventory.removeItem(new ItemStack(Material.CHEST,2));
						
						// Fill the chest (We know it's all gonna fit)
						Chest chest = (Chest)world.getBlockAt(deathLocation).getState();
						drops = inventory.getContents();
						for (int i=0 ; i < drops.length ; i++){
							if (drops[i] != null) {
								chest.getInventory().addItem(drops[i]);
							}
						}
						player.sendMessage("§cYour items have been safely stored in a double chest at your death site.");
						
						// Don't double-drop: Clear the PlayerDeathEvent's drops.
						event.getDrops().clear();
					}
					else {
						// Either the player only has one chest 
						// or there aren't two chestable blocks to place the double.
						// Place a single chest and stash what we can. 
						
						// We know the death block is Chestable, so drop a chest
						world.getBlockAt(deathLocation).setType(Material.CHEST);
						inventory.removeItem(new ItemStack(Material.CHEST,1));
						
						
						// Fill the chest (removing from inventory as we go)
						Chest chest = (Chest)world.getBlockAt(deathLocation).getState();
						drops = inventory.getContents();
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
						player.sendMessage("§cSome of your items have been safely stored in a chest at your death site.");
						
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
	if ( block.isEmpty() || chestable_blocks.contains(block.getType()) ) {
		return true;
	}
	return false;
}

private boolean blockIsLow(Block block) {
	if ( low_blocks.contains(block.getType()) ) {
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