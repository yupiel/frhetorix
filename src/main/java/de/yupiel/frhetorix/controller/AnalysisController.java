package de.yupiel.frhetorix.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import de.yupiel.frhetorix.TagCloudTask;
import de.yupiel.frhetorix.model.TagWord;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AnalysisController {
    private CompletableFuture<List<TagWord>> tagCloudFuture;
    private final Gson gson = new Gson();
    private final Path saveStatePath = Paths.get(System.getProperty("user.home"), "Documents", "frhetorix");
    private final String saveFileName = "lastSessionResult.json";

    @FXML
    private TextFlow tagCloudTextFlow;
    @FXML
    private TextArea inputTextArea;
    @FXML
    private Button analyzeButton;
    @FXML
    private Button cancelClearButton;

    @FXML
    public void initialize(){
        try {
            File saveFile = this.saveStatePath.resolve(saveFileName).toFile();
            if (saveFile.isFile()) {
                JsonReader reader = new JsonReader(new FileReader(saveFile));
                Type readFileType = new TypeToken<List<TagWord>>() {
                }.getType();
                List<TagWord> loadedTagWords = gson.fromJson(reader, readFileType);

                if(loadedTagWords == null || loadedTagWords.size() == 0)
                    return;

                this.addTextElementsToTagCloud(loadedTagWords);
            }
        } catch (FileNotFoundException fileNotFoundException){
            fileNotFoundException.printStackTrace();
        }
    }

    @FXML
    private void analyzeButtonHandler(MouseEvent event) {
        toggleButtonStateWorking(true);
        tagCloudTextFlow.getChildren().clear();

        //Asynchronously generating text cloud
        tagCloudFuture = CompletableFuture.supplyAsync(new TagCloudTask(inputTextArea.getText()))
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        System.out.println("Analyzing failed with Exception: ");
                        throwable.printStackTrace();
                    } else if (result == null) {
                        System.out.println("Nothing to analyze");
                    } else {
                        this.addTextElementsToTagCloud(result);
                        this.saveTagCloudResult(result);
                    }
                });
    }

    @FXML
    private void cancelClearButtonHandler(MouseEvent event) {
        if (tagCloudFuture != null && !tagCloudFuture.isDone()) {
            System.out.println("Canceling Tag Cloud Generation...");
            tagCloudFuture.cancel(true);
            toggleButtonStateWorking(false);
            return;
        }

        inputTextArea.clear();
        tagCloudTextFlow.getChildren().clear();
    }

    private void addTextElementsToTagCloud(List<TagWord> words) {
        Platform.runLater(() -> {   //needed here because JavaFX won't update the Scene unless this happens on the main thread
            for (TagWord word : words) {
                Text wordTextElement = new Text(word.word + " ");
                wordTextElement.setFont(new Font(word.fontSize));

                tagCloudTextFlow.getChildren().add(wordTextElement);
            }

            toggleButtonStateWorking(false);
        });
    }

    //Button eye candy
    private void toggleButtonStateWorking(boolean working) {
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

    private void saveTagCloudResult(List<TagWord> tagWords) {
        try {
            File saveFilePath = this.saveStatePath.toFile();
            if (!saveFilePath.exists())
                saveFilePath.mkdirs();

            Writer writer = new FileWriter(this.saveStatePath.resolve(saveFileName).toString());
            gson.toJson(tagWords, writer);
            writer.close();
        } catch (IOException fileWriterException) {
            System.err.println("File could not be saved");
            fileWriterException.printStackTrace();
        }
    }
}
