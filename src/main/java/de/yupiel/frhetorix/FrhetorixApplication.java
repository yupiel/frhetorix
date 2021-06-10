package de.yupiel.frhetorix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class FrhetorixApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Frhetorix");

        URL fxmlFilePath = new File("src/main/resources/AnalysisView.fxml").toURI().toURL();
        Parent content = new FXMLLoader(fxmlFilePath).load();

        primaryStage.setScene(new Scene(content));

        primaryStage.show();
    }
}
