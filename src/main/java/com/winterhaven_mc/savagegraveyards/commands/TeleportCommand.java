package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

import static com.winterhaven_mc.savagegraveyards.messages.Macro.*;
import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;


/**
 * Teleport command implementation<br>
 * teleports player to graveyard location
 */
public class TeleportCommand extends AbstractCommand implements Subcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	TeleportCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("teleport");
		this.setUsage("/graveyard teleport <name>");
		this.setDescription(COMMAND_HELP_TELEPORT);
		this.addAlias("tp");
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

		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageBuilder.build(sender, COMMAND_FAIL_CONSOLE).send();
			return true;
		}

		// check for permission
		if (!sender.hasPermission("graveyard.teleport")) {
			plugin.messageBuilder.build(sender, PERMISSION_DENIED_TELEPORT).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// argument limits
		int minArgs = 1;

		if (args.size() < minArgs) {
			plugin.messageBuilder.build(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			displayUsage(sender);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// cast sender to player
		Player player = (Player) sender;

		// get display name from remaining arguments
		String displayName = String.join(" ", args);

		// get graveyard from datastore
		Graveyard graveyard = plugin.dataStore.selectGraveyard(displayName);

		// if graveyard does not exist, send message and return
		if (graveyard == null) {

			// create dummy graveyard to send to message manager
			Graveyard dummyGraveyard = new Graveyard.Builder().displayName(displayName).build();

			// send message
			plugin.messageBuilder.build(sender, COMMAND_FAIL_NO_RECORD)
					.setMacro(GRAVEYARD, dummyGraveyard)
					.send();

			// play sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get graveyard location
		Location destination = graveyard.getLocation();

		// if destination is null, send fail message and return
		if (destination == null) {

			// send message
			plugin.messageBuilder.build(sender, COMMAND_FAIL_TELEPORT_WORLD_INVALID)
					.setMacro(GRAVEYARD, graveyard)
					.setMacro(INVALID_WORLD, graveyard.getWorldName())
					.send();

			// play sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// play teleport departure sound
		plugin.soundConfig.playSound(player, SoundId.TELEPORT_SUCCESS_DEPARTURE);
		if (player.teleport(destination, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
			plugin.messageBuilder.build(sender, COMMAND_SUCCESS_TELEPORT)
					.setMacro(GRAVEYARD, graveyard)
					.setMacro(LOCATION, graveyard.getLocation())
					.send();
			plugin.soundConfig.playSound(player, SoundId.TELEPORT_SUCCESS_ARRIVAL);
		}
		else {
			// send message
			plugin.messageBuilder.build(sender, COMMAND_FAIL_TELEPORT).setMacro(GRAVEYARD, graveyard).send();

			// play sound
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		}

		return true;
	}

}
