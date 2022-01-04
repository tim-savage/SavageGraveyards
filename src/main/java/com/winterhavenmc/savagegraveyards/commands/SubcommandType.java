package com.winterhavenmc.savagegraveyards.commands;

import com.winterhavenmc.savagegraveyards.PluginMain;


public enum SubcommandType {

	CLOSEST() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new ClosestCommand(plugin));
		}
	},

	CREATE() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new CreateCommand(plugin));
		}
	},

	DELETE() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new DeleteCommand(plugin));
		}
	},

	FORGET() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new ForgetCommand(plugin));
		}
	},

	HELP() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new HelpCommand(plugin, subcommandMap));
		}
	},

	LIST() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new ListCommand(plugin));
		}
	},

	RELOAD() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new ReloadCommand(plugin));
		}
	},

	SET() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new SetCommand(plugin));
		}
	},

	SHOW() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new ShowCommand(plugin));
		}
	},

	STATUS() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new StatusCommand(plugin));
		}
	},

	TELEPORT() {
		@Override
		void register(final PluginMain plugin, final SubcommandMap subcommandMap) {
			subcommandMap.register(new TeleportCommand(plugin));
		}
	};

	abstract void register(final PluginMain plugin, final SubcommandMap subcommandMap);

}
