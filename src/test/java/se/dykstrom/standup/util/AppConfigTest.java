/*
 * Copyright 2020 Johan Dykström
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.dykstrom.standup.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.dykstrom.standup.model.Settings;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AppConfigTest {

    private static Settings originalSettings;

    @BeforeAll
    static void setUpClass() {
        originalSettings = AppConfig.getSettings();
    }

    @AfterAll
    static void tearDownClass() {
        AppConfig.setSettings(originalSettings);
    }

    @Test
    void shouldSaveAndLoadSettings() {
        // Given
        Settings expectedSettings = new Settings(10, true, true, "foo", false, List.of("bar", "tee"));

        // When
        AppConfig.setSettings(expectedSettings);
        Settings actualSettings = AppConfig.getSettings();

        // Then
        assertEquals(expectedSettings, actualSettings);
    }

    @Test
    void shouldReturnSameInstanceEveryTime() {
        // Given
        Settings savedSettings = new Settings();

        // When
        AppConfig.setSettings(savedSettings);
        Settings firstSettings = AppConfig.getSettings();
        Settings secondSettings = AppConfig.getSettings();

        // Then
        assertSame(savedSettings, firstSettings);
        assertSame(firstSettings, secondSettings);
    }
}
