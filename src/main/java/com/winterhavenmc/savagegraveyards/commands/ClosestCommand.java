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


/**
 * Closest command implementation<br>
 * Returns name of closest graveyard to player position
 */
final class ClosestCommand extends SubcommandAbstract implements Subcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	ClosestCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("closest");
		this.setUsage("/graveyard closest");
		this.setDescription(MessageId.COMMAND_HELP_CLOSEST);
		this.addAlias("nearest");
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if command sender does not have permission to display closest graveyard,
		// output error message and return true
		if (!sender.hasPermission("graveyard.closest")) {
			plugin.messageBuilder.build(sender, MessageId.PERMISSION_DENIED_CLOSEST).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// sender must be in game player
		if (!(sender instanceof Player)) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_CONSOLE).send();
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
