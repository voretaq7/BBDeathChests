package net.bsdbox.minecraft.DeathChests;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathChestPlugin  extends JavaPlugin
{
  private JavaPlugin plugin;
  private PluginManager pluginManager;
  private FileConfiguration config;
  private List<Material> chestable_blocks;
  
  public DeathChestPlugin() {
	  saveDefaultConfig();
  }

  public void onEnable()
  {
    this.plugin = this;
    this.pluginManager = getServer().getPluginManager();
    
    config = getConfig();
    loadChestables();
	  
    registerPlugin();
  }

  public boolean msgLocation() {
    return config.getBoolean("location_message", true);
  }

  public int delay() {
	  return config.getInt("delay", 5);
  }

  public boolean debug() {
    return config.getBoolean("debug", false);
  }

  private void loadChestables() {	  
	  this.chestable_blocks = new ArrayList<Material>();

	  for (String s : config.getStringList("chestable_blocks")){
		  if (Material.getMaterial(s) == null)
			  this.getLogger().warning("Unknown Chestable Block Type: " + s);
		  else
			  chestable_blocks.add(Material.getMaterial(s));
	  }
  }
  
  public void reloadPlugin() {
	  HandlerList.unregisterAll(this);
	  config = getConfig();
	  loadChestables();
	  registerPlugin();
  }
  
  private void registerPlugin()
  {  
	  this.pluginManager.registerEvents(new DeathChestListener(this, chestable_blocks), this);
  }
  
  public JavaPlugin getPlugin()
  {
    return this.plugin;
  }
}

