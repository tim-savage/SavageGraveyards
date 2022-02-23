/*
 * Copyright (c) 2022 Tim Savage.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.winterhavenmc.savagegraveyards.tasks;

import com.winterhavenmc.savagegraveyards.PluginMain;
import com.winterhavenmc.savagegraveyards.messages.MessageId;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SafetyTask extends BukkitRunnable {

	private final PluginMain plugin;
	private final Player player;

	public SafetyTask(final PluginMain plugin, final Player player) {
		this.plugin = plugin;
		this.player = player;
	}


	public void run() {

		plugin.safetyManager.removePlayer(player);
		if (plugin.getConfig().getBoolean("titles-enabled")) {
			if (plugin.messageBuilder.isEnabled(MessageId.SAFETY_COOLDOWN_END_TITLE)) {
				String safetyMessage = plugin.messageBuilder.compose(player, MessageId.SAFETY_COOLDOWN_END_TITLE).draft();
				player.sendTitle(" ", safetyMessage, 10, 70, 20);
			}
		}
		plugin.messageBuilder.compose(player, MessageId.SAFETY_COOLDOWN_END).send();
	}

}
