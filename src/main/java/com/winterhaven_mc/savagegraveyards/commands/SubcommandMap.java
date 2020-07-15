package com.winterhaven_mc.savagegraveyards.commands;

import java.util.*;


public class SubcommandMap {

	SortedMap<String, Subcommand> subcommandMap = new TreeMap<>();
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
