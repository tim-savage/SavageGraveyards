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
import com.winterhavenmc.savagegraveyards.storage.Graveyard;
import com.winterhavenmc.savagegraveyards.messages.Macro;
import com.winterhavenmc.savagegraveyards.messages.MessageId;
import com.winterhavenmc.savagegraveyards.sounds.SoundId;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Forget command implementation<br>
 * Removes graveyard discovery record for player
 */
final class ForgetSubcommand extends AbstractSubcommand implements Subcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	ForgetSubcommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "forget";
		this.usageString = "/graveyard forget <player> <graveyard name>";
		this.description = MessageId.COMMAND_HELP_FORGET;
		this.permissionNode = "graveyard.forget";
		this.minArgs = 2;
	}


	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command command,
									  final String alias, final String[] args) {

		List<String> resultList = new ArrayList<>();

		if (args.length == 2) {

			// get collection of players with discoveries
			Collection<String> playerNames = plugin.dataStore.selectPlayersWithDiscoveries();

			Predicate<String> startsWith = string -> string.toLowerCase().startsWith(args[1].toLowerCase());

			resultList = playerNames.stream().filter(startsWith).collect(Collectors.toList());
		}

		else if (args.length == 3) {

			// get player uuids from name
			Set<UUID> matchedPlayerUids = plugin.getServer().matchPlayer(args[1]).stream()
					.map(Entity::getUniqueId)
					.collect(Collectors.toSet());

			Collection<String> graveyardKeys = new HashSet<>();

			for (UUID playerUid : matchedPlayerUids) {
				graveyardKeys.addAll(plugin.dataStore.selectDiscoveredKeys(playerUid));
			}

			Predicate<String> startsWith = string -> string.toLowerCase().startsWith(args[2].toLowerCase());

			resultList = graveyardKeys.stream().filter(startsWith).collect(Collectors.toList());
		}

		return resultList;
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// check for permission
		if (!sender.hasPermission(permissionNode)) {
			plugin.messageBuilder.compose(sender, MessageId.PERMISSION_DENIED_FORGET).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// check for minimum arguments
		if (args.size() < minArgs) {
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			displayUsage(sender);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get player name
		String playerName = args.remove(0);

		// get list of offline players
		OfflinePlayer[] offlinePlayers = plugin.getServer().getOfflinePlayers();

		OfflinePlayer player = null;

		for (OfflinePlayer offlinePlayer : offlinePlayers) {
			if (playerName.equals(offlinePlayer.getName())) {
				player = offlinePlayer;
			}
		}

		// if player not found, send message and return
		if (player == null) {
			plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_FORGET_INVALID_PLAYER).send();
			return true;
		}

		// get graveyard search key
		String searchKey = String.join("_", args);

		// fetch graveyard from datastore
		Optional<Graveyard> optionalGraveyard = plugin.dataStore.selectGraveyard(searchKey);

		// if no matching graveyard found, send message and return
		if (optionalGraveyard.isEmpty()) {
			sendInvalidGraveyardMessage(sender, searchKey);
		}
		else {
			// get unwrapped optional graveyard from datastore
			Graveyard graveyard = optionalGraveyard.get();

			// delete discovery record
			if (plugin.dataStore.deleteDiscovery(searchKey, player.getUniqueId())) {
				sendForgetSuccessMessage(sender, player, graveyard);
			}
			else {
				sendForgetFailedMessage(sender, player, graveyard);
			}
		}
		return true;
	}


	private void sendForgetSuccessMessage(CommandSender sender, OfflinePlayer player, Graveyard graveyard) {

		// send success message
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_SUCCESS_FORGET)
				.setMacro(Macro.GRAVEYARD, graveyard)
				.setMacro(Macro.TARGET_PLAYER, player)
				.send();

		// play success sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_FORGET);
	}


	private void sendForgetFailedMessage(CommandSender sender, OfflinePlayer player, Graveyard graveyard) {

		// send failure message
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_FORGET)
				.setMacro(Macro.GRAVEYARD, graveyard)
				.setMacro(Macro.TARGET_PLAYER, player)
				.send();

		// send command fail sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
	}


	private void sendInvalidGraveyardMessage(CommandSender sender, String searchKey) {

		// create dummy graveyard for message
		Graveyard dummyGraveyard = new Graveyard.Builder(plugin).displayName(searchKey).build();

		// send graveyard not found message
		plugin.messageBuilder.compose(sender, MessageId.COMMAND_FAIL_FORGET_INVALID_GRAVEYARD)
				.setMacro(Macro.GRAVEYARD, dummyGraveyard)
				.send();

		// play command fail sound
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
	}
}
