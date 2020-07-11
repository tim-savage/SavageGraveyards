package com.winterhaven_mc.savagegraveyards.storage;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;


/**
 * Graveyard object
 */
public final class Graveyard {

	// constant value for integer attributes to use configured default
	private final static int CONFIG_DEFAULT = -1;

	private final int primaryKey;
	private final String searchKey;
	private final String displayName;
	private final boolean enabled;
	private final boolean hidden;
	private final int discoveryRange;
	private final String discoveryMessage;
	private final String respawnMessage;
	private final String group;
	private final int safetyRange;
	private final long safetyTime;
	private final String worldName;
	private final UUID worldUid;
	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;


	/**
	 * Private class constructor to prevent instantiation
	 */
	@SuppressWarnings("unused")
	private Graveyard() {
		throw new AssertionError();
	}


	/**
	 * Private class constructor for use with static builder
	 *
	 * @param builder builder object
	 */
	private Graveyard(final Builder builder) {
		primaryKey = builder.primaryKey;
		displayName = builder.displayName;
		searchKey = builder.searchKey;
		enabled = builder.enabled;
		hidden = builder.hidden;
		discoveryRange = builder.discoveryRange;
		discoveryMessage = builder.discoveryMessage;
		respawnMessage = builder.respawnMessage;
		group = builder.group;
		safetyRange = builder.safetyRange;
		safetyTime = builder.safetyTime;
		worldName = builder.worldName;
		worldUid = builder.worldUid;
		x = builder.x;
		y = builder.y;
		z = builder.z;
		yaw = builder.yaw;
		pitch = builder.pitch;
	}


	/**
	 * Builder class
	 */
	public final static class Builder {

		private final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(this.getClass());

		private int primaryKey;
		private String displayName;
		private String searchKey;
		private boolean enabled = plugin.getConfig().getBoolean("default-enabled");
		private boolean hidden = plugin.getConfig().getBoolean("default-hidden");
		private int discoveryRange = CONFIG_DEFAULT;
		private String discoveryMessage = "";
		private String respawnMessage = "";
		private String group = "";
		private int safetyRange = CONFIG_DEFAULT;
		private long safetyTime = CONFIG_DEFAULT;
		private String worldName;
		private UUID worldUid;
		private double x;
		private double y;
		private double z;
		private float yaw;
		private float pitch;

		/**
		 * Builder class constructor
		 */
		public Builder() { }


		/**
		 * Builder class constructor
		 *
		 * @param graveyard existing graveyard object from which all values are copied
		 */
		public Builder(Graveyard graveyard) {
			this.primaryKey = graveyard.getPrimaryKey();
			this.displayName = graveyard.getDisplayName();
			this.searchKey = graveyard.getSearchKey();
			this.enabled = graveyard.isEnabled();
			this.hidden = graveyard.isHidden();
			this.discoveryRange = graveyard.getDiscoveryRange();
			this.respawnMessage = graveyard.getRespawnMessage();
			this.group = graveyard.getGroup();
			this.safetyRange = graveyard.getSafetyRange();
			this.safetyTime = graveyard.getSafetyTime();
			this.worldName = graveyard.getWorldName();
			this.worldUid = graveyard.getWorldUid();
			this.x = graveyard.getX();
			this.y = graveyard.getY();
			this.z = graveyard.getZ();
			this.yaw = graveyard.getYaw();
			this.pitch = graveyard.getPitch();
		}


		/**
		 * set primary key
		 *
		 * @param value int value to assign to builder primary key field
		 * @return this Builder object
		 */
		public final Builder primaryKey(final int value) {
			primaryKey = value;
			return this;
		}


		/**
		 * set display name field
		 *
		 * @param value string value to assign to builder display name field
		 * @return this Builder object
		 */
		public final Builder displayName(final String value) {
			displayName = value;
			searchKey = createSearchKey(value);
			return this;
		}


		/**
		 * set search key field
		 *
		 * @param value string value to assign to builder search key field
		 * @return this Builder object
		 */
		final Builder searchKey(final String value) {
			searchKey = value;
			return this;
		}


		/**
		 * set enabled field
		 *
		 * @param value boolean value to assign to builder enabled field
		 * @return this Builder object
		 */
		public final Builder enabled(final boolean value) {
			enabled = value;
			return this;
		}


		/**
		 * set hidden field
		 *
		 * @param value boolean value to assign to builder hidden field
		 * @return this Builder object
		 */
		public final Builder hidden(final boolean value) {
			hidden = value;
			return this;
		}


		/**
		 * set discovery range field
		 *
		 * @param value int value to assign to builder discovery range field
		 * @return this Builder object
		 */
		public final Builder discoveryRange(final int value) {
			discoveryRange = value;
			return this;
		}


		/**
		 * set discovery message field
		 *
		 * @param value string value to assign to builder discover message field
		 * @return this Builder object
		 */
		public final Builder discoveryMessage(final String value) {
			discoveryMessage = value;
			return this;
		}


		/**
		 * set respawn message field
		 *
		 * @param value string value to assign to builder respawn message field
		 * @return this Builder object
		 */
		public final Builder respawnMessage(final String value) {
			respawnMessage = value;
			return this;
		}


		/**
		 * set group field
		 *
		 * @param value string value to assign to builder group field
		 * @return this Builder object
		 */
		public final Builder group(final String value) {
			group = value;
			return this;
		}


		/**
		 * set safety range field (currently unused)
		 *
		 * @param value int value to assign to builder safety range field
		 * @return this Builder object
		 */
		final Builder safetyRange(final int value) {
			safetyRange = value;
			return this;
		}


