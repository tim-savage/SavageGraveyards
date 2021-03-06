package com.winterhaven_mc.savagegraveyards;

import com.winterhaven_mc.savagegraveyards.commands.CommandManager;
import com.winterhaven_mc.savagegraveyards.listeners.PlayerEventListener;
import com.winterhaven_mc.savagegraveyards.storage.DataStore;
import com.winterhaven_mc.savagegraveyards.tasks.DiscoveryTask;
import com.winterhaven_mc.savagegraveyards.util.SafetyManager;
import com.winterhaven_mc.util.LanguageManager;
import com.winterhaven_mc.util.SoundConfiguration;
import com.winterhaven_mc.util.WorldManager;
import com.winterhaven_mc.util.YamlSoundConfiguration;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;


/**
 * Bukkit plugin to allow creation of graveyard locations where players
 * will respawn on death. The nearest graveyard location that is valid
 * for the player will be chosen at the time of death.
 * 
 * @author      Tim Savage
 */
public final class PluginMain extends JavaPlugin {

	public Boolean debug = getConfig().getBoolean("debug");

	public DataStore dataStore;
	public WorldManager worldManager;
	public SoundConfiguration soundConfig;
	public SafetyManager safetyManager;
	private BukkitTask discoveryTask;


	@Override
	public void onEnable() {

		// install default config.yml if not present
		saveDefaultConfig();

		// initialize language manager
		LanguageManager.init();

		// instantiate world manager
		worldManager = new WorldManager(this);

		// instantiate sound configuration
		soundConfig = new YamlSoundConfiguration(this);

		// get initialized destination storage object
		dataStore = DataStore.create();

		// instantiate safety manager
		safetyManager = new SafetyManager(this);

		// instantiate player event listener
		new PlayerEventListener(this);

		// instantiate command manager
		new CommandManager(this);

		// run discovery task
		discoveryTask = new DiscoveryTask(this)
			.runTaskTimer(this, 0, getConfig().getInt("discovery-interval"));
	}


	@Override
	public void onDisable() {
		discoveryTask.cancel();
		dataStore.close();
	}

}
