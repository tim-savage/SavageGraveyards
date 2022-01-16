package com.winterhavenmc.savagegraveyards.commands;

import com.winterhavenmc.savagegraveyards.PluginMain;


public enum SubcommandType {

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
