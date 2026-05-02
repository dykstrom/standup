/*
 * Copyright 2026 Johan Dykström
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

package se.dykstrom.standup.i18n;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

class I18nTest {

    @AfterEach
    void resetLanguage() {
        I18n.setLanguage(Language.ENGLISH);
    }

    @Test
    void getBundleShouldReturnEnglishBundle() {
        I18n.setLanguage(Language.ENGLISH);
        ResourceBundle bundle = I18n.getBundle();
        assertNotNull(bundle);
        assertEquals("Good morning!", bundle.getString("main.message.goodMorning"));
    }

    @Test
    void getBundleShouldReturnSwedishBundle() {
        I18n.setLanguage(Language.SWEDISH);
        ResourceBundle bundle = I18n.getBundle();
        assertNotNull(bundle);
        assertEquals("God morgon!", bundle.getString("main.message.goodMorning"));
    }

    @Test
    void getShouldReturnTranslatedString() {
        I18n.setLanguage(Language.ENGLISH);
        assertEquals("Good morning!", I18n.get("main.message.goodMorning"));
    }

    @Test
    void getShouldReturnKeyForMissingString() {
        assertEquals("no.such.key", I18n.get("no.such.key"));
    }

    @Test
    void formatShouldInterpolateArguments() {
        I18n.setLanguage(Language.ENGLISH);
        assertEquals("Version 1.2.3", I18n.format("about.label.version", "1.2.3"));
    }

    @Test
    void setLanguageShouldSwitchBundle() {
        I18n.setLanguage(Language.ENGLISH);
        assertEquals("Settings", I18n.get("settings.dialog.title"));

        I18n.setLanguage(Language.SWEDISH);
        assertEquals("Inställningar", I18n.get("settings.dialog.title"));
    }

    @Test
    void bothBundlesShouldHaveTheSameKeys() {
        I18n.setLanguage(Language.ENGLISH);
        ResourceBundle english = I18n.getBundle();

        I18n.setLanguage(Language.SWEDISH);
        ResourceBundle swedish = I18n.getBundle();

        for (String key : english.keySet()) {
            assertTrue(swedish.containsKey(key), "Swedish bundle is missing key: " + key);
        }
        for (String key : swedish.keySet()) {
            assertTrue(english.containsKey(key), "English bundle has extra key not in Swedish: " + key);
        }
    }
}
