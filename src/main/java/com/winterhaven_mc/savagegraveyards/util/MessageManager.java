package com.winterhaven_mc.savagegraveyards.util;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.util.LanguageManager;
import com.winterhaven_mc.util.SoundManager;
import com.winterhaven_mc.util.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Implements message manager for {@code SavageGraveyards}.
 * 
 * @author      Tim Savage
 * @version		1.0
 *  
 */
public class MessageManager {

	// reference to main class
	private final PluginMain plugin;

	// message cooldown hashmap
	private ConcurrentHashMap<UUID, EnumMap<MessageId, Long>> messageCooldownMap;

	// language manager
	private LanguageManager languageManager;

	// sound manager
	private SoundManager soundManager;

	// custom configuration for messages
	private YamlConfiguration messages;


	/**
	 * Constructor method for class
	 * 
	 * @param plugin reference to main class
	 */
	public MessageManager(final PluginMain plugin) {

		// create pointer to main class
		this.plugin = plugin;

		// initialize messageCooldownMap
		this.messageCooldownMap = new ConcurrentHashMap<>();

		// instantiate language manager
		this.languageManager = new LanguageManager(plugin);

		// instantiate sound manager
		this.soundManager = new SoundManager(plugin);

		// load messages from file
		this.messages = languageManager.loadMessages();
	}


	/**
	 *  Send message to player
	 * 
	 * @param sender			player receiving message
	 * @param messageId			message identifier in messages file
	 */
	public void sendPlayerMessage(final CommandSender sender, final MessageId messageId) {
		this.sendPlayerMessage(sender, messageId, "", "", "", null, null);
	}


	/** Send message to player
	 * 
	 * @param sender		Player receiving message
	 * @param messageId		message identifier in messages file
	 * @param displayName	name of destination
	 */
	public void sendPlayerMessage(final CommandSender sender,
								  final MessageId messageId,
								  final String displayName) {
		this.sendPlayerMessage(sender, messageId, displayName, "", "", null, null);
	}


	/** Send message to player
	 * 
	 * @param sender		Player receiving message
	 * @param messageId		message identifier in messages file
	 * @param displayName	name of destination
	 * @param value			a value to display in the message
	 */
	public void sendPlayerMessage(final CommandSender sender,
								  final MessageId messageId,
								  final String displayName,
								  final String value) {
		this.sendPlayerMessage(sender, messageId, displayName, "", value, null, null);
	}


	/** Send message to player
	 * 
	 * @param sender		Player receiving message
	 * @param messageId		message identifier in messages file
	 * @param displayName	name of destination
	 * @param attribute		an attribute to display in the message
	 * @param value			a value to display in the message
	 */
	@SuppressWarnings("unused")
	void sendPlayerMessage(final CommandSender sender,
			final MessageId messageId,
			final String displayName,
			final String attribute,
			final String value) {
		this.sendPlayerMessage(sender, messageId, displayName, attribute, value, null, null);
	}

	/** Send message to player
	 * 
	 * @param sender		Player receiving message
	 * @param messageId		message identifier in messages file
	 * @param page 			page number to display for multi-page messages
	 * @param pageCount 	total number of pages for multi-page messages
	 */
	public void sendPlayerMessage(final CommandSender sender, final MessageId messageId,
								  final Integer page, final Integer pageCount) {
		sendPlayerMessage(sender,messageId,"","","",page,pageCount);
	}

