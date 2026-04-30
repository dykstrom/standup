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

import java.util.Locale;
import java.util.Objects;

/**
 * Enum of languages supported by the application. Adding a new language requires
 * one new constant here and one new messages_XX.properties file.
 */
public enum Language {

    ENGLISH(Locale.ENGLISH, "English"),
    SWEDISH(Locale.of("sv"), "Svenska");

    private final Locale locale;
    private final String displayName;

    Language(Locale locale, String displayName) {
        this.locale = locale;
        this.displayName = displayName;
    }

    public Locale getLocale() {
        return locale;
    }

    /** Returns the ISO 639-1 language code, e.g. "en" or "sv". */
    public String getCode() {
        return locale.getLanguage();
    }

    /** Returns the display name shown in the Settings dropdown. */
    @Override
    public String toString() {
        return displayName;
    }

    /** Returns the language matching the given code, or the default language if not found. */
    public static Language fromCode(String code) {
        for (Language lang : values()) {
            if (Objects.equals(lang.getCode(), code)) {
                return lang;
            }
        }
        return defaultLanguage();
    }

    /** Returns the best matching language for the given locale, or the default language if not found. */
    public static Language fromLocale(Locale locale) {
        return locale != null ? fromCode(locale.getLanguage()) : defaultLanguage();
    }

    @SuppressWarnings("SameReturnValue")
    public static Language defaultLanguage() {
        return ENGLISH;
    }
}
