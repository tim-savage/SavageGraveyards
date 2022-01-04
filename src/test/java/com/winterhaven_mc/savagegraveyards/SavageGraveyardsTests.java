package com.winterhaven_mc.savagegraveyards;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.winterhaven_mc.savagegraveyards.sounds.SoundId;
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
    @DisplayName("Test mock objects.")
    class MockingTests {

        @Test
        @DisplayName("mock server not null.")
        void MockServerNotNull() {
            Assertions.assertNotNull(server);
        }

        @Test
        @DisplayName("mock plugin not null.")
        void MockPluginNotNull() {
            Assertions.assertNotNull(plugin);
        }
    }


    @Nested
    @DisplayName("Test plugin main objects.")
    class PluginTests {
        @Test
        @DisplayName("language manager not null.")
        void LanguageManagerNotNull() {
            Assertions.assertNotNull(plugin.messageBuilder);
        }

        @Test
        @DisplayName("world manager not null.")
        void WorldManagerNotNull() {
            Assertions.assertNotNull(plugin.worldManager);
        }

        @Test
        @DisplayName("sound config not null.")
        void SoundConfigNotNull() {
            Assertions.assertNotNull(plugin.soundConfig);
        }
    }


    @Nested
    @DisplayName("Test plugin config.")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ConfigTests {

        Configuration config = plugin.getConfig();
        Set<String> enumConfigKeyStrings = new HashSet<>();

        public ConfigTests() {
            for (ConfigSetting configSetting : ConfigSetting.values()) {
                this.enumConfigKeyStrings.add(configSetting.getKey());
            }
        }

        @Test
        @DisplayName("config not null.")
        void ConfigNotNull() {
            Assertions.assertNotNull(config);
        }

        @Test
        @DisplayName("test configured language.")
        void GetLanguage() {
            Assertions.assertEquals("en-US", config.getString("language"),
                    "configured language does not match en-US");
        }

        @SuppressWarnings("unused")
        Set<String> ConfigFileKeys() {
            return plugin.getConfig().getKeys(false);
        }

        @ParameterizedTest
        @DisplayName("file config key is contained in ConfigSetting enum.")
        @MethodSource("ConfigFileKeys")
        void ConfigFileKeyNotNull(String key) {
            Assertions.assertNotNull(key);
            Assertions.assertTrue(enumConfigKeyStrings.contains(key),
                    "file config key is not contained in ConfigSetting enum.");
        }

        @ParameterizedTest
        @EnumSource(ConfigSetting.class)
        @DisplayName("ConfigSetting enum matches config file key/value pairs.")
        void ConfigFileKeysContainsEnumKey(ConfigSetting configSetting) {
            Assertions.assertEquals(configSetting.getValue(), plugin.getConfig().getString(configSetting.getKey()));
        }
    }


    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Test Sounds config.")
    class SoundTests {

        // collection of enum sound name strings
        Collection<String> enumSoundNames = new HashSet<>();

        // class constructor
        SoundTests() {
            // add all SoundId enum values to collection
            for (com.winterhaven_mc.savagegraveyards.sounds.SoundId SoundId : SoundId.values()) {
                enumSoundNames.add(SoundId.name());
            }
        }

        @Test
        @DisplayName("Sounds config is not null.")
        void SoundConfigNotNull() {
            Assertions.assertNotNull(plugin.soundConfig);
        }

        @SuppressWarnings("unused")
        Collection<String> GetConfigFileKeys() {
            return plugin.soundConfig.getSoundConfigKeys();
        }

        @ParameterizedTest
        @EnumSource(SoundId.class)
        @DisplayName("enum member soundId is contained in getConfig() keys.")
        void FileKeysContainsEnumValue(SoundId soundId) {
            Assertions.assertTrue(plugin.soundConfig.isValidSoundConfigKey(soundId.name()),
                    "Enum value '" + soundId.name() + "' does not have matching key in sounds.yml.");
        }

        @ParameterizedTest
        @MethodSource("GetConfigFileKeys")
        @DisplayName("config file key has matching key in enum sound names")
        void SoundConfigEnumContainsAllFileSounds(String key) {
            Assertions.assertTrue(enumSoundNames.contains(key),
                    "File key does not have matching key in enum sound names.");
        }

        @ParameterizedTest
        @MethodSource("GetConfigFileKeys")
        @DisplayName("sound file key has valid bukkit sound name")
        void SoundConfigFileHasValidBukkitSound(String key) {
            String bukkitSoundName = plugin.soundConfig.getBukkitSoundName(key);
            Assertions.assertTrue(plugin.soundConfig.isValidBukkitSoundName(bukkitSoundName),
                    "File key '" + key + "' has invalid bukkit sound name: " + bukkitSoundName);
        }
    }

}
