package com.winterhaven_mc.savagegraveyards.messages;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.storage.Graveyard;
import com.winterhaven_mc.util.AbstractMessageManager;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Implements message manager for {@code SavageGraveyards}.
 *
 * @author Tim Savage
 * @version 1.0
 */
public class MessageManager extends AbstractMessageManager<MessageId> {


	/**
	 * Constructor method for class
	 *
	 * @param plugin reference to main class
	 */
	public MessageManager(final PluginMain plugin) {

		// call super class constructor
		super(plugin, MessageId.class);
	}

//# %PLAYER_NAME%          Player's name, with no color codes
//# %PLAYER_NICKNAME%      Player's nickname, with no color codes
//# %GRAVEYARD_NAME%       display name of graveyard, with no color codes
//# %WORLD_NAME%           World name that player is in, with no color codes
//# %LOC_X%                location coordinate X
//# %LOC_Y%                location coordinate Y
//# %LOC_Z%                location coordinate Z


	/**
	 * Set default replacement values
	 *
	 * @param recipient the message recipient
	 * @return Map of replacement strings
	 * @throws NullPointerException if any parameter is null
	 */
	@Override
	protected Map<String, String> getDefaultReplacements(final CommandSender recipient) {

		// check for null parameter
		Objects.requireNonNull(recipient);

		Map<String, String> replacements = new HashMap<>();

		replacements.put("%PLAYER_NAME%", recipient.getName());
		replacements.put("%WORLD_NAME%", ChatColor.stripColor(getWorldName(recipient)));
		replacements.put("%GRAVEYARD_NAME%", ChatColor.RED + "graveyard" + ChatColor.RESET);
		replacements.put("%TARGET_PLAYER%", ChatColor.RED + "target player" + ChatColor.RESET);

		if (recipient instanceof Entity) {
			Entity entity = (Entity) recipient;
			replacements.put("%LOC_X%", String.valueOf(entity.getLocation().getBlockX()));
			replacements.put("%LOC_Y%", String.valueOf(entity.getLocation().getBlockY()));
			replacements.put("%LOC_Z%", String.valueOf(entity.getLocation().getBlockZ()));
		}
		else {
			replacements.put("%LOC_X%", "X");
			replacements.put("%LOC_Y%", "Y");
			replacements.put("%LOC_Z%", "Z");
		}

		return replacements;
	}


	/**
	 * Send message to player
	 *
	 * @param recipient player receiving message
	 * @param messageId message identifier
	 * @throws NullPointerException if any parameter is null
	 */
	public void sendMessage(final CommandSender recipient, final MessageId messageId) {

		// check for null parameters
		Objects.requireNonNull(recipient);
		Objects.requireNonNull(messageId);

		// get default replacement map
		Map<String, String> replacements = getDefaultReplacements(recipient);

		// send message
		sendMessage(recipient, messageId, replacements);
	}


	/**
	 * Send message to player
	 *
	 * @param recipient Player receiving message
	 * @param messageId message identifier
	 * @param graveyard graveyard object
	 * @throws NullPointerException if any parameter is null
	 */
	public void sendMessage(final CommandSender recipient,
							final MessageId messageId,
							final Graveyard graveyard) {

		// check for null parameters
		Objects.requireNonNull(recipient);
		Objects.requireNonNull(messageId);
		Objects.requireNonNull(graveyard);

		// get default replacement map
		Map<String, String> replacements = getDefaultReplacements(recipient);

		// if graveyard display name is not null or empty, set replacement
		if (graveyard.getDisplayName() != null && !graveyard.getDisplayName().isEmpty()) {
			replacements.put("%GRAVEYARD_NAME%", graveyard.getDisplayName());
		}

		// if graveyard location is not null, set replacements
		if (graveyard.getLocation() != null) {
			replacements.put("%WORLD_NAME%", getWorldName(graveyard.getLocation()));
			replacements.put("%LOC_X%", String.valueOf(graveyard.getLocation().getBlockX()));
			replacements.put("%LOC_Y%", String.valueOf(graveyard.getLocation().getBlockY()));
			replacements.put("%LOC_Z%", String.valueOf(graveyard.getLocation().getBlockZ()));
		}

		// send message
		sendMessage(recipient, messageId, replacements);
	}


	/**
	 * Send message to player
	 *
	 * @param recipient Player receiving message
	 * @param messageId message identifier
	 * @param graveyard graveyard object
	 * @param value     new value
	 * @throws NullPointerException if any parameter is null
	 */
	public void sendMessage(final CommandSender recipient,
							final MessageId messageId,
							final Graveyard graveyard,
							final String value) {

		// check for null parameters
		Objects.requireNonNull(recipient);
		Objects.requireNonNull(messageId);
		Objects.requireNonNull(graveyard);
		Objects.requireNonNull(value);

		// get default replacement map
		Map<String, String> replacements = getDefaultReplacements(recipient);

		// if graveyard display name is not null or empty, set replacement
		if (graveyard.getDisplayName() != null && !graveyard.getDisplayName().isEmpty()) {
			replacements.put("%GRAVEYARD_NAME%", graveyard.getDisplayName());
		}

		// if graveyard location is not null, set replacements
		if (graveyard.getLocation() != null) {
			replacements.put("%WORLD_NAME%", getWorldName(graveyard.getLocation()));
			replacements.put("%LOC_X%", String.valueOf(graveyard.getLocation().getBlockX()));
			replacements.put("%LOC_Y%", String.valueOf(graveyard.getLocation().getBlockY()));
			replacements.put("%LOC_Z%", String.valueOf(graveyard.getLocation().getBlockZ()));
		}

		replacements.put("%VALUE%", value);

		// send message
		sendMessage(recipient, messageId, replacements);
	}


