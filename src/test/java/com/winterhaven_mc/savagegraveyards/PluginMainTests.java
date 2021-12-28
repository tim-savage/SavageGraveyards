package com.winterhaven_mc.savagegraveyards;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.bukkit.configuration.Configuration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.Set;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PluginMainTests {

    private ServerMock server;
    private PluginMain plugin;

    @BeforeAll
    public void setUp() {
        // Start the mock server
        server = MockBukkit.mock();

        // start the mock plugin
        plugin = MockBukkit.load(PluginMain.class);
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
            Assertions.assertNotNull(plugin.languageHandler);
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

        @SuppressWarnings("unused")
        public void Config() {
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
            Assertions.assertEquals("en-US", config.getString("language"));
        }

        Set<String> ConfigFileKeys() {
            return config.getKeys(false);
        }

//        @ParameterizedTest
//        @DisplayName("file config key is contained in enum.")
//        @MethodSource("ConfigFileKeys")
//        void ConfigFileKeyNotNull(String key) {
//            Assertions.assertNotNull(key);
//            Assertions.assertTrue(enumConfigKeyStrings.contains(key));
//        }

//        @ParameterizedTest
//        @EnumSource(ConfigSetting.class)
//        @DisplayName("ConfigSetting enum matches config file key/value pairs.")
//        void ConfigFileKeysContainsEnumKey(ConfigSetting configSetting) {
//            Assertions.assertEquals(configSetting.getValue(), plugin.getConfig().getString(configSetting.getKey()));
//        }
    }

}
