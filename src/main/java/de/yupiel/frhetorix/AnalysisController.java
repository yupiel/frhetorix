package de.yupiel.frhetorix;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class AnalysisController {
    @FXML
    private TextArea inputTextArea;
    @FXML
    private Button analyzeButton;
    @FXML
    private Button cancelClearButton;

    @FXML
    private void analyzeInput(MouseEvent event){
        System.out.println("Button Pressed");
    }
}
