package com.winterhavenmc.savagegraveyards;


public enum ConfigSetting {

    LANGUAGE("en-US"),
    ENABLED_WORLDS("[]"),
    DISABLED_WORLDS("[]"),
    DEFAULT_ENABLED("true"),
    DEFAULT_HIDDEN("true"),
    SAFETY_TIME("15"),
    DISCOVERY_RANGE("50"),
    DISCOVERY_INTERVAL("40"),
    LIST_PAGE_SIZE("5"),
    RESPAWN_PRIORITY("NORMAL"),
    SOUND_EFFECTS("true"),
    ;

    private final String value;

    ConfigSetting(String value) {
        this.value = value;
    }

    public String getKey() {
        return this.name().toLowerCase().replace('_', '-');
    }
    public String getValue() {
        return this.value;
    }

}
