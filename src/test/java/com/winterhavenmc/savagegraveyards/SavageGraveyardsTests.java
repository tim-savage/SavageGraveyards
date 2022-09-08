package com.winterhavenmc.savagegraveyards;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.winterhavenmc.savagegraveyards.sounds.SoundId;
import org.bukkit.configuration.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SavageGraveyardsTests {

    private ServerMock server;
    private PluginMain plugin;


    @BeforeAll
    public void setUp() {

        // Start the mock server
        server = MockBukkit.mock();

        // start the mock plugin
        plugin = MockBukkit.load(PluginMain.class);

        // create mock world
//        WorldMock world = MockBukkit.getMock().addSimpleWorld("world");

        // create mock player
//        PlayerMock player = server.addPlayer("testy");
    }

    @AfterAll
    public void tearDown() {

        // cancel all tasks
        server.getScheduler().cancelTasks(plugin);

        // Stop the mock server
        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("Test mocking setup.")
    class MockingTests {

        @Test
        @DisplayName("server is not null.")
        void serverNotNull() {
            Assertions.assertNotNull(server, "server is null.");
        }

        @Test
        @DisplayName("plugin is not null.")
        void pluginNotNull() {
            Assertions.assertNotNull(plugin, "plugin is null.");
        }

        @Test
        @DisplayName("plugin is enabled.")
        void pluginEnabled() {
            Assertions.assertTrue(plugin.isEnabled(),"plugin is not enabled.");
        }
    }


    @Nested
    @DisplayName("Test plugin main objects.")
    class PluginTests {
        @Test
        @DisplayName("message builder not null.")
        void messageBuilderNotNull() {
            Assertions.assertNotNull(plugin.messageBuilder);
        }

        @Test
        @DisplayName("world manager not null.")
        void worldManagerNotNull() {
            Assertions.assertNotNull(plugin.worldManager);
        }

        @Test
        @DisplayName("sound config not null.")
        void soundConfigNotNull() {
            Assertions.assertNotNull(plugin.soundConfig);
        }
    }


    @Nested
    @DisplayName("Test plugin config.")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ConfigTests {

        final Configuration config = plugin.getConfig();
        final Set<String> enumConfigKeyStrings = new HashSet<>();

        public ConfigTests() {
            for (ConfigSetting configSetting : ConfigSetting.values()) {
                this.enumConfigKeyStrings.add(configSetting.getKey());
            }
        }

        @Test
        @DisplayName("config not null.")
        void configNotNull() {
            Assertions.assertNotNull(config);
        }

        @Test
        @DisplayName("test configured language.")
        void getLanguage() {
            Assertions.assertEquals("en-US", config.getString("language"),
                    "configured language does not match en-US");
        }

        @SuppressWarnings("unused")
        Set<String> configFileKeys() {
            return plugin.getConfig().getKeys(false);
        }

        @ParameterizedTest
        @DisplayName("file config key is contained in ConfigSetting enum.")
        @MethodSource("configFileKeys")
        void ConfigFileKeyNotNull(String key) {
            Assertions.assertNotNull(key);
            Assertions.assertTrue(enumConfigKeyStrings.contains(key),
                    "file config key is not contained in ConfigSetting enum.");
        }

        @ParameterizedTest
        @EnumSource(ConfigSetting.class)
        @DisplayName("ConfigSetting enum matches config file key/value pairs.")
        void configFileKeysContainsEnumKey(ConfigSetting configSetting) {
            Assertions.assertEquals(configSetting.getValue(), plugin.getConfig().getString(configSetting.getKey()));
        }
    }


    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Test Sounds config.")
    class SoundTests {

        // collection of enum sound name strings
        final Collection<String> enumSoundNames = new HashSet<>();

        // class constructor
        SoundTests() {
            // add all SoundId enum values to collection
            for (com.winterhavenmc.savagegraveyards.sounds.SoundId SoundId : SoundId.values()) {
                enumSoundNames.add(SoundId.name());
            }
        }

        @Test
        @DisplayName("Sounds config is not null.")
        void soundConfigNotNull() {
            Assertions.assertNotNull(plugin.soundConfig);
        }

        final Collection<String> GetConfigFileKeys() {
            return plugin.soundConfig.getSoundConfigKeys();
        }

        @ParameterizedTest
        @EnumSource(SoundId.class)
        @DisplayName("enum member soundId is contained in getConfig() keys.")
        void fileKeysContainsEnumValue(SoundId soundId) {
            Assertions.assertTrue(plugin.soundConfig.isValidSoundConfigKey(soundId.name()),
                    "Enum value '" + soundId.name() + "' does not have matching key in sounds.yml.");
        }

        @ParameterizedTest
        @MethodSource("GetConfigFileKeys")
        @DisplayName("config file key has matching key in enum sound names")
        void soundConfigEnumContainsAllFileSounds(String key) {
            Assertions.assertTrue(enumSoundNames.contains(key),
                    "File key does not have matching key in enum sound names.");
        }

        @ParameterizedTest
        @MethodSource("GetConfigFileKeys")
        @DisplayName("sound file key has valid bukkit sound name")
        void soundConfigFileHasValidBukkitSound(String key) {
            String bukkitSoundName = plugin.soundConfig.getBukkitSoundName(key);
            Assertions.assertTrue(plugin.soundConfig.isValidBukkitSoundName(bukkitSoundName),
                    "File key '" + key + "' has invalid bukkit sound name: " + bukkitSoundName);
        }
    }

}
