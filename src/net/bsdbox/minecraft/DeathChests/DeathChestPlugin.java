package net.bsdbox.minecraft.DeathChests;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathChestPlugin  extends JavaPlugin
{
  private JavaPlugin plugin;
  private PluginManager pluginManager;
  
  public void onEnable()
  {
    this.plugin = this;
    this.pluginManager = getServer().getPluginManager();
    
    registerListeners();
  }
  
  private void registerListeners()
  {
    this.pluginManager.registerEvents(new DeathChestListener(), this);
  }
  
  public JavaPlugin getPlugin()
  {
    return this.plugin;
  }
}
