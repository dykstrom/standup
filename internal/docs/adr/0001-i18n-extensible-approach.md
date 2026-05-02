# ADR-0001: Internationalization with I18n Utility and SupportedLanguage Enum

**Date:** 2026-04-27
**Status:** Accepted
**Feature:** Internationalization (i18n) — English and Swedish UI support

## Context

StandUp is a JavaFX desktop application with all UI strings hardcoded in English across FXML files and Java controllers. To support users who prefer Swedish, the application needs to externalize UI strings and load them based on a user-selected language.

Key constraints:
- Java 21 + JavaFX 24 under JPMS (`se.dykstrom.standup` module)
- Settings are persisted to JSON via Gson — locale preference must survive restarts
- Language must be selectable by the user in the Settings dialog
- UI must reload immediately when language changes (no app restart required)
- User-defined reminder messages are not translated (they are user data)
- "StandUp!" brand title stays unchanged in all languages

## Decision

We will use an extensible approach centered on two new classes:

- **`I18n.java`** — static utility that owns the active `Locale` and `ResourceBundle`. All string lookups go through `I18n.get("key")` or `I18n.format("key", args)` for parameterized strings. Exposes `getSupportedLanguages()` for the settings dropdown.
- **`SupportedLanguage.java`** — enum listing every language StandUp ships with (`ENGLISH`, `SWEDISH`), each carrying its `Locale` and display name. Adding a new language requires one enum constant and one `.properties` file.

UI strings move to `src/main/resources/i18n/messages.properties` (English default) and `messages_sv.properties` (Swedish). FXML files reference bundle keys via the `%key` syntax, which JavaFX's `FXMLLoader` resolves automatically when given a `ResourceBundle`. Dynamic strings in Java code (`"Good morning!"`, dialog titles) are looked up via `I18n.get(...)`.

When the user selects a different language and clicks OK, `MainController` calls `I18n.setLanguage(newLang)`, shuts down its current scheduled executor, and reloads `main.fxml` with the new bundle — swapping the scene root on the existing `Stage` to preserve window position and size.

## Consequences

### Positive
- Adding a third language in the future requires only one enum constant + one `.properties` file — no controller or infrastructure changes.
- `SupportedLanguage.values()` drives the Settings dropdown automatically; no manual list to maintain.
- `I18n.get(key)` provides a safe, consistent API with a missing-key fallback that prevents crashes from forgotten translations.
- The enum's `toString()` returns the display name directly, so no custom `StringConverter` is needed in the settings combo box.

### Negative
- Two additional source files (`I18n.java`, `SupportedLanguage.java`) compared to the minimal approach.
- A new `i18n` subpackage is introduced, which needs an `opens i18n;` directive in `module-info.java` for JPMS resource loading.
- `I18n` uses static mutable state (the current language/bundle), which is a global side-effect — acceptable for a single-window desktop app.

### Risks
- **JPMS resource access:** `ResourceBundle.getBundle` may fail if the module doesn't open the `i18n` package. Mitigation: add `opens i18n;` in `module-info.java` and cover with a unit test that loads both bundles.
- **Thread leak on UI reload:** the old `MainController`'s `ScheduledExecutorService` must be shut down before the scene root is swapped. Mitigation: call `executorService.shutdownNow()` in a `disposeForReload()` method called by `StandUp.reloadMainScene()`.
- **Properties file encoding:** Swedish characters (å, ä, ö) require UTF-8 or `\uXXXX` escapes. Mitigation: use `\uXXXX` escapes in `.properties` files for portability across all toolchains.

## Alternatives Considered

### Alternative 1: Minimal — no new classes
`AppConfig.getResourceBundle()` serves as the bundle access point. A private `LanguageOption` record in `SettingsController` handles display names. The bundle is passed explicitly through controller constructors/setters.
- **Pros:** Fewer files, no new abstractions.
- **Cons:** Adding a third language requires edits in `SettingsController` and `AppConfig`; no single place to discover supported languages.
- **Why rejected:** The user prefers future extensibility; the overhead of the two extra classes is low.

### Alternative 2: Existing patterns — Locale-typed ComboBox
Like Approach 1 but types the combo box as `ComboBox<Locale>` with a `StringConverter` and adds an explicit `messages_en.properties` alongside `messages.properties`.
- **Pros:** Avoids a custom type; leverages `Locale.getDisplayLanguage()` for display names.
- **Cons:** `StringConverter` boilerplate; using raw `Locale` in the UI couples the view to a JDK type rather than a domain type.
- **Why rejected:** The `SupportedLanguage` enum (Approach 2) is cleaner and more refactor-safe.

## Implementation Notes

**New files to create:**
- `src/main/java/se/dykstrom/standup/i18n/I18n.java`
- `src/main/java/se/dykstrom/standup/i18n/SupportedLanguage.java`
- `src/main/resources/i18n/messages.properties` (English default)
- `src/main/resources/i18n/messages_sv.properties` (Swedish)

**Files to modify:**
- `Settings.java` — add `String language` field (default `"en"`, Gson null-safe)
- `AppConfig.java` — add `getLanguage()` returning `SupportedLanguage`
- `StandUp.java` — initialize `I18n` from `AppConfig.getLanguage()`, load FXML with bundle, expose `reloadMainScene(Stage)`
- `MainController.java` — store bundle field, use `I18n.get()` for "Good morning!" and dialog titles, pass bundle to sub-dialog loaders, detect locale change and call `StandUp.reloadMainScene()`
- `SettingsController.java` — add `ChoiceBox<SupportedLanguage>`, populate from `I18n.getSupportedLanguages()`
- `AboutController.java` — use `I18n.format("about.label.version", Version.instance())`
- `main.fxml` — replace literal strings with `%key` references
- `settings.fxml` — replace literal strings with `%key` references, add Language row
- `module-info.java` — add `opens se.dykstrom.standup.i18n;`

**Key patterns to follow:**
- `I18n` is `final` with a private constructor, matching the style of `AppConfig`.
- Persist language as a plain `String` tag (`"en"`, `"sv"`) in `Settings` — no custom Gson adapter needed.
- UI reload swaps `stage.getScene().setRoot(newRoot)` to preserve window geometry.

## References

- JavaFX FXMLLoader ResourceBundle integration: https://openjfx.io/javadoc/24/javafx.fxml/javafx/fxml/FXMLLoader.html
- Java ResourceBundle documentation: https://docs.oracle.com/en/java/docs/api/java.base/java/util/ResourceBundle.html
- JPMS resource access: https://openjdk.org/projects/jigsaw/spec/sotms/#resources
