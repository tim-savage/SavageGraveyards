package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import com.winterhaven_mc.savagegraveyards.storage.DataStore;
import com.winterhaven_mc.util.LanguageManager;
import org.bukkit.command.CommandSender;

import java.util.Objects;

import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;


public class ReloadCommand implements Subcommand {

	private final PluginMain plugin;
	private final CommandSender sender;

	final static String usageString = "/graveyard reload";


	ReloadCommand(final PluginMain plugin, final CommandSender sender) {
		this.plugin = Objects.requireNonNull(plugin);
		this.sender = Objects.requireNonNull(sender);
	}


	public boolean execute() {

		// if sender does not have permission to reload config, send error message and return true
		if (!sender.hasPermission("graveyard.reload")) {
			Message.create(sender, PERMISSION_DENIED_RELOAD).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// reload main configuration
		plugin.reloadConfig();

		// reload enabled worlds
		plugin.worldManager.reload();

		// reload messages
		LanguageManager.reload();

		// reload sounds
		plugin.soundConfig.reload();

		// reload datastore
		DataStore.reload();

		// set debug field
		plugin.debug = plugin.getConfig().getBoolean("debug");

		// send reloaded message
		Message.create(sender, COMMAND_SUCCESS_RELOAD).send();

		// return true to suppress bukkit usage message
		return true;
	}

}
