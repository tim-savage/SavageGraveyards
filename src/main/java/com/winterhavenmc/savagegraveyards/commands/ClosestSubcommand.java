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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * Closest command implementation<br>
 * Returns name of closest graveyard to player position
 */
final class ClosestSubcommand extends SubcommandAbstract implements Subcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	ClosestSubcommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.name = "closest";
		this.usageString = "/graveyard closest";
		this.description = MessageId.COMMAND_HELP_CLOSEST;
		this.permission = "graveyard.closest";
		this.aliases = Set.of("nearest");
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if command sender does not have permission to display the closest graveyard,
		// output error message and return true
		if (!sender.hasPermission(permission)) {
			plugin.messageBuilder.build(sender, MessageId.PERMISSION_DENIED_CLOSEST).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_CONSOLE).send();
			return true;
		}

		// check maximum arguments
		if (args.size() > maxArgs) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			displayUsage(sender);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// cast sender to player
		Player player = (Player) sender;

		// get nearest graveyard
		Graveyard graveyard = plugin.dataStore.selectNearestGraveyard(player);

		// if no graveyard returned from datastore, send failure message and return
		if (graveyard == null || graveyard.getLocation() == null) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_CLOSEST_NO_MATCH).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// send success message
		plugin.messageBuilder.build(sender, MessageId.COMMAND_SUCCESS_CLOSEST)
				.setMacro(Macro.GRAVEYARD, graveyard)
				.setMacro(Macro.LOCATION, graveyard.getLocation())
				.send();
		return true;
	}

}
