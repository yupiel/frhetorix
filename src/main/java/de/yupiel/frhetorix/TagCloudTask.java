package de.yupiel.frhetorix;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TagCloudTask implements Supplier<ArrayList<Text>> {
    private final HashMap<String, Integer> words;

    public TagCloudTask(HashMap<String, Integer> words) {
        this.words = words;
    }

    @Override
    public ArrayList<Text> get() {
        HashMap<String, Integer> wordsWithFrequency = this.words;
        ArrayList<Text> tagCloudTextElements = new ArrayList<>();

        if (wordsWithFrequency.entrySet().isEmpty())
            return null;    //return empty tag cloud if nothing was passed

        double minWeight = Double.POSITIVE_INFINITY;
        double maxWeight = 0;

        for (Map.Entry<String, Integer> pair : wordsWithFrequency.entrySet()) {
            double w = (double) pair.getValue();
            if (w > maxWeight)
                maxWeight = w;
            if (w < minWeight)
                minWeight = w;
        }

        for (Map.Entry<String, Integer> pair : wordsWithFrequency.entrySet()) {
            Text text = new Text(pair.getKey() + " ");
            text.setFont(setFontForWord(pair.getValue(), minWeight, maxWeight));

            tagCloudTextElements.add(text);
        }

        return tagCloudTextElements;
    }

    private Font setFontForWord(int frequency, double minWeight, double maxWeight) {
        double minSize = 10;
        double maxSize = 32;

        double size = (maxSize - minSize) * ((frequency - minWeight) / (maxWeight - minWeight)) + minSize;

        return new Font(size);
    }
}

