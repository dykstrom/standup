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

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import se.dykstrom.standup.i18n.I18n;
import se.dykstrom.standup.util.Version;

/**
 * A controller class for the About dialog.
 */
public class AboutController {

    @FXML
    private DialogPane dialogPane;
    @FXML
    private Label versionLabel;
    @FXML
    private Label copyrightLabel;

    public void initialize() {
        versionLabel.setText(I18n.format("about.label.version", Version.instance()));
        copyrightLabel.setText(I18n.get("about.label.copyright"));

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.getStyleClass().addAll("primary", "sm");
        okButton.setText(I18n.get("button.ok"));
    }
}
