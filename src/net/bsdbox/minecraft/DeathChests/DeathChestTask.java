package net.bsdbox.minecraft.DeathChests;

import java.util.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DeathChestTask implements Runnable
{
	private Player player;
	private Inventory drops;
	private Location deathLocation;
	private List<Material> chestable_blocks;
	private boolean debug;

	public DeathChestTask(Player P, Inventory D, Location L, List<Material> CB, boolean dbg) {
		player = P;
		drops = D;
		deathLocation = L;
		chestable_blocks = CB;
		debug=dbg;
	}

	@Override
	public void run() {
		BlockFace secondChest = BlockFace.SELF; // Location of second chest
        int numDrops = 0;
        ItemStack d[];
		// Count the number of items dropped.
		for (int i = 0; i < drops.getContents().length; i++) {
			if (drops.getContents()[i] != null &&
			    ! drops.getContents()[i].getType().equals(Material.AIR) )
				numDrops++;
		}

		// Try to find a place to put the player's death chest.
		if (! blockIsChestable(deathLocation.getBlock()) ) {
			// Well fuck - They're inside a block or something.
			// Let's see what we can do...
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
				// If we couldn't find a Chestable block within 1 space
				// of the death location they're not getting a chest. Sorry.
				d = drops.getContents();
				for (int i=0 ; i < d.length ; i++){
					if (d[i] != null) {
						deathLocation.getWorld().dropItemNaturally(deathLocation, d[i]);
					}
				}
				player.sendMessage("§cYour items are on the ground. Hurry up and retrieve them!");
				if (debug) {
					// Debugging: Print the block type for everything we looked at.
					player.sendMessage("§cLocations examined were §lNOT§r§c suitable for a chest.");
					player.sendMessage("Block: " + deathLocation.getBlock().getType().name());
					player.sendMessage("Above: " + deathLocation.getBlock().getRelative(BlockFace.UP).getType().name());
					player.sendMessage("North: " + deathLocation.getBlock().getRelative(BlockFace.NORTH).getType().name());
					player.sendMessage("East:  " + deathLocation.getBlock().getRelative(BlockFace.EAST).getType().name());
					player.sendMessage("South: " + deathLocation.getBlock().getRelative(BlockFace.SOUTH).getType().name());
					player.sendMessage("West:  " + deathLocation.getBlock().getRelative(BlockFace.WEST).getType().name());
				}

				return;
			}
		}

		// We know the deathLocation block is Chestable, so drop a chest
		drops.removeItem(new ItemStack(Material.CHEST,1));
		deathLocation.getBlock().setType(Material.CHEST);

		// If we need to drop a second chest & have one do that too
		if (numDrops > 27 && drops.contains(Material.CHEST)) {
			secondChest = placeSecondChest(deathLocation.getBlock());
			if (secondChest != BlockFace.SELF) {
				drops.removeItem(new ItemStack(Material.CHEST,1));
			}
		}

		// Grab the chests so we can fill them.
		// Note that if secondChest is "SELF" then chest2 is really invalid...
		Chest chest1 = (Chest)deathLocation.getBlock().getState();
		Chest chest2 = (Chest)deathLocation.getBlock().getRelative(secondChest).getState();

		// Fill the chests
		int chest_count = 0;
		int ground_count = 0;
		d = drops.getContents();
		for (int i=0 ; i < d.length ; i++){
			if (d[i] != null) {
				if (chest_count < 27) {
					chest1.getInventory().addItem(d[i]);
					chest_count++;
				} else if (chest_count < 54 && secondChest != BlockFace.SELF) {
					chest2.getInventory().addItem(d[i]);
					chest_count++;
				}
				else {
					deathLocation.getWorld().dropItemNaturally(deathLocation, d[i]);
					ground_count++;
				}
			}
		}
		if (ground_count > 0) {
			player.sendMessage("§cSome of your items have been safely stored in a chest at your death site.");
			player.sendMessage("§cHurry up and retrieve the rest!");
		} else {
			player.sendMessage("§cAll of your items have been safely stored in a chest at your death site.");
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
