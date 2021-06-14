package de.yupiel.frhetorix.controller;

import com.google.gson.Gson;
import de.yupiel.frhetorix.TagCloudTask;
import de.yupiel.frhetorix.model.TagWord;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class AnalysisController {
    private CompletableFuture<ArrayList<Text>> tagCloudFuture;
    private final Gson gson = new Gson();
    private final Path saveStatePath = Paths.get(System.getProperty("user.home"), "Documents", "frhetorix");

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
        String[] cleanedWordList = inputTextArea.getText().replaceAll("[\\t\\n\\r]+", " ").replaceAll("\\p{Punct}", "").split(" ");

        HashMap<String, Integer> wordsToTagCloud = getWordFrequency(cleanedWordList);

        //Asynchronously generating text cloud
        tagCloudFuture = CompletableFuture.supplyAsync(new TagCloudTask(wordsToTagCloud))
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Analyzing failed with Exception: ");
                        throwable.printStackTrace();
                    } else if (result == null) {
                        System.out.println("Nothing to analyze");
                    } else {
                        addTextElementsToTagCloud(result);
                        this.saveTagCloudResult(wordsToTagCloud);
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
            cancelClearButton.setText("Clear");
            analyzeButton.setText("Analyze");
            analyzeButton.setDisable(false);
        }
    }

    public HashMap<String, Integer> getWordFrequency(String[] words) {
        HashMap<String, Integer> countingMap = new HashMap<>();

        for (String word : words) {
            countingMap.compute(word, (k, v) -> v == null ? 1 : v + 1);
        }

        return countingMap;
    }

    private void saveTagCloudResult(HashMap<String, Integer> wordsWithFrequency) {
        try {
            ArrayList<TagWord> tagWords = new ArrayList<>();

            for (Map.Entry<String, Integer> wordFrequencyPair : wordsWithFrequency.entrySet()) {
                tagWords.add(new TagWord(wordFrequencyPair));
            }

            File saveFilePath = this.saveStatePath.toFile();
            if (!saveFilePath.exists())
                saveFilePath.mkdirs();

            Writer writer = new FileWriter(this.saveStatePath.resolve("lastSessionResult.json").toString());
            gson.toJson(tagWords, writer);
            writer.close();
        } catch (IOException fileWriterException) {
            System.err.println("File could not be saved");
            fileWriterException.printStackTrace();
        }
    }
}
