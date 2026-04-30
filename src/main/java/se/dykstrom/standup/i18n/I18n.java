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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Central access point for translated UI strings. Controllers load FXML with
 * {@link #getBundle()} and look up dynamic strings with {@link #get(String)}.
 */
public final class I18n {

    private static final String BUNDLE_BASE = "i18n.messages";

    private static Language currentLanguage = Language.defaultLanguage();
    private static ResourceBundle currentBundle;

    private I18n() {}

    /** Sets the active language and invalidates the cached bundle. */
    public static void setLanguage(Language language) {
        currentLanguage = language;
        currentBundle = null;
    }

    /** Returns the active ResourceBundle, loading it lazily on first call or after a language change. */
    public static ResourceBundle getBundle() {
        if (currentBundle == null) {
            currentBundle = ResourceBundle.getBundle(BUNDLE_BASE, currentLanguage.getLocale(), I18n.class.getModule());
        }
        return currentBundle;
    }

    /** Returns the translated string for the given key, or the key itself if not found. */
    public static String get(String key) {
        try {
            return getBundle().getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /** Returns a translated and formatted string using {@link MessageFormat}. */
    public static String format(String key, Object... args) {
        return MessageFormat.format(get(key), args);
    }
}
