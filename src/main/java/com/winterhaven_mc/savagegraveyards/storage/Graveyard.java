package com.winterhaven_mc.savagegraveyards.storage;

import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Graveyard object
 */
@SuppressWarnings("WeakerAccess")
public class Graveyard {

	private Integer key;
	private String searchKey;
	private String displayName;
	private Boolean enabled;
	private Boolean hidden;
	private Integer discoveryRange;
	private String discoveryMessage;
	private String respawnMessage;
	private String group;
	private Integer safetyRange;
	private Integer safetyTime;
	private Location location;


	/**
	 * Class constructor
	 */
	public Graveyard() {

		// set some default values
		this.setDiscoveryRange(-1);
		this.setDiscoveryMessage("");
		this.setRespawnMessage("");
		this.setGroupName("");
		this.setSafetyRange(-1);
		this.setSafetyTime(-1);
	}


	/**
	 * Class constructor
	 */
	public Graveyard(String displayName) {

		// set some default values
		this.displayName = displayName;
	}


	public static String deriveKey(String displayName) {
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);
		return ChatColor.stripColor(displayName.replace(' ', '_'));
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(final Integer key) {
		this.key = key;
	}

	public String getSearchKey() {
		return this.searchKey;
	}
	
	public void setSearchKey(final String displayName) {
		this.searchKey = Graveyard.deriveKey(displayName);
	}
	
	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}
	
	public Boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(final Boolean enabled) {
		this.enabled = enabled;
	}
	
	public Boolean isHidden() {
		return this.hidden;
	}
	
	public void setHidden(final Boolean hidden) {
		this.hidden = hidden;
	}
	
	public Integer getDiscoveryRange() {
		return this.discoveryRange;
	}

	public void setDiscoveryRange(final Integer discoveryRange) {
		if (discoveryRange == null) {
			this.discoveryRange = -1;
		}
		else {
			this.discoveryRange = discoveryRange;
		}
	}

	public String getGroup() {
		return this.group;
	}

	public void setGroupName(final String group) {
		this.group = group;
	}

	public String getDiscoveryMessage() {
		return this.discoveryMessage;
	}

	public void setDiscoveryMessage(final String discoveryMessage) {
		this.discoveryMessage = discoveryMessage;
	}

	public String getRespawnMessage() {
		return this.respawnMessage;
	}

	public void setRespawnMessage(final String respawnMessage) {
		this.respawnMessage = respawnMessage;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(final Location location) {
		this.location = location;
	}
	
	public Integer getSafetyRange() {
		return this.safetyRange;
	}

	public void setSafetyRange(final Integer safetyRange) {		
		if (safetyRange == null) {
			this.safetyRange = -1;
		}
		else {
			this.safetyRange = safetyRange;
		}
	}

	public Integer getSafetyTime() {
		return this.safetyTime;
	}

	public void setSafetyTime(final Integer safetyTime) {		
		if (safetyTime == null) {
			this.safetyTime = -1;
		}
		else {
			this.safetyTime = safetyTime;
		}
	}

}
