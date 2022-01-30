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

package com.winterhavenmc.savagegraveyards.commands;

import com.winterhavenmc.savagegraveyards.PluginMain;


enum SubcommandType {

	CLOSEST() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new ClosestCommand(plugin);
		}
	},

	CREATE() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new CreateCommand(plugin);
		}
	},

	DELETE() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new DeleteCommand(plugin);
		}
	},

	FORGET() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new ForgetCommand(plugin);
		}
	},

	LIST() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new ListCommand(plugin);
		}
	},

	RELOAD() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new ReloadCommand(plugin);
		}
	},

	SET() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new SetCommand(plugin);
		}
	},

	SHOW() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new ShowCommand(plugin);
		}
	},

	STATUS() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new StatusCommand(plugin);
		}
	},

	TELEPORT() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new TeleportCommand(plugin);
		}
	};

	abstract Subcommand create(final PluginMain plugin);

}
