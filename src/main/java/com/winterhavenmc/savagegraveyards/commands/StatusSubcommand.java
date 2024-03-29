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
import com.winterhavenmc.savagegraveyards.messages.MessageId;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Objects;


/**
 * Status command implementation<br>
 * Display plugin settings
 */
final class StatusSubcommand extends AbstractSubcommand implements Subcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	StatusSubcommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "status";
		this.usageString = "/graveyard status";
		this.description = MessageId.COMMAND_HELP_STATUS;
		this.permissionNode = "graveyard.status";
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if command sender does not have permission to view status, output error message and return true
		if (!sender.hasPermission(permissionNode)) {
			plugin.messageBuilder.compose(sender, MessageId.PERMISSION_DENIED_STATUS).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// output config settings
		showPluginVersion(sender);
		showDebugSetting(sender);
		showLanguageSetting(sender);
		showDiscoveryRangeSetting(sender);
		showSafetyTimeSetting(sender);
		showDiscoveryIntervalSetting(sender);
		showListItemPageSizeSetting(sender);
		showEnabledWorlds(sender);

		// always return true to suppress bukkit usage message
		return true;
	}


	private void showPluginVersion(final CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_AQUA + "[" + plugin.getName() + "] " + ChatColor.AQUA + "Version: "
				+ ChatColor.RESET + plugin.getDescription().getVersion());
	}


	private void showDebugSetting(final CommandSender sender) {
		if (plugin.getConfig().getBoolean("debug")) {
			sender.sendMessage(ChatColor.DARK_RED + "DEBUG: true");
		}
	}


	private void showLanguageSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Language: "
				+ ChatColor.RESET + plugin.getConfig().getString("language"));
	}


	private void showDiscoveryRangeSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Default discovery range: "
				+ ChatColor.RESET + plugin.getConfig().getInt("discovery-range") + " blocks");
	}


	private void showSafetyTimeSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Default safety time: "
				+ ChatColor.RESET + plugin.getConfig().getInt("safety-time") + " seconds");
	}


	private void showDiscoveryIntervalSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Discovery check interval: "
				+ ChatColor.RESET + plugin.getConfig().getInt("discovery-interval") + " ticks");
	}


	private void showListItemPageSizeSetting(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "List items page size: "
				+ ChatColor.RESET + plugin.getConfig().getInt("list-page-size") + " items");
	}


	private void showEnabledWorlds(final CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Enabled Words: "
				+ ChatColor.RESET + plugin.worldManager.getEnabledWorldNames().toString());
	}


}
