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

package com.winterhavenmc.savagegraveyards.messages;

/**
 * Enum with entries for all player messages in language configuration files
 */
public enum MessageId {

	COMMAND_FAIL_ARGS_COUNT_UNDER,
	COMMAND_FAIL_ARGS_COUNT_OVER,
	COMMAND_FAIL_CONSOLE,
	COMMAND_FAIL_CREATE_EXISTS,
	COMMAND_FAIL_CLOSEST_NO_MATCH,
	COMMAND_FAIL_FORGET,
	COMMAND_FAIL_FORGET_INVALID_GRAVEYARD,
	COMMAND_FAIL_FORGET_INVALID_PLAYER,
	COMMAND_FAIL_INVALID_ATTRIBUTE,
	COMMAND_FAIL_INVALID_COMMAND,
	COMMAND_FAIL_SET_INVALID_BOOLEAN,
	COMMAND_FAIL_SET_INVALID_INTEGER,
	COMMAND_FAIL_SET_INVALID_NAME,
	COMMAND_FAIL_NO_RECORD,
	COMMAND_FAIL_TELEPORT,
	COMMAND_FAIL_TELEPORT_WORLD_INVALID,

	COMMAND_SUCCESS_CLOSEST,
	COMMAND_SUCCESS_CREATE,
	COMMAND_SUCCESS_DELETE,
	COMMAND_SUCCESS_FORGET,
	COMMAND_SUCCESS_RELOAD,
	COMMAND_SUCCESS_TELEPORT,

	COMMAND_SUCCESS_SET_NAME,
	COMMAND_SUCCESS_SET_LOCATION,
	COMMAND_SUCCESS_SET_ENABLED,
	COMMAND_SUCCESS_SET_HIDDEN,
	COMMAND_SUCCESS_SET_GROUP,
	COMMAND_SUCCESS_SET_DISCOVERYMESSAGE,
	COMMAND_SUCCESS_SET_DISCOVERYMESSAGE_DEFAULT,
	COMMAND_SUCCESS_SET_DISCOVERYRANGE,
	COMMAND_SUCCESS_SET_DISCOVERYRANGE_DEFAULT,
	COMMAND_SUCCESS_SET_RESPAWNMESSAGE,
	COMMAND_SUCCESS_SET_RESPAWNMESSAGE_DEFAULT,
	COMMAND_SUCCESS_SET_SAFETYTIME,
	COMMAND_SUCCESS_SET_SAFETYTIME_DEFAULT,

	COMMAND_HELP_CLOSEST,
	COMMAND_HELP_CREATE,
	COMMAND_HELP_DELETE,
	COMMAND_HELP_FORGET,
	COMMAND_HELP_HELP,
	COMMAND_HELP_LIST,
	COMMAND_HELP_RELOAD,
	COMMAND_HELP_SET,
	COMMAND_HELP_SHOW,
	COMMAND_HELP_STATUS,
	COMMAND_HELP_TELEPORT,
	COMMAND_HELP_INVALID,
	COMMAND_HELP_USAGE_HEADER,

	DEFAULT_DISCOVERY,
	DEFAULT_RESPAWN,

	SAFETY_COOLDOWN_START,
	SAFETY_COOLDOWN_END,

	PERMISSION_DENIED_CLOSEST,
	PERMISSION_DENIED_CREATE,
	PERMISSION_DENIED_DELETE,
	PERMISSION_DENIED_FORGET,
	PERMISSION_DENIED_HELP,
	PERMISSION_DENIED_LIST,
	PERMISSION_DENIED_RELOAD,
	PERMISSION_DENIED_SHOW,
	PERMISSION_DENIED_SET_LOCATION,
	PERMISSION_DENIED_SET_NAME,
	PERMISSION_DENIED_SET_ENABLED,
	PERMISSION_DENIED_SET_GROUP,
	PERMISSION_DENIED_SET_HIDDEN,
	PERMISSION_DENIED_SET_DISCOVERYRANGE,
	PERMISSION_DENIED_SET_DISCOVERYMESSAGE,
	PERMISSION_DENIED_SET_RESPAWNMESSAGE,
	PERMISSION_DENIED_SET_SAFETYTIME,
	PERMISSION_DENIED_STATUS,
	PERMISSION_DENIED_TELEPORT,

	LIST_HEADER,
	LIST_FOOTER,
	LIST_ITEM,
	LIST_ITEM_DISABLED,
	LIST_ITEM_UNDISCOVERED,
	LIST_ITEM_INVALID_WORLD,
	LIST_EMPTY

}
