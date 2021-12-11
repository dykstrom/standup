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

import javafx.scene.image.Image;
import javafx.stage.Stage;

import static java.util.Objects.requireNonNull;

/**
 * Icon utility methods.
 */
public final class IconUtil {

    private IconUtil() { }

    /**
     * Sets the application icons for the given stage.
     */
    public static void setIcons(Stage stage) {
        stage.getIcons().addAll(
                new Image(requireNonNull(IconUtil.class.getResourceAsStream("/images/arrow-186-16.png"))),
                new Image(requireNonNull(IconUtil.class.getResourceAsStream("/images/arrow-186-32.png")))
        );
    }
}
