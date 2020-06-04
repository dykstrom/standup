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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.util.Duration;
import se.dykstrom.standup.model.Settings;
import se.dykstrom.standup.util.AppConfig;
import se.dykstrom.standup.util.IconUtil;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A controller class for the main window.
 */
public class MainController implements Initializable {

    @FXML
    private Label label;

    /** Used to schedule the wake-up task that shows the main window after some time. */
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    /** The date when the frame was last displayed. Used to find out if midnight has passed. */
    private LocalDate lastDisplayDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText(getRandomMessage());
    }

    @FXML
    private void handleHideAction(ActionEvent event) {
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
                    // Convert the filename to an URL
                    source = Paths.get(filename).toUri().toString();
                }
                AudioClip clip = new AudioClip(source);
                clip.play();
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
                label.setText("Good morning!");
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
        FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/fxml/settings.fxml"));
        DialogPane dialogPane = loader.load();

        SettingsController controller = loader.getController();
        controller.initialize(AppConfig.getSettings());

        // Style default buttons
        dialogPane.lookupButton(ButtonType.OK).getStyleClass().addAll("primary", "sm");
        dialogPane.lookupButton(ButtonType.CANCEL).getStyleClass().addAll("sm");

        Dialog<Settings> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setDialogPane(dialogPane);
        dialog.setResultConverter(controller.getResultConverter());
        IconUtil.setIcons((Stage) dialogPane.getScene().getWindow());
        Optional<Settings> result = dialog.showAndWait();
        result.ifPresent(AppConfig::setSettings);
    }

    @FXML
    public void handleFileExitAction() {
        exit();
    }

    @FXML
    private void handleHelpAboutAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/fxml/about.fxml"));
        DialogPane dialogPane = loader.load();
        // Style default buttons
        dialogPane.lookupButton(ButtonType.OK).getStyleClass().addAll("primary", "sm");

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("About");
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
        return messages.get(new Random().nextInt(messages.size()));
    }

    /**
     * Returns true if the clock has passed midnight, and it is now the next day.
     */
    private boolean isNextDay() {
        return LocalDate.now().isAfter(lastDisplayDate);
    }
}
