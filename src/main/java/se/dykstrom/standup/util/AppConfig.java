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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import se.dykstrom.standup.model.Settings;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.lang.System.Logger.Level.ERROR;

/**
 * A class that provides the application with configuration data.
 */
public final class AppConfig {

    private static final System.Logger LOGGER = System.getLogger(AppConfig.class.getName());

    private static final String FILE_NAME = System.getProperty("user.home") + "/standup.json";

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

	    if (Files.notExists(Path.of(FILE_NAME))) {
	        return new Settings();
        }
	    try (FileReader reader = new FileReader(FILE_NAME, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
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
    public static void setSettings(Settings settings) {
        cachedSettings = settings;

        try (Writer writer = new FileWriter(FILE_NAME, StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(settings, writer);
        } catch (IOException e) {
            LOGGER.log(ERROR, "Failed to save settings", e);
        }
    }
}
