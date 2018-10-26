package com.winterhaven_mc.savagegraveyards.messages;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import com.winterhaven_mc.util.AbstractMessageManager;

import org.bukkit.ChatColor;
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

	/**
	 * Constructor method for class
	 * 
	 * @param plugin reference to main class
	 */
	public MessageManager(final PluginMain plugin) {

		// call super class constructor
		//noinspection unchecked
		super(plugin, MessageId.class);
	}

//# %PLAYER_NAME%          Player's name, with no color codes
//# %PLAYER_NICKNAME%      Player's nickname, with no color codes
//# %WORLD_NAME%           World name that player is in, with no color codes
//# %DESTINATION_NAME%		display name of graveyard, with no color codes

	@Override
	protected Map<String,String> getDefaultReplacements(CommandSender recipient) {

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
		replacements.put("%GRAVEYARD_NAME%", graveyard.getDisplayName());

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

		replacements.put("%GRAVEYARD_NAME%", graveyard.getDisplayName());
		replacements.put("%VALUE%", value);

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

}
