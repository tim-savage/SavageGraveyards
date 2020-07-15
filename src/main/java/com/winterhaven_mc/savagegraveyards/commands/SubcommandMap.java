package com.winterhaven_mc.savagegraveyards.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SubcommandMap {

	Map<String, Subcommand> subcommandMap = new HashMap<>();
	Map<String, String> aliasMap = new HashMap<>();


	void put(final String name, final Subcommand command) {

		command.setName(name);

		subcommandMap.put(name.toLowerCase(), command);

		command.getAliases();

		for (String alias : command.getAliases()) {
			aliasMap.put(alias.toLowerCase(), name.toLowerCase());
		}
	}


	Subcommand get(final String name) {

		String key = name;

		if (aliasMap.containsKey(key)) {
			key = aliasMap.get(key);
		}

		return (subcommandMap.get(key));
	}

	List<String> getKeys() {
		return new ArrayList<>(subcommandMap.keySet());
	}
}
