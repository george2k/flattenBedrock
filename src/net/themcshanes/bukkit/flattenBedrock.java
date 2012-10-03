package net.themcshanes.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class flattenBedrock extends JavaPlugin {
	private boolean _enabled;
	private int _level;
	
	@Override
	public void onEnable(){
		getLogger().info("Enabled!");
		_enabled = getConfig().getBoolean("flattenBedrock", false);
		_level = getConfig().getInt("bedrockLevel", 0);
				
		String enabledMessage = String.format("Enabled: {0}", _enabled ? "True" : "False");
		String levelMessage = String.format("Level: {0}", _level);
		getLogger().info(enabledMessage);
		getLogger().info(levelMessage);
		getLogger().info(getConfig().get("replaceWith").toString());
	}
 
	@Override
	public void onDisable(){
		getLogger().info("Disabled!");
	}
}
