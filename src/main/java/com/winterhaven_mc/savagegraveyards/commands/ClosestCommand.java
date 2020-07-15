package com.winterhaven_mc.savagegraveyards.commands;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

import static com.winterhaven_mc.savagegraveyards.messages.Macro.GRAVEYARD;
import static com.winterhaven_mc.savagegraveyards.messages.Macro.LOCATION;
import static com.winterhaven_mc.savagegraveyards.messages.MessageId.*;


/**
 * Closest command implementation<br>
 * Returns name of closest graveyard to player position
 */
public class ClosestCommand extends AbstractCommand implements Subcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	ClosestCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		setUsage("/graveyard closest");
		setDescription(COMMAND_HELP_CLOSEST);
		addAlias("nearest");
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

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
