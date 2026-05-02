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

package se.dykstrom.standup;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se.dykstrom.standup.gui.MainController;
import se.dykstrom.standup.i18n.I18n;
import se.dykstrom.standup.util.AppConfig;
import se.dykstrom.standup.util.IconUtil;

import java.io.IOException;

/**
 * The main application. Loads and shows the main window.
 */
public class StandUp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        I18n.setLanguage(AppConfig.getLanguage());
        loadMainScene(stage);
        stage.setTitle("StandUp!");
        IconUtil.setIcons(stage);
        stage.show();
        Platform.setImplicitExit(false);
    }

    private static void loadMainScene(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(StandUp.class.getResource("/fxml/main.fxml"), I18n.getBundle());
        Parent root = loader.load();
        Scene existing = stage.getScene();
        Scene scene = (existing == null)
                ? new Scene(root, 230, 200)
                : new Scene(root, existing.getWidth(), existing.getHeight());
        MainController controller = loader.getController();
        controller.setSceneDelegate(() -> loadMainScene(stage));
        stage.setScene(scene);
        stage.setOnCloseRequest(_ -> controller.exit());
    }
}
