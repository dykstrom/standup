# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

StandUp is a JavaFX desktop application that periodically reminds users to stand up and leave their desk. The application hides after showing a reminder and reappears after a configurable time period.

## Build System

This is a Maven-based Java project using Java 25 and JavaFX 24.

### Key Commands

**Build and run the application:**
```bash
mvn javafx:run
```

**Run tests:**
```bash
mvn test
```

**Run a single test:**
```bash
mvn test -Dtest=AppConfigTest
```

**Package the application:**
```bash
mvn package
```

This creates a custom JRE image using jlink and generates distribution archives in `target/`.

**Clean build:**
```bash
mvn clean install
```

## Architecture

### Main Application Flow

1. **Entry point**: `StandUp.java` (extends `javafx.application.Application`) launches the JavaFX application and loads the main FXML window
2. **Main window**: Controlled by `MainController.java`, which manages the reminder lifecycle using a `ScheduledExecutorService`
3. **Settings persistence**: `AppConfig.java` handles loading/saving settings from `~/.config/standup/config.json` using Gson
4. **Settings model**: `Settings.java` is an immutable model containing all user preferences (sleep time, reminder options, messages, sound settings)

### Key Components

**MainController** (`src/main/java/se/dykstrom/standup/gui/MainController.java`):
- Manages the reminder scheduling logic
- Uses `ScheduledExecutorService` to hide/show the window periodically
- Displays random messages from configured list
- Handles sound playback and reminder animations
- Implements midnight detection for morning messages
- Decoupled from `StandUp` via `SceneDelegate` — calls `delegate.reloadScene()` on language change

**AppConfig** (`src/main/java/se/dykstrom/standup/util/AppConfig.java`):
- Singleton-style utility for accessing settings
- Caches settings in memory
- Handles migration from old config location (`~/standup.json`) to new location (`~/.config/standup/config.json`)
- Uses Gson for JSON serialization/deserialization

**Settings** (`src/main/java/se/dykstrom/standup/model/Settings.java`):
- Immutable value object containing: sleepTime (minutes), reminder flag, playSound flag, soundFilename, morningMessage flag, list of messages, and language code
- Provides default values: 30 minute sleep time, no reminder, no sound, "Stand Up!" as default message

### GUI Structure

- JavaFX FXML files in `src/main/resources/fxml/`
- Controllers in `src/main/java/se/dykstrom/standup/gui/`
- Dialogs: Settings dialog (`SettingsController`), About dialog (`AboutController`)
- Each controller is responsible for styling and labelling its own dialog buttons
- Icons managed by `IconUtil.java`
- Styled with jbootx theme

### Internationalization (i18n)

- Resource bundles in `src/main/resources/i18n/`: `messages.properties` (English) and `messages_sv.properties` (Swedish)
- `I18n` (`src/main/java/se/dykstrom/standup/i18n/I18n.java`): central access point — controllers load FXML with `I18n.getBundle()` and look up dynamic strings with `I18n.get(key)` or `I18n.format(key, args)`
- `Language` (`src/main/java/se/dykstrom/standup/i18n/Language.java`): enum of supported languages; adding a new language requires one new constant and one new `messages_XX.properties` file
- Language is persisted in `Settings` as an ISO 639-1 code and applied at startup via `I18n.setLanguage()`
- Changing language reloads the main scene via `SceneDelegate`
- `ButtonType.OK` and `ButtonType.CANCEL` in FXML use JavaFX's internal bundle; their displayed text is overridden in each controller's `initialize()` using `I18n.get("button.ok")` / `I18n.get("button.cancel")`

### Module System

This project uses Java Platform Module System (JPMS):
- Module name: `se.dykstrom.standup`
- Module descriptor: `src/main/java/module-info.java`
- Opens `gui` package to javafx.fxml for reflection
- Opens `model` package to com.google.gson for serialization
- Exports `se.dykstrom.standup` and `se.dykstrom.standup.i18n`

## Testing Notes

Tests use JUnit 5 (Jupiter).

## Dependencies

- JavaFX 24.0.1 (controls, media, fxml)
- Gson 2.11.0 (JSON serialization)
- JUnit 5.13.3 (testing)

## Development Guidelines

- **Unit tests required:** When adding new functionality, always add unit tests to verify it.
- **Cross-platform:** The application must work on Linux, macOS, and Windows. Avoid OS-specific APIs or path separators.
- **Plan first:** ALWAYS start in plan mode. Create and display a plan to the user before making any changes.

## Important Implementation Details

- Application uses `Platform.setImplicitExit(false)` to keep running in background when window is hidden
- Audio playback supports both local file paths and URLs
- Settings are cached in memory to avoid repeated file I/O
- The application includes middleware migration logic to move config files between versions
