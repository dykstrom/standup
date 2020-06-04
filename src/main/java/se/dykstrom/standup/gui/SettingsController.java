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

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import se.dykstrom.standup.model.Settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * A controller class for the Settings dialog.
 */
public class SettingsController {

    @FXML
    private Spinner<Integer> sleepTimeSpinner;
    @FXML
    private CheckBox playSoundCheckBox;
    @FXML
    private TextField filenameTextField;
    @FXML
    private Button browseButton;
    @FXML
    private CheckBox reminderCheckBox;
    @FXML
    private CheckBox morningCheckBox;
    @FXML
    private ListView<String> messagesListView;
    @FXML
    private Button removeButton;

    private final ObservableList<String> messages = FXCollections.observableList(new ArrayList<>());

    /**
     * Initializes the Settings dialog with the provided settings.
     */
    public void initialize(Settings settings) {
        // Initialize controls
        sleepTimeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 30));
        messagesListView.setItems(messages);
        messagesListView.setCellFactory(param -> new TextFieldListCell<>(new DefaultStringConverter()));

        // Enable or disable controls
        filenameTextField.disableProperty().bind(playSoundCheckBox.selectedProperty().not());
        browseButton.disableProperty().bind(playSoundCheckBox.selectedProperty().not());
        removeButton.disableProperty().bind(messagesListView.getSelectionModel().selectedItemProperty().isNull()
                .or(Bindings.size(messagesListView.itemsProperty().getValue()).lessThanOrEqualTo(1))
        );

        // Initialize values
        sleepTimeSpinner.getValueFactory().setValue(settings.getSleepTime());
        playSoundCheckBox.setSelected(settings.getPlaySound());
        filenameTextField.setText(initializeFilename(settings.getSoundFilename()));
        reminderCheckBox.setSelected(settings.getReminder());
        morningCheckBox.setSelected(settings.getMorningMessage());

        messages.addAll(initializeMessages(settings.getMessages()));
    }

    private String initializeFilename(String filename) {
        return filename == null || filename.isBlank() ? "" : filename;
    }

    private List<String> initializeMessages(List<String> messages) {
        return messages == null || messages.isEmpty() ? singletonList("Stand Up!") : messages;
    }

    public Callback<ButtonType, Settings> getResultConverter() {
        return buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new Settings(
                        sleepTimeSpinner.getValue(),
                        reminderCheckBox.isSelected(),
                        playSoundCheckBox.isSelected(),
                        filenameTextField.getText().strip(),
                        morningCheckBox.isSelected(),
                        messages
                );
            } else {
                return null;
            }
        };
    }

    @FXML
    public void handleBrowseAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File initialFile = new File(filenameTextField.getText().strip());
        if (initialFile.exists()) {
            fileChooser.setInitialDirectory(initialFile.getParentFile());
        }
        Window owner = ((Button) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(owner);
        if (file != null) {
            filenameTextField.setText(file.getPath());
        }
    }

    @FXML
    public void handleAddAction() {
        messages.add("");
        int index = messages.size() - 1;
        messagesListView.requestFocus();
        messagesListView.getSelectionModel().select(index);
        messagesListView.scrollTo(index);
    }

    @FXML
    public void handleRemoveAction() {
        // We will only get here if there is a list item selected
        messages.remove(messagesListView.getSelectionModel().getSelectedIndex());
    }
}
