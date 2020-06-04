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
import javafx.scene.Scene;
import javafx.stage.Stage;
import se.dykstrom.standup.gui.MainController;
import se.dykstrom.standup.util.IconUtil;

import java.io.IOException;

/**
 * The main application. Loads and shows the main window.
 */
public class StandUp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(StandUp.class.getResource("/fxml/main.fxml"));

        Scene scene = new Scene(loader.load(), 230, 200);
        stage.setScene(scene);
        stage.setTitle("StandUp!");
        IconUtil.setIcons(stage);
        stage.setOnCloseRequest(e -> ((MainController) loader.getController()).exit());
        stage.show();

        Platform.setImplicitExit(false);
    }

    public static void main(String[] args) {
        launch();
    }
}
