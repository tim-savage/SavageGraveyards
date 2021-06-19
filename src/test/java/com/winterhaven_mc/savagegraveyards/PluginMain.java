package com.winterhaven_mc.savagegraveyards;

import com.winterhaven_mc.savagegraveyards.commands.CommandManager;
import com.winterhaven_mc.savagegraveyards.listeners.PlayerEventListener;
import com.winterhaven_mc.savagegraveyards.tasks.DiscoveryTask;
import com.winterhaven_mc.savagegraveyards.util.SafetyManager;
import com.winterhaven_mc.util.*;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class PluginMain extends JavaPlugin {

    protected WorldManager worldManager;
    public SoundConfiguration soundConfig;
    protected LanguageHandler languageHandler;
    protected SafetyManager safetyManager;
    protected PlayerEventListener playerEventListener;
    protected CommandManager commandManager;
    protected BukkitTask discoveryTask;


    @SuppressWarnings("unused")
    public PluginMain() {
        super();
    }


    @SuppressWarnings("unused")
    protected PluginMain(JavaPluginLoader loader, PluginDescriptionFile descriptionFile, File dataFolder, File file) {
        super(loader, descriptionFile, dataFolder, file);
    }


    @Override
    public void onEnable() {

        // install default config.yml if not present
        saveDefaultConfig();

        // instantiate language manager
        languageHandler = new LanguageHandler(this);

        // instantiate world manager
        worldManager = new WorldManager(this);

        // instantiate sound configuration
        soundConfig = new YamlSoundConfiguration(this);

//        // get initialized destination storage object
//        dataStore = DataStore.create();

        // instantiate safety manager
        safetyManager = new SafetyManager(this);

        // instantiate player event listener
        playerEventListener = new PlayerEventListener(this);

        // instantiate command manager
        commandManager = new CommandManager(this);

        // run discovery task
        discoveryTask = new DiscoveryTask(this)
                .runTaskTimer(this, 0, getConfig().getInt("discovery-interval"));

    }

    @Override
    public void onDisable() {
        discoveryTask.cancel();
    }

}