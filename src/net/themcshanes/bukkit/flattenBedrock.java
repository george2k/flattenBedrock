package net.themcshanes.bukkit;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class flattenBedrock extends JavaPlugin implements Listener {
	private boolean _enabled;
	private int _level;
	private String _worldName;
	private ArrayList<ReplacementBlock> _replacements;
	private Random rand = new Random();
	
	@Override
	public void onEnable(){
		this.saveDefaultConfig();
		getLogger().info("Enabled!");
		_enabled = getConfig().getBoolean("flattenBedrock", false);
		_level = getConfig().getInt("bedrockLevel", 0);
		_worldName = getConfig().getString("worldName", "world");
		_replacements = new ArrayList<ReplacementBlock>();
		
		if (!_enabled)
		{
			getLogger().warning("Turned off via config");
			return;
		}
		
		for(Map<?, ?> entry : getConfig().getMapList("replaceWith"))
        {
			int blockId = -1;
			byte dataValue = -1;
			int weight = -1;
			
            for(Map.Entry<?, ?> map : entry.entrySet())
            {
            	if (map.getKey().toString().compareToIgnoreCase("blockId") == 0)
            	{
            		blockId = Integer.parseInt(map.getValue().toString());
            	}
            	else if (map.getKey().toString().compareToIgnoreCase("dataValue") == 0)
            	{
            		dataValue = Byte.parseByte(map.getValue().toString());
            	}
            	else if (map.getKey().toString().compareToIgnoreCase("weight") == 0)
            	{
            		weight = Integer.parseInt(map.getValue().toString());
            	}
            }
            
            getLogger().info(String.format("ID: %s | Value: %s | Weight: %s", blockId, dataValue, weight));
            
            for (int i = 0; i < weight; i++)
            {
            	_replacements.add(new ReplacementBlock(blockId, dataValue));
            }
        }
		
		if (_replacements.size() == 0)
		{
			getLogger().warning("No Replacement blocks specified.");
			_enabled = false;
			return;
		}
		
		getLogger().info(String.format("Enabled: %s | Level: %s | World: %s", _enabled, _level, _worldName));
		getLogger().info(String.format("Number of Replacement Blocks: %s", _replacements.size()));
		
		Chunk[] loadedChunks = Bukkit.getServer().getWorld(_worldName).getLoadedChunks();
		for (int i = 0; i < loadedChunks.length; i++)
		{
			processChunk(loadedChunks[i]);
		}
		
		getServer().getPluginManager().registerEvents(this, this);
	}

	private void processChunk(Chunk chunk) 
	{
		if (!_enabled)
		{
			return;
		}
		getLogger().info(String.format("Processing Chunk at %s,%s", chunk.getX(), chunk.getZ()));
		for (int y = 5; y >= _level; y--)
		{
			for (int x = 0; x <= 15; x++)
			{
				for (int z = 0; z <= 15; z++)
				{
					if (y == _level)
					{
						Block block = chunk.getBlock(x,y,z);
						if (block.getTypeId() != 7)
						{
							block.setTypeIdAndData(7, (byte)0, false);
							getLogger().info(String.format("Added Bedrock at %s,%s,%s", x,y,z));
						}
					}
					else
					{
						Block block = chunk.getBlock(x,y,z);
						if (block.getTypeId() == 7)
						{
							ReplacementBlock replacement = _replacements.get(rand.nextInt(_replacements.size()));
							block.setTypeIdAndData(replacement.BlockID, replacement.DataValue, false);
							getLogger().info(String.format("Replaced Bedrock at %s,%s,%s with %s.%s", x,y,z,replacement.BlockID,replacement.DataValue));
						}
					}
				}
			}
		}
	}
 
	@Override
	public void onDisable(){
		getLogger().info("Disabled!");
	}
	
	@EventHandler
	public void onChunkLoad(final ChunkLoadEvent event) {
		processChunk(event.getChunk());
	}
}
