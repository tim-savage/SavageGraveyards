package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static com.winterhaven_mc.savagegraveyards.messages.Macro.*;
import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;


public class ClosestCommand implements Subcommand {

	private final PluginMain plugin;
	private final CommandSender sender;

	final static String usageString = "/graveyard closest";


	ClosestCommand(final PluginMain plugin, final CommandSender sender) {
		this.plugin = Objects.requireNonNull(plugin);
		this.sender = Objects.requireNonNull(sender);
	}


	@Override
	public boolean execute() {

		// if command sender does not have permission to display closest graveyard,
		// output error message and return true
		if (!sender.hasPermission("graveyard.closest")) {
			Message.create(sender, PERMISSION_DENIED_CLOSEST).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// sender must be in game player
		if (!(sender instanceof Player)) {
			Message.create(sender, COMMAND_FAIL_CONSOLE).send();
			return true;
		}

		// cast sender to player
		Player player = (Player) sender;

		// get nearest graveyard
		Graveyard graveyard = plugin.dataStore.selectNearestGraveyard(player);

		// if no graveyard returned from datastore, send failure message and return
		if (graveyard == null || graveyard.getLocation() == null) {
			Message.create(sender, COMMAND_FAIL_CLOSEST_NO_MATCH).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// send success message
		Message.create(sender, COMMAND_SUCCESS_CLOSEST)
				.setMacro(GRAVEYARD, graveyard)
				.setMacro(LOCATION, graveyard.getLocation())
				.send();

		return true;
	}

}