	/** Send message to player
	 * 
	 * @param sender		Player receiving message
	 * @param messageId		message identifier in messages file
	 * @param displayName	name of destination
	 * @param attribute		an attribute to display in the message
	 * @param value			a value to display in the message
	 */	
	private void sendPlayerMessage(final CommandSender sender,
			final MessageId messageId,
			String displayName,
			String attribute,
			String value,
			Integer page,
			Integer pageCount) {

		// if message is not enabled in messages file, do nothing and return
		if (!messages.getBoolean("messages." + messageId.toString() + ".enabled")) {
			return;
		}

		// set substitution variable defaults			
		String playerName = "console";
		String playerNickname = "console";
		String worldName = "world";
		//noinspection WrapperTypeMayBePrimitive
		Integer duration = 0;

		// set page and pageTotal defaults
		if (page == null || page == 0) {
			page = 1;
		}
		if (pageCount == null || pageCount == 0) {
			pageCount = 1;
		}

		// if sender is a player...
		if (sender instanceof Player) {

			Player player = (Player) sender;

			// get message cooldown time remaining
			long lastDisplayed = getMessageCooldown(player,messageId);

			// get message repeat delay
			//noinspection WrapperTypeMayBePrimitive
			int messageRepeatDelay = messages.getInt("messages." + messageId.toString() + ".repeat-delay");

			// if message has repeat delay value and was displayed to player more recently, do nothing and return
			if (lastDisplayed > System.currentTimeMillis() - messageRepeatDelay * 1000) {
				return;
			}

			// if repeat delay value is greater than zero, add entry to messageCooldownMap
			if (messageRepeatDelay > 0) {
				putMessageCooldown(player,messageId);
			}

			// assign player dependent variables
			playerName = player.getName();
			playerNickname = player.getPlayerListName();
			worldName = player.getWorld().getName();
			duration = plugin.safetyManager.getDuration(player);

			// if value is -1, set to 'default'
			if (value.equals("-1")) {
				value = "default";
			}
		}

		// get message from file
		String message = messages.getString("messages." + messageId.toString() + ".string");

		// if Multiverse is installed, use Multiverse world alias for world name
		worldName = plugin.worldManager.getWorldName(worldName);

		// replace underscores with spaces in display name
		displayName = displayName.replace('_', ' ');

		// do variable substitutions
		if (message.contains("%")) {
			message = StringUtil.replace(message,"%playername%", playerName);
			message = StringUtil.replace(message,"%playernickname%", playerNickname);
			message = StringUtil.replace(message,"%worldname%", worldName);
			message = StringUtil.replace(message,"%displayname%", displayName);
			message = StringUtil.replace(message,"%attribute%", attribute);
			message = StringUtil.replace(message,"%value%", value);
			message = StringUtil.replace(message,"%duration%", duration.toString());
			message = StringUtil.replace(message,"%page%", page.toString());
			message = StringUtil.replace(message,"%pagecount%", pageCount.toString());

			// do variable substitutions, stripping color codes from all caps variables
			message = StringUtil.replace(message,"%PLAYERNAME%", 
					ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',playerName)));
			message = StringUtil.replace(message,"%PLAYERNICKNAME%", 
					ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',playerNickname)));
			message = StringUtil.replace(message,"%WORLDNAME%", 
					ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',worldName)));
			message = StringUtil.replace(message,"%DISPLAYNAME%", 
					ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',displayName)));
			message = StringUtil.replace(message,"%ATTRIBUTE%",
					ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',attribute)));
			message = StringUtil.replace(message,"%VALUE%",
					ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',value)));
			message = StringUtil.replace(message,"%DURATION%",
					ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',duration.toString())));

			// do capitalized substitutions, no stripping of color codes necessary
			message = StringUtil.replace(message,"%PAGE%", page.toString());
			message = StringUtil.replace(message,"%PAGECOUNT%", pageCount.toString());
		}

		// send message to player
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
	}


	/**
	 * Add entry to message cooldown map
	 * @param player the player to add to the message cooldown map
	 * @param messageId the message identifier to add to the message cooldown map for player
	 */
	private void putMessageCooldown(final Player player, final MessageId messageId) {

		EnumMap<MessageId,Long> tempMap = new EnumMap<>(MessageId.class);

		tempMap.put(messageId, System.currentTimeMillis());
		messageCooldownMap.put(player.getUniqueId(),tempMap);
	}


	/**
	 * get entry from message cooldown map
	 * @param player the player for whom to retrieve a message cooldown expire time
	 * @param messageId the message identifier for which to retrieve a message cooldown expire time for player
	 * @return cooldown expire time
	 */
	private long getMessageCooldown(final Player player, final MessageId messageId) {

		// check if player is in message cooldown hashmap
		if (messageCooldownMap.containsKey(player.getUniqueId())) {

			// check if messageID is in player's cooldown hashmap
			if (messageCooldownMap.get(player.getUniqueId()).containsKey(messageId)) {

				// return cooldown time
				return messageCooldownMap.get(player.getUniqueId()).get(messageId);
			}
		}
		return 0L;
	}


	/**
	 * Remove player from message cooldown map
	 * @param player the player to remove from the message cooldown map
	 */
	@SuppressWarnings("unused")
	public void removePlayerCooldown(final Player player) {
		messageCooldownMap.remove(player.getUniqueId());
	}


	/**
	 * Play sound
	 * @param sender command sender (player) to play sound
	 * @param soundId unique identifier that refers to sound in sounds.yml
	 */
	public final void sendPlayerSound(final CommandSender sender, final SoundId soundId) {
		this.soundManager.playerSound(sender,soundId.toString());
	}


	/**
	 * Reload messages and sounds config files
	 */
	public void reload() {

		// reload messages
		this.messages = languageManager.loadMessages();

		// reload sounds
		this.soundManager.reload();
	}

}
