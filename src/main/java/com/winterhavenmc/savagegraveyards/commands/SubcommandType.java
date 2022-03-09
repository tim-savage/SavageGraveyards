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
			return new ClosestSubcommand(plugin);
		}
	},

	CREATE() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new CreateSubcommand(plugin);
		}
	},

	DELETE() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new DeleteSubcommand(plugin);
		}
	},

	FORGET() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new ForgetSubcommand(plugin);
		}
	},

	LIST() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new ListSubcommand(plugin);
		}
	},

	RELOAD() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new ReloadSubcommand(plugin);
		}
	},

	SET() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new SetSubcommand(plugin);
		}
	},

	SHOW() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new ShowSubcommand(plugin);
		}
	},

	STATUS() {
		@Override
		Subcommand create(final PluginMain plugin) {
			return new StatusSubcommand(plugin);
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
