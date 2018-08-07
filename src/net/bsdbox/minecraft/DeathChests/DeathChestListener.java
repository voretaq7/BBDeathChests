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
	private final List<Material> chestable_blocks;
	private final DeathChestPlugin plugin;
	
	public DeathChestListener(DeathChestPlugin P, List<Material> cb) {
		plugin=P;
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

		if ( plugin.msgLocation() ) {
			player.sendMessage("§c§lYou died at §e" + deathLocation.getBlockX() + ",  " + 
					deathLocation.getBlockY() + ",  " + deathLocation.getBlockZ());
		}
		
		if (numDrops > 0 ) { // Player had items
			if (inventory.contains(Material.CHEST)) { // Player had at least one chest
				// Schedule a test drop in 5 ticks (0.25 seconds)
				Bukkit.getScheduler().runTaskLater(plugin, new DeathChestTask(player, drops, deathLocation, chestable_blocks), 5);
				event.getDrops().clear(); // Don't double-drop: Clear the PlayerDeathEvent's drops.
			} else {
				// No chests - Items will drop on the ground as normal.
				player.sendMessage("§cYou did not have any chests, so your items are on the ground.");
				player.sendMessage("§cHurry up and retrieve them!");
				return;
			}
		}
	}
}
