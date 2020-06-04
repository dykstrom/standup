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

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Provides the current version of the application.
 */
public final class Version {

    private String version = "<no version>";

    /**
     * The singleton instance of this class.
     */
    private static final class Holder {
        public static final Version INSTANCE = new Version();
    }

    /**
     * Returns the singleton {@code Version} instance.
     *
     * @return The singleton {@code Version} instance.
     */
    @SuppressWarnings("SameReturnValue")
    public static Version instance() {
        return Holder.INSTANCE;
    }

    private Version() {
        try {
            URL url = Version.class.getResource("/version.properties");
            if (url != null) {
                Properties properties = new Properties();
                properties.load(url.openStream());
                version = properties.getProperty("standup.version");
            }
        } catch (IOException ignore) {
            // Ignore
        }
    }

    @Override
    public String toString() {
        return version;
    }
}
