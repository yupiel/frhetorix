package de.yupiel.frhetorix;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FrhetorixApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Frhetorix");

        TextArea textArea = new TextArea();
        Button firstButton = new Button("First Button");
        Button secondButton = new Button("Second Button");

        VBox basicVbox = new VBox(textArea, firstButton, secondButton);
        basicVbox.setSpacing(5);

        Scene mainScene = new Scene(basicVbox, 1280, 720);

        primaryStage.setScene(mainScene);

        primaryStage.show();
    }
}
