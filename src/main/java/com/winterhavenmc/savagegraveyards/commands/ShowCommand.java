/*
 * Copyright (c) 2022 Tim Savage.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.winterhavenmc.savagegraveyards.commands;

import com.winterhavenmc.savagegraveyards.PluginMain;
import com.winterhavenmc.savagegraveyards.sounds.SoundId;
import com.winterhavenmc.savagegraveyards.storage.Graveyard;
import com.winterhavenmc.savagegraveyards.messages.Macro;
import com.winterhavenmc.savagegraveyards.messages.MessageId;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * Show command implementation<br>
 * displays graveyard settings
 */
final class ShowCommand extends SubcommandAbstract implements Subcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	ShowCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "show";
		this.usageString = "/graveyard show <graveyard>";
		this.description = MessageId.COMMAND_HELP_SHOW;
		this.minArgs = 1;
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
									  final String alias, final String[] args) {

		if (args.length == 2) {
			// return list of valid matching graveyard names
			return plugin.dataStore.selectMatchingGraveyardNames(args[1]);
		}

		return Collections.emptyList();
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if command sender does not have permission to show graveyards, output error message and return true
		if (!sender.hasPermission("graveyard.show")) {
			plugin.messageBuilder.build(sender, MessageId.PERMISSION_DENIED_SHOW).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check minimum arguments
		if (args.size() < minArgs) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			displayUsage(sender);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get display name from remaining arguments joined with spaces
		String displayName = String.join(" ", args).trim();

		// retrieve graveyard from data store
		Graveyard graveyard = plugin.dataStore.selectGraveyard(displayName);

		// if retrieved graveyard is null, display error and usage messages and return
		if (graveyard == null) {

			// create dummy graveyard to send to message manager
			Graveyard dummyGraveyard = new Graveyard.Builder().displayName(displayName).build();

			// send message
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_NO_RECORD).setMacro(Macro.GRAVEYARD, dummyGraveyard).send();

			// play sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// display graveyard display name
		sender.sendMessage(ChatColor.DARK_AQUA + "Name: "
				+ ChatColor.RESET + graveyard.getDisplayName());

		// display graveyard 'enabled' setting
		sender.sendMessage(ChatColor.DARK_AQUA + "Enabled: "
				+ ChatColor.RESET + graveyard.isEnabled());

		// display graveyard 'hidden' setting
		sender.sendMessage(ChatColor.DARK_AQUA + "Hidden: "
				+ ChatColor.RESET + graveyard.isHidden());

		// if graveyard discovery range is set to non-negative value, display it; else display configured default
		if (graveyard.getDiscoveryRange() >= 0) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Discovery Range: "
					+ ChatColor.RESET + graveyard.getDiscoveryRange() + " blocks");
		}
		else {
			sender.sendMessage(ChatColor.DARK_AQUA + "Discovery Range: "
					+ ChatColor.RESET + plugin.getConfig().getInt("discovery-range") + " blocks (default)");
		}

		// get custom discovery message and display if not null or empty
		if (graveyard.getDiscoveryMessage() != null && !graveyard.getDiscoveryMessage().isEmpty()) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Custom Discovery Message: "
					+ ChatColor.RESET + graveyard.getDiscoveryMessage());
		}

		// get custom respawn message and display if not null or empty
		if (graveyard.getRespawnMessage() != null && !graveyard.getRespawnMessage().isEmpty()) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Custom Respawn Message: "
					+ ChatColor.RESET + graveyard.getRespawnMessage());
		}

		// if graveyard safety time is set to non-negative value, display it; else display configured default
		if (graveyard.getSafetyTime() >= 0L) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Safety time: "
					+ ChatColor.RESET + graveyard.getSafetyTime() + " seconds");
		}
		else {
			sender.sendMessage(ChatColor.DARK_AQUA + "Safety time: "
					+ ChatColor.RESET + plugin.getConfig().getLong("safety-time") + " seconds (default)");
		}

		// get graveyard group; if null or empty, set to ALL
		String group = graveyard.getGroup();
		if (group == null || group.isEmpty()) {
			group = "ALL";
		}
		sender.sendMessage(ChatColor.DARK_AQUA + "Group: "
				+ ChatColor.RESET + group);

		// if world is invalid, set color to gray
		ChatColor worldColor = ChatColor.AQUA;
		if (graveyard.getLocation() == null) {
			worldColor = ChatColor.GRAY;
		}

		// display graveyard location
		String locationString = ChatColor.DARK_AQUA + "Location: "
				+ ChatColor.RESET + "["
				+ worldColor + graveyard.getWorldName()
				+ ChatColor.RESET + "] "
				+ ChatColor.RESET + "X: " + ChatColor.AQUA + Math.round(graveyard.getX()) + " "
				+ ChatColor.RESET + "Y: " + ChatColor.AQUA + Math.round(graveyard.getY()) + " "
				+ ChatColor.RESET + "Z: " + ChatColor.AQUA + Math.round(graveyard.getZ()) + " "
				+ ChatColor.RESET + "P: " + ChatColor.GOLD + String.format("%.2f", graveyard.getPitch()) + " "
				+ ChatColor.RESET + "Y: " + ChatColor.GOLD + String.format("%.2f", graveyard.getYaw());
		sender.sendMessage(locationString);

		return true;
	}
}
