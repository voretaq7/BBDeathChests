package net.bsdbox.minecraft.DeathChests;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


public class DeathChestListener implements Listener
{
	private final List<Material> chestable_blocks;
	private final DeathChestPlugin plugin;
	
	public DeathChestListener(DeathChestPlugin P, List<Material> cb) {
		plugin=P;
		chestable_blocks=cb;
	}
	
	public DeathChestListener(DeathChestPlugin P) {
		// This default constructor should never be used
		// If it is there are no chestable block types
		// (chests can only be placed in empty/air blocks)
		plugin = P;
		chestable_blocks = new ArrayList<Material>();
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		Inventory inventory = plugin.getServer().createInventory(null, 54);
		Location deathLocation = player.getLocation();
		int numDrops = event.getDrops().size();

		if (numDrops > 54) { // I don't know how you did this...
			player.sendMessage("§c§lYou had more items than can fit in a double chest.");
			player.sendMessage("§c§lYour items are on the ground. Hurry up and retrieve them!");
			return;

		} else {
			for (ItemStack item : event.getDrops()) {
				inventory.addItem(item);
			}
		}

		if ( plugin.msgLocation() ) {
			player.sendMessage("§c§lYou died at §e" + deathLocation.getBlockX() + ",  " + 
					deathLocation.getBlockY() + ",  " + deathLocation.getBlockZ());
		}
		
		if (numDrops > 0 ) { // Player had items
			if (inventory.contains(Material.CHEST)) { // Player had at least one chest
				// Schedule a test drop in 5 ticks (0.25 seconds)
				plugin.getServer().getScheduler().runTaskLater((Plugin)plugin,
						(Runnable) new DeathChestTask(player, inventory, deathLocation,
														chestable_blocks,
														plugin.debug()),
						plugin.delay());
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
