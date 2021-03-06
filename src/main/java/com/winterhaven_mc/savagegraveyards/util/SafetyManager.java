package com.winterhaven_mc.savagegraveyards.util;

import com.winterhaven_mc.savagegraveyards.PluginMain;
import com.winterhaven_mc.savagegraveyards.messages.Macro;
import com.winterhaven_mc.savagegraveyards.messages.Message;
import com.winterhaven_mc.savagegraveyards.messages.MessageId;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * Cancel mob targeting of players for configured period after respawn
 */
public class SafetyManager {

	// reference to main class
	private final PluginMain plugin;

	// safety cooldown map
	private final Map<UUID, BukkitTask> safetyCooldownMap;


	/**
	 * Class constructor
	 *
	 * @param plugin reference to main class
	 */
	public SafetyManager(final PluginMain plugin) {

		// set reference to main class
		this.plugin = plugin;

		// instantiate safety cooldown map
		safetyCooldownMap = new ConcurrentHashMap<>();
	}


	/**
	 * Insert player uuid into safety cooldown map
	 *
	 * @param player   the player whose uuid will be used as key in the safety cooldown map
	 * @param duration in seconds
	 */
	public void putPlayer(final Player player, final long duration) {

		// get safety time from passed duration
		long safetyTime = duration;

		// if safetyTime is negative, use configured default
		if (safetyTime < 0L) {
			safetyTime = plugin.getConfig().getLong("safety-time");
		}

		// if safetyTime is zero, do nothing and return
		if (safetyTime == 0L) {
			return;
		}

		// send safety message to player
		Message.create(player, MessageId.SAFETY_COOLDOWN_START)
				.setMacro(Macro.DURATION, TimeUnit.SECONDS.toMillis(safetyTime))
				.send();

		// create task to remove player from map after safetyTime duration
		BukkitTask task = new BukkitRunnable() {
			@Override
			public void run() {
				removePlayer(player);
				Message.create(player, MessageId.SAFETY_COOLDOWN_END).send();
			}
		}.runTaskLater(plugin, safetyTime * 20L);

		// if player is already in cooldown map, cancel existing task
		if (isPlayerProtected(player)) {
			safetyCooldownMap.get(player.getUniqueId()).cancel();
		}

		// add player to safety cooldown map
		safetyCooldownMap.put(player.getUniqueId(), task);
	}


	/**
	 * Remove player from safety cooldown map
	 *
	 * @param player the player to be removed from the safety cooldown map
	 */
	private void removePlayer(final Player player) {
		safetyCooldownMap.remove(player.getUniqueId());
	}


	/**
	 * Check if player is in safety cooldown map
	 *
	 * @param player the player to test if in the safety cooldown map
	 * @return {@code true} if the player is in the safety cooldown map, {@code false} if they are not
	 */
	public boolean isPlayerProtected(final Player player) {

		return safetyCooldownMap.containsKey(player.getUniqueId());
	}

}
