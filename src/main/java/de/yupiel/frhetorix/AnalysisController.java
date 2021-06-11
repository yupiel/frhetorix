package de.yupiel.frhetorix;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class AnalysisController {
    private CompletableFuture<ArrayList<Text>> tagCloudFuture;

    @FXML
    private TextFlow tagCloudTextFlow;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private Button analyzeButton;
    @FXML
    private Button cancelClearButton;

    @FXML
    private void analyzeButtonHandler(MouseEvent event) {
        buttonStateWorking(true);

        //Cleaning up words in the text area
        String[] wordsToTagCloud = inputTextArea.getText().replaceAll("[\\t\\n\\r]+", " ").replaceAll("\\p{Punct}", "").split(" ");

        //Asynchronously generating text cloud
        tagCloudFuture = CompletableFuture.supplyAsync(new TagCloudTask(getWordFrequency(wordsToTagCloud)))
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Analyzing failed with Exception: ");
                        throwable.printStackTrace();
                    } else if (result == null) {
                        System.out.println("Nothing to analyze");
                    } else {
                        addTextElementsToTagCloud(result);
                    }
                });
    }

    @FXML
    private void cancelClearButtonHandler(MouseEvent event) {
        if (tagCloudFuture != null && !tagCloudFuture.isDone()) {
            System.out.println("Canceling Tag Cloud Generation...");
            tagCloudFuture.cancel(true);
            buttonStateWorking(false);
            return;
        }

        inputTextArea.clear();
        tagCloudTextFlow.getChildren().clear();
    }

    private HashMap<String, Integer> getWordFrequency(String[] words) {
        HashMap<String, Integer> countingMap = new HashMap<>();
        for (String word : words) {
            countingMap.compute(word, (k, v) -> v == null ? 1 : v + 1);
        }

        System.out.println(countingMap);
        return countingMap;
    }

    private void addTextElementsToTagCloud(ArrayList<Text> words) {
        Platform.runLater(() -> {   //needed here because JavaFX won't update the Scene unless this happens on the main thread
            for (Text word : words) {
                tagCloudTextFlow.getChildren().add(word);
            }

            buttonStateWorking(false);
        });
    }

    //Button eye candy
    private void buttonStateWorking(boolean working) {
        if (working) {
            cancelClearButton.setText("Abort");
            analyzeButton.setText("Analyzing...");
            analyzeButton.setDisable(true);
        } else {
            analyzeButton.setText("Analyze");
            analyzeButton.setDisable(false);
            cancelClearButton.setText("Clear");
        }
    }
}
