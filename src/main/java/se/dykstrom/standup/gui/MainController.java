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

package se.dykstrom.standup.gui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.util.Duration;
import se.dykstrom.standup.i18n.I18n;
import se.dykstrom.standup.i18n.Language;
import se.dykstrom.standup.model.Settings;
import se.dykstrom.standup.util.AppConfig;
import se.dykstrom.standup.util.IconUtil;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.System.Logger.Level.ERROR;

/**
 * A controller class for the main window.
 */
public class MainController {

    private static final System.Logger LOGGER = System.getLogger(MainController.class.getName());

    @FXML
    private Label label;

    /** Used to schedule the wake-up task that shows the main window after some time. */
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final Random random = new Random();

    /** The date when the frame was last displayed. Used to find out if midnight has passed. */
    private LocalDate lastDisplayDate;

    /** The audio clip to play when showing the main window. */
    private AudioClip audioClip;

    /** Used to reload the main scene after language change. */
    private SceneDelegate sceneDelegate;

    public void setSceneDelegate(SceneDelegate sceneDelegate) {
        this.sceneDelegate = sceneDelegate;
    }

    public void initialize() {
        label.setText(getRandomMessage());
    }

    @FXML
    private void handleHideAction(ActionEvent event) {
        if (audioClip != null) {
            audioClip.stop();
        }

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.hide();

        executorService.schedule(() -> show(stage), AppConfig.getSleepTime(), TimeUnit.MINUTES);
    }

    /**
     * Shows the stage if hidden, possibly displaying a new text and playing a sound.
     */
    private void show(Stage stage) {
        Platform.runLater(() -> {
            label.setText(getRandomMessage());

            if (AppConfig.getPlaySound()) {
                String filename = AppConfig.getSoundFilename();
                String source;
                if (filename.startsWith("http")) {
                    // The filename is not a filename at all, but a URL
                    source = filename;
                } else {
                    // Convert the filename to a URL
                    source = Paths.get(filename).toUri().toString();
                }
                audioClip = new AudioClip(source);
                audioClip.play();
            }

            stage.show();
            animateAndRemind(stage);
        });
    }

    /**
     * Animates the text label, and possibly starts a background task to remind the user.
     */
    private void animateAndRemind(Stage stage) {
        lastDisplayDate = LocalDate.now();

        FadeTransition transition = new FadeTransition(Duration.seconds(0.2), label);
        transition.setToValue(0.0);
        transition.setAutoReverse(true);
        transition.setCycleCount(6);
        transition.play();

        if (AppConfig.getReminder()) {
            executorService.schedule(() -> remind(stage), 1, TimeUnit.MINUTES);
        }
    }

    /**
     * Reminds the user that it is still time to stand up.
     */
    private void remind(Stage stage) {
        Platform.runLater(() -> {
            // If we have passed midnight change message to a morning greeting
            if (isNextDay() && AppConfig.getMorningMessage()) {
                label.setText(I18n.get("main.message.goodMorning"));
            }

            // If frame is still visible, bring it to the front
            if (stage.isShowing()) {
                stage.toFront();
                animateAndRemind(stage);
            }
        });
    }

    @FXML
    private void handleFileSettingsAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/fxml/settings.fxml"), I18n.getBundle());
        DialogPane dialogPane = loader.load();

        SettingsController controller = loader.getController();
        controller.initialize(AppConfig.getSettings());

        Dialog<Settings> dialog = new Dialog<>();
        dialog.setTitle(I18n.get("settings.dialog.title"));
        dialog.setDialogPane(dialogPane);
        dialog.setResultConverter(controller.getResultConverter());
        IconUtil.setIcons((Stage) dialogPane.getScene().getWindow());
        Optional<Settings> result = dialog.showAndWait();
        result.ifPresent(this::updateSettings);
    }

    private void updateSettings(Settings newSettings) {
        final var oldLanguage = AppConfig.getSettings().getLanguage();
        final var newLanguage = newSettings.getLanguage();

        AppConfig.setSettings(newSettings);

        // Reload UI in new language if it has changed
        if (!newLanguage.equals(oldLanguage)) {
            I18n.setLanguage(Language.fromCode(newLanguage));
            try {
                sceneDelegate.reloadScene();
                executorService.shutdownNow();
            } catch (IOException e) {
                LOGGER.log(ERROR, "Failed to reload main scene after language change", e);
            }
        }
    }

    @FXML
    public void handleFileExitAction() {
        exit();
    }

    @FXML
    private void handleHelpAboutAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/fxml/about.fxml"), I18n.getBundle());
        DialogPane dialogPane = loader.load();

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(I18n.get("about.dialog.title"));
        dialog.setDialogPane(dialogPane);
        IconUtil.setIcons((Stage) dialogPane.getScene().getWindow());
        dialog.showAndWait();
    }

    /**
     * Exits the application.
     */
    public void exit() {
        executorService.shutdownNow();
        Platform.exit();
    }

    /**
     * Returns a random message from one of the configured messages.
     */
    private String getRandomMessage() {
        List<String> messages = AppConfig.getMessages();
        return messages.get(random.nextInt(messages.size()));
    }

    /**
     * Returns true if the clock has passed midnight, and it is now the next day.
     */
    private boolean isNextDay() {
        return LocalDate.now().isAfter(lastDisplayDate);
    }
}
