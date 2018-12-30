package com.winterhaven_mc.savagegraveyards.messages;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import com.winterhaven_mc.util.AbstractMessageManager;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;


/**
 * Implements message manager for {@code SavageGraveyards}.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class MessageManager extends AbstractMessageManager {

	// reference to plugin main class
	private final PluginMain plugin;

	/**
	 * Constructor method for class
	 * 
	 * @param plugin reference to main class
	 */
	public MessageManager(final PluginMain plugin) {

		// call super class constructor
		//noinspection unchecked
		super(plugin, MessageId.class);

		// set reference to plugin main class
		this.plugin = plugin;
	}

//# %PLAYER_NAME%          Player's name, with no color codes
//# %PLAYER_NICKNAME%      Player's nickname, with no color codes
//# %WORLD_NAME%           World name that player is in, with no color codes
//# %DESTINATION_NAME%		display name of graveyard, with no color codes

	@Override
	protected Map<String,String> getDefaultReplacements(final CommandSender recipient) {

		Map<String,String> replacements = new HashMap<>();

		replacements.put("%PLAYER_NAME%", recipient.getName());
		replacements.put("%WORLD_NAME%", ChatColor.stripColor(getWorldName(recipient)));

		return replacements;
	}


	/**
	 *  Send message to player
	 * 
	 * @param recipient			player receiving message
	 * @param messageId			message identifier
	 */
	public void sendMessage(final CommandSender recipient, final MessageId messageId) {

		// get default replacement map
		Map<String,String> replacements = getDefaultReplacements(recipient);

		//noinspection unchecked
		sendMessage(recipient, messageId, replacements);
	}


	/** Send message to player
	 *
	 * @param recipient		Player receiving message
	 * @param messageId		message identifier
	 * @param graveyard		graveyard object
	 */
	public void sendMessage(final CommandSender recipient,
							final MessageId messageId,
							final Graveyard graveyard) {

		// get default replacement map
		Map<String,String> replacements = getDefaultReplacements(recipient);

		// set replacement strings
		if (graveyard != null) {
			replacements.put("%GRAVEYARD_NAME%", graveyard.getDisplayName());
			replacements.put("%WORLD_NAME%", plugin.worldManager.getWorldName(graveyard.getLocation().getWorld()));
			replacements.put("%LOC_X%", String.valueOf(graveyard.getLocation().getBlockX()));
			replacements.put("%LOC_Y%", String.valueOf(graveyard.getLocation().getBlockY()));
			replacements.put("%LOC_Z%", String.valueOf(graveyard.getLocation().getBlockZ()));
		}

		//noinspection unchecked
		sendMessage(recipient, messageId, replacements);
	}


	/** Send message to player
	 *
	 * @param recipient		Player receiving message
	 * @param messageId		message identifier
	 * @param duration		duration of safety
	 */
	public void sendMessage(final CommandSender recipient,
							final MessageId messageId,
							final Integer duration) {

		// get default replacement map
		Map<String,String> replacements = getDefaultReplacements(recipient);

		// set replacement strings
		replacements.put("%DURATION%", duration.toString());

		//noinspection unchecked
		sendMessage(recipient, messageId, replacements);
	}


	/** Send message to player
	 *
	 * @param recipient		Player receiving message
	 * @param messageId		message identifier
	 * @param graveyard		graveyard object
	 * @param value			new value
	 */
	public void sendMessage(final CommandSender recipient,
							final MessageId messageId,
							final Graveyard graveyard,
							final String value) {

		// get default replacement map
		Map<String,String> replacements = getDefaultReplacements(recipient);

		if (graveyard != null) {
			replacements.put("%GRAVEYARD_NAME%", graveyard.getDisplayName());
			replacements.put("%WORLD_NAME%", plugin.worldManager.getWorldName(graveyard.getLocation().getWorld()));
			replacements.put("%LOC_X%", String.valueOf(graveyard.getLocation().getBlockX()));
			replacements.put("%LOC_Y%", String.valueOf(graveyard.getLocation().getBlockY()));
			replacements.put("%LOC_Z%", String.valueOf(graveyard.getLocation().getBlockZ()));
		}

		replacements.put("%VALUE%", value);

		//noinspection unchecked
		sendMessage(recipient, messageId, replacements);
	}


	/** Send message to player
	 *
	 * @param recipient		Player receiving message
	 * @param messageId		message identifier
	 * @param graveyard		graveyard object
	 * @param targetPlayer	targeted player
	 */
	public void sendMessage(final CommandSender recipient,
							final MessageId messageId,
							final Graveyard graveyard,
							final OfflinePlayer targetPlayer) {

		// get default replacement map
		Map<String,String> replacements = getDefaultReplacements(recipient);

		if (graveyard != null) {
			replacements.put("%GRAVEYARD_NAME%", graveyard.getDisplayName());
			replacements.put("%WORLD_NAME%", plugin.worldManager.getWorldName(graveyard.getLocation().getWorld()));
			replacements.put("%LOC_X%", String.valueOf(graveyard.getLocation().getBlockX()));
			replacements.put("%LOC_Y%", String.valueOf(graveyard.getLocation().getBlockY()));
			replacements.put("%LOC_Z%", String.valueOf(graveyard.getLocation().getBlockZ()));
		}

		replacements.put("%TARGET_PLAYER%", targetPlayer.getName());

		//noinspection unchecked
		sendMessage(recipient, messageId, replacements);
	}


	/** Send message to player
	 * 
	 * @param recipient		Player receiving message
	 * @param messageId		message identifier
	 * @param page 			page number to display for multi-page messages
	 * @param pageCount 	total number of pages for multi-page messages
	 */
	public void sendMessage(final CommandSender recipient,
							final MessageId messageId,
							final Integer page,
							final Integer pageCount) {

		// get default replacement map
		Map<String,String> replacements = getDefaultReplacements(recipient);

		replacements.put("%PAGE%", page.toString());
		replacements.put("%PAGE_COUNT%", pageCount.toString());

		//noinspection unchecked
		sendMessage(recipient, messageId, replacements);
	}


	/**
	 *  display one list item
	 * @param recipient		Player receiving message
	 * @param messageId		message identifier
	 * @param itemNumber	item number in list
	 */
	public void listItem(final CommandSender recipient,
							final MessageId messageId,
							final Graveyard graveyard,
							final int itemNumber) {

		// get default replacement map
		Map<String,String> replacements = getDefaultReplacements(recipient);

		// set replacement strings
		replacements.put("%ITEM_NUMBER%", String.valueOf(itemNumber));

		if (graveyard != null) {
			replacements.put("%GRAVEYARD_NAME%", graveyard.getDisplayName());
			replacements.put("%WORLD_NAME%", plugin.worldManager.getWorldName(graveyard.getLocation().getWorld()));
			replacements.put("%LOC_X%", String.valueOf(graveyard.getLocation().getBlockX()));
			replacements.put("%LOC_Y%", String.valueOf(graveyard.getLocation().getBlockY()));
			replacements.put("%LOC_Z%", String.valueOf(graveyard.getLocation().getBlockZ()));
		}

		//noinspection unchecked
		sendMessage(recipient, messageId, replacements);
	}

}