	/**
	 * Send message to player
	 *
	 * @param recipient    Player receiving message
	 * @param messageId    message identifier
	 * @param graveyard    graveyard object
	 * @param targetPlayer targeted player
	 * @throws NullPointerException if any parameter is null
	 */
	public void sendMessage(final CommandSender recipient,
							final MessageId messageId,
							final Graveyard graveyard,
							final OfflinePlayer targetPlayer) {

		// check for null arguments
		Objects.requireNonNull(recipient);
		Objects.requireNonNull(messageId);
		Objects.requireNonNull(graveyard);
		Objects.requireNonNull(targetPlayer);

		// get default replacement map
		Map<String, String> replacements = getDefaultReplacements(recipient);

		// if graveyard display name is not null or empty, set replacement
		if (graveyard.getDisplayName() != null && !graveyard.getDisplayName().isEmpty()) {
			replacements.put("%GRAVEYARD_NAME%", graveyard.getDisplayName());
		}

		// if graveyard location is not null, set replacements
		if (graveyard.getLocation() != null) {
			replacements.put("%WORLD_NAME%", getWorldName(graveyard.getLocation()));
			replacements.put("%LOC_X%", String.valueOf(graveyard.getLocation().getBlockX()));
			replacements.put("%LOC_Y%", String.valueOf(graveyard.getLocation().getBlockY()));
			replacements.put("%LOC_Z%", String.valueOf(graveyard.getLocation().getBlockZ()));
		}

		// get target player name
		if (targetPlayer.getName() != null) {
			replacements.put("%TARGET_PLAYER%", targetPlayer.getName());
		}

		// send message
		sendMessage(recipient, messageId, replacements);
	}


	/**
	 * Send message to player
	 *
	 * @param recipient Player receiving message
	 * @param messageId message identifier
	 * @param duration  duration of safety
	 * @throws NullPointerException if any parameter is null
	 */
	public void sendMessage(final CommandSender recipient,
							final MessageId messageId,
							final Integer duration) {

		// check for null parameters
		Objects.requireNonNull(recipient);
		Objects.requireNonNull(messageId);
		Objects.requireNonNull(duration);

		// get default replacement map
		Map<String, String> replacements = getDefaultReplacements(recipient);

		// set replacement strings
		replacements.put("%DURATION%", duration.toString());

		// send message
		sendMessage(recipient, messageId, replacements);
	}


	/**
	 * Display list header/footer
	 *
	 * @param recipient Player receiving message
	 * @param messageId message identifier
	 * @param page      page number to display for multi-page messages
	 * @param pageCount total number of pages for multi-page messages
	 * @throws NullPointerException if any parameter is null
	 */
	public void listAnnotation(final CommandSender recipient,
							   final MessageId messageId,
							   final Integer page,
							   final Integer pageCount) {

		// check for null parameters
		Objects.requireNonNull(recipient);
		Objects.requireNonNull(messageId);
		Objects.requireNonNull(page);
		Objects.requireNonNull(pageCount);

		// get default replacement map
		Map<String, String> replacements = getDefaultReplacements(recipient);

		replacements.put("%PAGE%", page.toString());
		replacements.put("%PAGE_COUNT%", pageCount.toString());

		// send message
		sendMessage(recipient, messageId, replacements);
	}


	/**
	 * display one list item
	 *
	 * @param recipient  Player receiving message
	 * @param messageId  message identifier
	 * @param graveyard  the graveyard whose details are being listed
	 * @param itemNumber item number in list
	 * @throws NullPointerException if any parameter is null
	 */
	public void listItem(final CommandSender recipient,
						 final MessageId messageId,
						 final Graveyard graveyard,
						 final int itemNumber) {

		// check for null parameters
		Objects.requireNonNull(recipient);
		Objects.requireNonNull(messageId);
		Objects.requireNonNull(graveyard);

		// get default replacement map
		Map<String, String> replacements = getDefaultReplacements(recipient);

		// set replacement strings
		replacements.put("%ITEM_NUMBER%", String.valueOf(itemNumber));

		// if graveyard display name is not null or empty, set replacement
		if (graveyard.getDisplayName() != null && !graveyard.getDisplayName().isEmpty()) {
			replacements.put("%GRAVEYARD_NAME%", graveyard.getDisplayName());
		}

		// if graveyard location is not null, set replacements
		if (graveyard.getLocation() != null) {
			replacements.put("%WORLD_NAME%", getWorldName(graveyard.getLocation()));
			replacements.put("%LOC_X%", String.valueOf(graveyard.getLocation().getBlockX()));
			replacements.put("%LOC_Y%", String.valueOf(graveyard.getLocation().getBlockY()));
			replacements.put("%LOC_Z%", String.valueOf(graveyard.getLocation().getBlockZ()));
		}

		// send message
		sendMessage(recipient, messageId, replacements);
	}

}
