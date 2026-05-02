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

package se.dykstrom.standup.model;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {

    @Test
    void sevenArgConstructorShouldStoreLanguage() {
        Settings settings = new Settings(10, false, false, "", false, List.of("msg"), "sv");
        assertEquals("sv", settings.getLanguage());
    }

    @Test
    void getLanguageShouldReturnEnglishForNullField() {
        // Simulates a Settings object deserialized from JSON without a language field (Gson sets it to null)
        Settings settings = new Settings(10, false, false, "", false, List.of("msg"), null);
        assertEquals("en", settings.getLanguage());
    }

    @Test
    void getLanguageShouldReturnEnglishForBlankField() {
        Settings settings = new Settings(10, false, false, "", false, List.of("msg"), "  ");
        assertEquals("en", settings.getLanguage());
    }

    @Test
    void equalsShouldIncludeLanguage() {
        Settings english = new Settings(10, false, false, "", false, List.of("msg"), "en");
        Settings swedish = new Settings(10, false, false, "", false, List.of("msg"), "sv");
        assertNotEquals(english, swedish);
    }

    @Test
    void equalsShouldBeTrueForSameLanguage() {
        Settings a = new Settings(10, false, false, "", false, List.of("msg"), "sv");
        Settings b = new Settings(10, false, false, "", false, List.of("msg"), "sv");
        assertEquals(a, b);
    }

    @Test
    void hashCodeShouldIncludeLanguage() {
        Settings english = new Settings(10, false, false, "", false, List.of("msg"), "en");
        Settings swedish = new Settings(10, false, false, "", false, List.of("msg"), "sv");
        assertNotEquals(english.hashCode(), swedish.hashCode());
    }

    @Test
    void gsonDeserializationWithoutLanguageFieldShouldDefaultToEnglish() {
        // Gson uses Unsafe to set final fields, bypassing constructors — language will be null
        String json = "{\"sleepTime\":10,\"reminder\":false,\"playSound\":false,\"soundFilename\":\"\",\"morningMessage\":false,\"messages\":[\"msg\"]}";
        Settings settings = new Gson().fromJson(json, Settings.class);
        assertEquals("en", settings.getLanguage());
    }
}
