package de.yupiel.frhetorix;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class AnalysisController {
    private CompletableFuture<String[]> tagCloudFuture;

    @FXML
    private TextArea inputTextArea;
    @FXML
    private Button analyzeButton;
    @FXML
    private Button cancelClearButton;

    @FXML
    private void analyzeButtonHandler(MouseEvent event) {
        System.out.println("Button Pressed");

        tagCloudFuture = CompletableFuture.supplyAsync(new TagCloudTask(new String[]{"something", "something else"}))
                .whenComplete((result, throwable) -> {
                    printResult(result);
                });
    }

    @FXML
    private void cancelClearButtonHandler(MouseEvent event) {
        if (tagCloudFuture != null && !tagCloudFuture.isDone()) {
            System.out.println("Canceling Tag Cloud Generation...");
            tagCloudFuture.cancel(true);
            return; //since the button will have another function later on this is needed here to not overlap functionalities
        }
    }

    private void printResult(String[] words) {
        System.out.println(Arrays.toString(words));
    }
}
