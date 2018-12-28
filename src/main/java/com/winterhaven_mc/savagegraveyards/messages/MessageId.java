package com.winterhaven_mc.savagegraveyards.messages;

public enum MessageId {

    COMMAND_FAIL_ARGS_COUNT_UNDER,
    COMMAND_FAIL_ARGS_COUNT_OVER,
    COMMAND_FAIL_CONSOLE,
    COMMAND_FAIL_CREATE_EXISTS,
    COMMAND_FAIL_CLOSEST_NO_MATCH,
    COMMAND_FAIL_INVALID_ATTRIBUTE,
    COMMAND_FAIL_SET_INVALID_BOOLEAN,
    COMMAND_FAIL_INVALID_COMMAND,
    COMMAND_FAIL_SET_INVALID_INTEGER,
    COMMAND_FAIL_NO_RECORD,
    COMMAND_FAIL_TELEPORT,

    COMMAND_SUCCESS_CLOSEST,
    COMMAND_SUCCESS_CREATE,
    COMMAND_SUCCESS_DELETE,
    COMMAND_SUCCESS_RELOAD,
    COMMAND_SUCCESS_TELEPORT,
    COMMAND_SUCCESS_SET_DISCOVERYMESSAGE,
    COMMAND_SUCCESS_SET_DISCOVERYRANGE,
    COMMAND_SUCCESS_SET_NAME,
    COMMAND_SUCCESS_SET_ENABLED,
    COMMAND_SUCCESS_SET_GROUP,
    COMMAND_SUCCESS_SET_HIDDEN,
    COMMAND_SUCCESS_SET_LOCATION,
    COMMAND_SUCCESS_SET_RESPAWNMESSAGE,
    COMMAND_SUCCESS_SET_SAFETYTIME,

    DEFAULT_DISCOVERY,
    DEFAULT_RESPAWN,

    SAFETY_COOLDOWN_START,
    SAFETY_COOLDOWN_END,

    PERMISSION_DENIED_CLOSEST,
    PERMISSION_DENIED_CREATE,
    PERMISSION_DENIED_DELETE,
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
    LIST_EMPTY

}
