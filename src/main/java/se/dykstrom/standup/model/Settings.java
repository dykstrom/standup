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

package se.dykstrom.standup.model;

import java.util.*;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

/**
 * A Java bean to keep track of the user settings.
 */
public final class Settings {

    private final int sleepTime;
    private final boolean reminder;
    private final boolean playSound;
    private final String soundFilename;
    private final boolean morningMessage;
    private final List<String> messages = new ArrayList<>();

    public Settings() {
        this(30, false, false, "", false, singletonList("Stand Up!"));
    }

    public Settings(int sleepTime, boolean reminder, boolean playSound, String soundFilename, boolean morningMessage, Collection<String> messages) {
        this.sleepTime = sleepTime;
        this.reminder = reminder;
        this.playSound = playSound;
        this.soundFilename = requireNonNull(soundFilename);
        this.morningMessage = morningMessage;
        if (messages.isEmpty()) {
            throw new IllegalArgumentException("empty list of messages");
        }
        this.messages.addAll(messages);
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public boolean getReminder() {
        return reminder;
    }

    public boolean getPlaySound() {
        return playSound;
    }

    public String getSoundFilename() {
        return soundFilename;
    }

    public boolean getMorningMessage() {
        return morningMessage;
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Settings.class.getSimpleName() + "[", "]")
                .add("sleepTime=" + sleepTime)
                .add("reminder=" + reminder)
                .add("playSound=" + playSound)
                .add("soundFilename='" + soundFilename + "'")
                .add("morningMessage=" + morningMessage)
                .add("messages=" + messages)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Settings that)) return false;
        return this.sleepTime == that.sleepTime &&
               this.reminder == that.reminder &&
               this.playSound == that.playSound &&
               this.morningMessage == that.morningMessage &&
               this.soundFilename.equals(that.soundFilename) &&
               this.messages.equals(that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sleepTime, reminder, playSound, soundFilename, morningMessage, messages);
    }
}