		/**
		 * set safety time field
		 *
		 * @param value int value to assign to builder safety time field
		 * @return this Builder object
		 */
		public final Builder safetyTime(final int value) {
			safetyTime = value;
			return this;
		}


		/**
		 * set location field
		 *
		 * @param value location value to assign to builder location fields
		 * @return this Builder object
		 */
		public final Builder location(final Location value) {

			worldUid = Objects.requireNonNull(value.getWorld()).getUID();
			x = value.getX();
			y = value.getY();
			z = value.getZ();
			yaw = value.getYaw();
			pitch = value.getPitch();

			return this;
		}


		/**
		 * Set worldName field
		 * @param value String value to assign to builder worldName field
		 * @return this builder object
		 */
		public final Builder worldName(final String value) {
			worldName = value;
			return this;
		}


		/**
		 * Set worldUid field
		 *
		 * @param value UUID value to assign to builder worldUid field
		 * @return this builder object
		 */
		public final Builder worldUid(final UUID value) {
			worldUid = value;
			return this;
		}


		/**
		 * Set x field
		 * @param value double value to assign to builder x field
		 * @return this builder object
		 */
		public final Builder x(final double value) {
			x = value;
			return this;
		}


		/**
		 * Set y field
		 * @param value double value to assign to builder y field
		 * @return this builder object
		 */
		public final Builder y(final double value) {
			y = value;
			return this;
		}


		/**
		 * Set z field
		 * @param value double value to assign to builder z field
		 * @return this builder object
		 */
		public final Builder z(final double value) {
			z = value;
			return this;
		}


		/**
		 * Set yaw field
		 * @param value float value to assign to builder yaw field
		 * @return this builder object
		 */
		public final Builder yaw(final float value) {
			yaw = value;
			return this;
		}


		/**
		 * Set pitch field
		 * @param value float value to assign to builder pitch field
		 * @return this builder object
		 */
		public final Builder pitch(final float value) {
			pitch = value;
			return this;
		}


		/**
		 * build Graveyard object from builder fields
		 *
		 * @return new Graveyard object
		 */
		public final Graveyard build() {
			return new Graveyard(this);
		}
	}


	@Override
	public String toString() {
		return this.displayName;
	}


	/**
	 * Getter for primary key
	 *
	 * @return int - primary key
	 */
	public final int getPrimaryKey() {
		return this.primaryKey;
	}


	/**
	 * Getter for display name
	 *
	 * @return String - display name
	 */
	public final String getDisplayName() {
		return this.displayName;
	}


	/**
	 * Getter for search key
	 *
	 * @return String - search key
	 */
	public final String getSearchKey() {
		return this.searchKey;
	}


	/**
	 * Getter for enabled
	 *
	 * @return boolean - enabled
	 */
	public final boolean isEnabled() {
		return this.enabled;
	}


	/**
	 * Getter for hidden
	 *
	 * @return boolean - hidden
	 */
	public final boolean isHidden() {
		return this.hidden;
	}


	/**
	 * Getter for discovery range
	 *
	 * @return int - discovery range
	 */
	public final int getDiscoveryRange() {
		return this.discoveryRange;
	}


	/**
	 * Getter for group
	 *
	 * @return String - group
	 */
	public final String getGroup() {
		return this.group;
	}


	/**
	 * Getter for discovery message
	 *
	 * @return String - discovery message
	 */
	public final String getDiscoveryMessage() {
		return this.discoveryMessage;
	}


	/**
	 * Getter for respawn message
	 *
	 * @return String - respawn message
	 */
	public final String getRespawnMessage() {
		return this.respawnMessage;
	}


	/**
	 * Getter for location
	 *
	 * @return Location - location
	 */
	public final Location getLocation() {

		if (worldUid == null) {
			return null;
		}

		// get world by uid
		World world = JavaPlugin.getProvidingPlugin(this.getClass()).getServer().getWorld(worldUid);

		// if world is null, return null
		if (world == null) {
			return null;
		}

		// return new location
		return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
	}


	/**
	 * Getter for worldName
	 * @return String - worldName
	 */
	public final String getWorldName() {
		return this.worldName;
	}


	/**
	 * Getter for worldUid
	 * @return UUID - worldUid
	 */
	public final UUID getWorldUid() {
		return this.worldUid;
	}


	/**
	 * Getter for x
	 * @return double - x
	 */
	public final double getX() {
		return this.x;
	}


	/**
	 * Getter for y
	 * @return double - y
	 */
	public final double getY() {
		return this.y;
	}


	/**
	 * Getter for z
	 * @return double - z
	 */
	public final double getZ() {
		return this.z;
	}


	/**
	 * Getter for yaw
	 * @return float - yaw
	 */
	public final float getYaw() {
		return this.yaw;
	}


	/**
	 * Getter for pitch
	 * @return float - pitch
	 */
	public float getPitch() {
		return this.pitch;
	}


	/**
	 * Getter for safety range (currently unused)
	 *
	 * @return int - safety range
	 */
	final int getSafetyRange() {
		return this.safetyRange;
	}


	/**
	 * Getter for safety time
	 *
	 * @return int - safety time
	 */
	public final long getSafetyTime() {
		return this.safetyTime;
	}


	/**
	 * Static method to create search key from graveyard display name;
	 * strips color codes and replaces spaces with underscores;
	 * preserves case
	 *
	 * @param displayName the graveyard display name
	 * @return String - a search key derived from graveyard display name
	 */
	static String createSearchKey(String displayName) {
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);
		return ChatColor.stripColor(displayName.replace(' ', '_'));
	}

}
