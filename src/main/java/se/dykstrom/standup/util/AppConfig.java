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

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.dykstrom.standup.model.Settings;

import static java.lang.System.Logger.Level.ERROR;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A class that provides the application with configuration data.
 */
public final class AppConfig {

    private static final System.Logger LOGGER = System.getLogger(AppConfig.class.getName());

    private static final String USER_HOME = System.getProperty("user.home");
    private static final Path OLD_CONFIG_FILE = Path.of(USER_HOME + "/standup.json");
    private static final Path NEW_CONFIG_FILE = Path.of(USER_HOME + "/.config/standup/config.json");

    /** Cached settings. */
    private static Settings cachedSettings;

    private AppConfig() { }

    /**
     * Returns the time to sleep in minutes.
     *
     * @return The time to sleep in minutes.
     */
    public static int getSleepTime() {
        return getSettings().getSleepTime();
    }

    /**
     * Returns true if user should be reminded after a short period of time.
     *
     * @return True if user should be reminded.
     */
    public static boolean getReminder() {
        return getSettings().getReminder();
    }

    /**
     * Returns true if a sound should be played when the main window appears.
     *
     * @return True if a sound should be played.
     */
    public static boolean getPlaySound() {
        return getSettings().getPlaySound();
    }

    /**
     * Returns the filename of the sound file.
     *
     * @return The filename of the sound file.
     */
    public static String getSoundFilename() {
        return getSettings().getSoundFilename();
    }

    /**
     * Returns true if a special morning message should be displayed in the morning.
     *
     * @return True if morning message should be displayed.
     */
    public static boolean getMorningMessage() {
        return getSettings().getMorningMessage();
    }

	/**
	 * Returns the list of normal messages.
	 *
	 * @return The list of normal messages.
	 */
	public static List<String> getMessages() {
	    return getSettings().getMessages();
	}

    /**
     * Returns all settings in the form of a {@link Settings} object.
     */
	public static Settings getSettings() {
	    if (cachedSettings != null) {
	        return cachedSettings;
        }

	    if (Files.notExists(NEW_CONFIG_FILE)) {
	        migrateSettings();
        }
	    try (final FileReader reader = new FileReader(NEW_CONFIG_FILE.toFile(), UTF_8)) {
            final Gson gson = new Gson();
            cachedSettings = gson.fromJson(reader, Settings.class);
            return cachedSettings == null ? new Settings() : cachedSettings;
        } catch (IOException e) {
            LOGGER.log(ERROR, "Failed to load settings, returning default values", e);
            return new Settings();
        }
    }

    /**
     * Saves all settings to persistent storage.
     */
    public static void setSettings(final Settings settings) {
        cachedSettings = settings;

        try (final Writer writer = new FileWriter(NEW_CONFIG_FILE.toFile(), UTF_8)) {
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(settings, writer);
        } catch (IOException e) {
            LOGGER.log(ERROR, "Failed to save settings", e);
        }
    }

    /**
     * Migrates all settings from the old location "~/standup.json"
     * to the new location "~/.config/standup/config.json". If no old
     * settings exist, this method creates a new settings file with
     * default settings instead.
     */
    private static void migrateSettings() {
        try {
            // Make sure the settings directory exists
            Files.createDirectories(NEW_CONFIG_FILE.getParent());
            // Either move the existing file or create a new file with default settings
            if (Files.exists(OLD_CONFIG_FILE)) {
                Files.move(OLD_CONFIG_FILE, NEW_CONFIG_FILE);
            } else {
                setSettings(new Settings());
            }
        } catch (IOException e) {
            LOGGER.log(ERROR, "Failed to migrate settings from ''{0}'' to ''{1}'': {2}", OLD_CONFIG_FILE, NEW_CONFIG_FILE, e.getMessage());
        }
    }
}
