package com.winterhavenmc.savagegraveyards.commands;

import com.winterhavenmc.savagegraveyards.PluginMain;
import com.winterhavenmc.savagegraveyards.sounds.SoundId;
import com.winterhavenmc.savagegraveyards.storage.Graveyard;
import com.winterhavenmc.savagegraveyards.messages.Macro;
import com.winterhavenmc.savagegraveyards.messages.MessageId;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;


/**
 * List command implementation<br>
 * Displays listing of graveyards
 */
final class ListCommand extends SubcommandAbstract implements Subcommand {

	private final PluginMain plugin;


	/**
	 * Class constructor
	 * @param plugin reference to plugin main class instance
	 */
	ListCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("list");
		this.setUsage("/graveyard list [page]");
		this.setDescription(MessageId.COMMAND_HELP_LIST);
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

		// if command sender does not have permission to list graveyards, output error message and return true
		if (!sender.hasPermission("graveyard.list")) {
			plugin.messageBuilder.build(sender, MessageId.PERMISSION_DENIED_LIST).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// argument limits
		int maxArgs = 1;

		if (args.size() > maxArgs) {
			plugin.messageBuilder.build(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			displayUsage(sender);
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// set default page
		int page = 1;

		// if argument exists, try to parse as integer page number
		if (args.size() == 1) {
			try {
				page = Integer.parseInt(args.get(0));
			}
			catch (NumberFormatException e) {
				// second argument not a page number, let default of 1 stand
			}
		}
		page = Math.max(1, page);

		int itemsPerPage = plugin.getConfig().getInt("list-page-size");

		// get all records from datastore
		final Collection<Graveyard> allRecords = plugin.dataStore.selectAllGraveyards();

		if (plugin.getConfig().getBoolean("debug")) {
			plugin.getLogger().info("Records fetched from datastore: " + allRecords.size());
		}

		// get undiscovered searchKeys for player
		Collection<String> undiscoveredKeys = new HashSet<>();
		if (sender instanceof Player) {
			undiscoveredKeys.addAll(plugin.dataStore.selectUndiscoveredKeys((Player) sender));
		}

		// create empty list of records
		List<Graveyard> displayRecords = new ArrayList<>();

		for (Graveyard graveyard : allRecords) {

			// if graveyard has invalid location and sender has list disabled permission, add to display list
			if (graveyard.getLocation() == null) {
				if (sender.hasPermission("graveyard.list.disabled")) {
					displayRecords.add(graveyard);
				}
				continue;
			}

			// if graveyard is not enabled and sender does not have override permission, do not add to display list
			if (!graveyard.isEnabled() && !sender.hasPermission("graveyard.list.disabled")) {
				if (plugin.getConfig().getBoolean("debug")) {
					plugin.getLogger().info(graveyard.getDisplayName()
							+ " is disabled and player does not have graveyard.list.disabled permission.");
				}
				continue;
			}

			// if graveyard is undiscovered and sender does not have override permission, do not add to display list
			if (graveyard.isHidden()
					&& undiscoveredKeys.contains(graveyard.getSearchKey())
					&& !sender.hasPermission("graveyard.list.hidden")) {
				if (plugin.getConfig().getBoolean("debug")) {
					plugin.getLogger().info(graveyard.getDisplayName()
							+ " is undiscovered and player does not have graveyard.list.hidden permission.");
				}
				continue;
			}

			// if graveyard has group set and sender does not have group permission, do not add to display list
			String group = graveyard.getGroup();
			if (group != null && !group.isEmpty() && !sender.hasPermission("group." + graveyard.getGroup())) {
				if (plugin.getConfig().getBoolean("debug")) {
					plugin.getLogger().info(graveyard.getDisplayName()
							+ " is in group that player does not have permission.");
				}
				continue;
			}

			// add graveyard to display list
			displayRecords.add(graveyard);
		}

		// if display list is empty, output list empty message and return
		if (displayRecords.isEmpty()) {
			plugin.messageBuilder.build(sender, MessageId.LIST_EMPTY).send();
			return true;
		}

		// get page count
		int pageCount = ((displayRecords.size() - 1) / itemsPerPage) + 1;
		if (page > pageCount) {
			page = pageCount;
		}
		int startIndex = ((page - 1) * itemsPerPage);
		int endIndex = Math.min((page * itemsPerPage), displayRecords.size());

		List<Graveyard> displayRange = displayRecords.subList(startIndex, endIndex);

		int itemNumber = startIndex;

		// display list header
		plugin.messageBuilder.build(sender, MessageId.LIST_HEADER)
				.setMacro(Macro.PAGE_NUMBER, page)
				.setMacro(Macro.PAGE_TOTAL, pageCount)
				.send();

		for (Graveyard graveyard : displayRange) {

			// increment item number
			itemNumber++;

			// display invalid world list item
			if (graveyard.getLocation() == null) {
				plugin.messageBuilder.build(sender, MessageId.LIST_ITEM_INVALID_WORLD)
						.setMacro(Macro.GRAVEYARD, graveyard)
						.setMacro(Macro.ITEM_NUMBER, itemNumber)
						.setMacro(Macro.INVALID_WORLD, graveyard.getWorldName())
						.send();
				continue;
			}

			// display disabled list item
			if (!graveyard.isEnabled()) {

				plugin.messageBuilder.build(sender, MessageId.LIST_ITEM_DISABLED)
						.setMacro(Macro.GRAVEYARD, graveyard)
						.setMacro(Macro.ITEM_NUMBER, itemNumber)
						.setMacro(Macro.LOCATION, graveyard.getLocation())
						.send();
				continue;
			}

			// display undiscovered list item
			if (graveyard.isHidden() && undiscoveredKeys.contains(graveyard.getSearchKey())) {
				plugin.messageBuilder.build(sender, MessageId.LIST_ITEM_UNDISCOVERED)
						.setMacro(Macro.GRAVEYARD, graveyard)
						.setMacro(Macro.ITEM_NUMBER, itemNumber)
						.setMacro(Macro.LOCATION, graveyard.getLocation())
						.send();
				continue;
			}

			// display normal list item
			plugin.messageBuilder.build(sender, MessageId.LIST_ITEM)
					.setMacro(Macro.GRAVEYARD, graveyard)
					.setMacro(Macro.ITEM_NUMBER, itemNumber)
					.setMacro(Macro.LOCATION, graveyard.getLocation())
					.send();
		}

		// display list footer
		plugin.messageBuilder.build(sender, MessageId.LIST_FOOTER)
				.setMacro(Macro.PAGE_NUMBER, page)
				.setMacro(Macro.PAGE_TOTAL, pageCount)
				.send();

		return true;
	}

}
