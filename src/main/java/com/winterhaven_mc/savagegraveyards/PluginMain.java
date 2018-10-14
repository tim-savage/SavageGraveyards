package com.winterhaven_mc.savagegraveyards;

import com.winterhaven_mc.savagegraveyards.commands.CommandManager;
import com.winterhaven_mc.savagegraveyards.listeners.PlayerEventListener;
import com.winterhaven_mc.savagegraveyards.storage.DataStore;
import com.winterhaven_mc.savagegraveyards.storage.DataStoreFactory;
import com.winterhaven_mc.savagegraveyards.tasks.DiscoveryTask;
import com.winterhaven_mc.savagegraveyards.util.MessageManager;
import com.winterhaven_mc.savagegraveyards.util.SafetyManager;
import com.winterhaven_mc.util.WorldManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


/**
 * Bukkit plugin to allow creation of graveyard locations where players
 * will respawn on death. The nearest graveyard location that is valid
 * for the player will be chosen at the time of death.
 * 
 * @author      Tim Savage
 * @version		1.0
 */
public final class PluginMain extends JavaPlugin {
	
	// static reference to main class
	public static PluginMain instance;

	public Boolean debug = getConfig().getBoolean("debug");
	
	public DataStore dataStore;
	public WorldManager worldManager;
	public MessageManager messageManager;
//	public SoundManager soundManager;
	public SafetyManager safetyManager;
	private BukkitTask discoveryTask;
	

	@Override
	public void onEnable() {

		// set static reference to main class
		instance = this;
		
		// install default config.yml if not present  
		saveDefaultConfig();
		
		// get initialized destination storage object
		dataStore = DataStoreFactory.create();
		
		// instantiate world manager
		worldManager = new WorldManager(this);
		
		// instantiate message manager
		messageManager = new MessageManager(this);

		// instantiate sound manager
//		soundManager = new SoundManager(this);

		// instantiate safety manager
		safetyManager = new SafetyManager(this);

		// instantiate player event listener
		new PlayerEventListener(this);

		// instantiate command manager
		new CommandManager(this);

		// run discovery task
		discoveryTask = new DiscoveryTask(this)
			.runTaskTimerAsynchronously(this, 0, getConfig().getInt("discovery-interval"));
	}
	
	@Override
	public void onDisable() {
		discoveryTask.cancel();
		dataStore.close();
	}

}

