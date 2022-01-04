package com.winterhavenmc.savagegraveyards;

import com.winterhavenmc.savagegraveyards.commands.CommandManager;
import com.winterhavenmc.savagegraveyards.listeners.PlayerEventListener;
import com.winterhavenmc.savagegraveyards.messages.Macro;
import com.winterhavenmc.savagegraveyards.messages.MessageId;
import com.winterhavenmc.savagegraveyards.storage.DataStore;
import com.winterhavenmc.savagegraveyards.tasks.DiscoveryTask;
import com.winterhavenmc.savagegraveyards.util.SafetyManager;

import com.winterhavenmc.util.messagebuilder.MessageBuilder;
import com.winterhavenmc.util.soundconfig.SoundConfiguration;
import com.winterhavenmc.util.soundconfig.YamlSoundConfiguration;
import com.winterhavenmc.util.worldmanager.WorldManager;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;


/**
 * Bukkit plugin to allow creation of graveyard locations where players
 * will respawn on death. The nearest graveyard location that is valid
 * for the player will be chosen at the time of death.
 */
public class PluginMain extends JavaPlugin {

	public MessageBuilder<MessageId, Macro> messageBuilder;
	public DataStore dataStore;
	public WorldManager worldManager;
	public SoundConfiguration soundConfig;
	public SafetyManager safetyManager;
	private BukkitTask discoveryTask;


	/**
	 * Class constructor for testing
	 */
	public PluginMain() {
		super();
	}


	/**
	 * Class constructor for testing
	 */
	@SuppressWarnings("unused")
	PluginMain(JavaPluginLoader loader, PluginDescriptionFile descriptionFile, File dataFolder, File file) {
		super(loader, descriptionFile, dataFolder, file);
	}


	@Override
	public void onEnable() {

		// install default config.yml if not present
		saveDefaultConfig();

		// instantiate message builder
		messageBuilder = new MessageBuilder<>(this);

		// instantiate world manager
		worldManager = new WorldManager(this);

		// instantiate sound configuration
		soundConfig = new YamlSoundConfiguration(this);

		// connect to storage object
		dataStore = DataStore.connect(this);

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
