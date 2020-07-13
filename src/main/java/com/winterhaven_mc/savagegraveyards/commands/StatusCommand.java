package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;


//	/**
//	 * Display plugin settings
//	 *
//	 * @param sender the command sender
//	 * @return always returns {@code true}, to prevent display of bukkit usage message
//	 * @throws NullPointerException if any parameter is null
//	 */
public class StatusCommand implements Subcommand {

	private final PluginMain plugin;
	private final CommandSender sender;

	final static String usageString = "/graveyard status";


	/**
	 * Class constructor
	 *
	 * @param sender the command sender
	 * @throws NullPointerException if any parameter is null
	 */
	StatusCommand(PluginMain plugin, CommandSender sender) {
		this.plugin = Objects.requireNonNull(plugin);
		this.sender = Objects.requireNonNull(sender);
	}


	@Override
	public boolean execute() {

		// if command sender does not have permission to view status, output error message and return true
		if (!sender.hasPermission("graveyard.status")) {
			Message.create(sender, PERMISSION_DENIED_STATUS).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// output config settings
		String versionString = plugin.getDescription().getVersion();

		sender.sendMessage(ChatColor.DARK_AQUA
				+ "[" + plugin.getName() + "] " + ChatColor.AQUA + "Version: " + ChatColor.RESET + versionString);

		if (plugin.debug) {
			sender.sendMessage(ChatColor.DARK_RED + "DEBUG: true");
		}

		sender.sendMessage(ChatColor.GREEN + "Language: "
				+ ChatColor.RESET + plugin.getConfig().getString("language"));

		sender.sendMessage(ChatColor.GREEN + "Storage type: "
				+ ChatColor.RESET + plugin.dataStore.toString());

		sender.sendMessage(ChatColor.GREEN + "Default discovery range: "
				+ ChatColor.RESET + plugin.getConfig().getInt("discovery-range") + " blocks");

		sender.sendMessage(ChatColor.GREEN + "Default safety time: "
				+ ChatColor.RESET + plugin.getConfig().getInt("safety-time") + " seconds");

		sender.sendMessage(ChatColor.GREEN + "Discovery check interval: "
				+ ChatColor.RESET + plugin.getConfig().getInt("discovery-interval") + " ticks");

		sender.sendMessage(ChatColor.GREEN + "List items page size: "
				+ ChatColor.RESET + plugin.getConfig().getInt("list-page-size") + " items");

		sender.sendMessage(ChatColor.GREEN + "Enabled Words: "
				+ ChatColor.RESET + plugin.worldManager.getEnabledWorldNames().toString());

		// always return true to suppress bukkit usage message
		return true;
	}

}
