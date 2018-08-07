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
	
	public DeathChestListener(List<Material> cb) {
		chestable_blocks=cb;
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
			
			if (! blockIsChestable(deathLocation.getBlock()) ) {
				// Well fuck - They're inside a block or something. Let's see what we can do...
				if (blockIsChestable(deathLocation.getBlock().getRelative(BlockFace.NORTH)))
						deathLocation.add(0,0,-1);
				else if (blockIsChestable(deathLocation.getBlock().getRelative(BlockFace.SOUTH)))
					deathLocation.add(0,0,1);				
				else if (blockIsChestable(deathLocation.getBlock().getRelative(BlockFace.EAST)))
					deathLocation.add(1,0,0);				
				else if (blockIsChestable(deathLocation.getBlock().getRelative(BlockFace.WEST)))
					deathLocation.add(-1,0,0);
				else if (blockIsChestable(deathLocation.getBlock().getRelative(BlockFace.UP)))
					deathLocation.add(0,1,0);
				else {
					// If we couldn't find a Chestable block within 1 space of their location
					// then they're not getting a chest. Sorry.
					player.sendMessage("§cYour death location was §lNOT§r§c suitable for a chest.");
					player.sendMessage(deathLocation.getBlock().getType().name());
					player.sendMessage(deathLocation.getBlock().getRelative(BlockFace.UP).getType().name());

					return;
				}
			}
			
			if (inventory.contains(Material.CHEST)) {
					BlockFace secondChest;
					// We know the death block is Chestable, so drop a chest
					inventory.removeItem(new ItemStack(Material.CHEST,1));
					world.getBlockAt(deathLocation).setType(Material.CHEST);
					
					// If we need to drop a second chest & have one do that too
					if (numDrops > 27 && inventory.contains(Material.CHEST)) {
						secondChest = placeSecondChest(deathLocation.getBlock());
						if (secondChest != BlockFace.SELF) {
							inventory.removeItem(new ItemStack(Material.CHEST,1));
						}
					} else {
						secondChest = BlockFace.SELF;
					}

					// Fill the chest (We know it's all gonna fit)
					Chest chest1 = (Chest)world.getBlockAt(deathLocation).getState();
					Chest chest2 = (Chest)world.getBlockAt(deathLocation).getRelative(secondChest).getState();
					
					drops = inventory.getContents();
					int chest_count = 0;
					int ground_count = 0;
					for (int i=0 ; i < drops.length ; i++){
						if (drops[i] != null) {
							if (chest_count < 27) {
								chest1.getInventory().addItem(drops[i]);
								chest_count++;
							} else if (chest_count < 54 && secondChest != BlockFace.SELF) {
								chest2.getInventory().addItem(drops[i]);
								chest_count++;
							}
							else {
								world.dropItemNaturally(deathLocation, drops[i]);
								ground_count++;
							}
						}
					}
					if (ground_count > 0) {
						player.sendMessage("§cSome of your items have been safely stored in a chest at your death site.");
						player.sendMessage("§cHurry up and retrieve the rest!");

					} else {
						player.sendMessage("§cAll of your items have be safely stored in a chest at your death site.");
					}

					// Don't double-drop: Clear the PlayerDeathEvent's drops.
					event.getDrops().clear();
				} 
			else {
				player.sendMessage("§cYou did not have any chests, so your items are on the ground.");
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

private BlockFace placeSecondChest(Block block)
{
	if (blockIsChestable(block.getRelative(BlockFace.NORTH)))
	{
		block.getRelative(BlockFace.NORTH).setType(Material.CHEST);
		return BlockFace.NORTH;
	}
	if (blockIsChestable(block.getRelative(BlockFace.EAST)))
	{
		block.getRelative(BlockFace.EAST).setType(Material.CHEST);
		return BlockFace.EAST;
	}
	if (blockIsChestable(block.getRelative(BlockFace.SOUTH)))
	{
		block.getRelative(BlockFace.SOUTH).setType(Material.CHEST);
		return BlockFace.SOUTH;
	}
	if (blockIsChestable(block.getRelative(BlockFace.WEST)))
	{
		block.getRelative(BlockFace.WEST).setType(Material.CHEST);
		return BlockFace.WEST;
	}
	return BlockFace.SELF;
}
}
