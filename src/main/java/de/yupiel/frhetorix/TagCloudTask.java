package de.yupiel.frhetorix;

import de.yupiel.frhetorix.model.TagWord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TagCloudTask implements Supplier<ArrayList<TagWord>> {
    private HashMap<String, Integer> words = new HashMap<>();

    public TagCloudTask() {
    }
    public TagCloudTask(String[] words) {
        this.words = getWordFrequency(words);
    }

    public void setWords(String[] words) {
        this.words = getWordFrequency(words);
    }

    @Override
    public ArrayList<TagWord> get() {
        try {
            HashMap<String, Integer> wordsWithFrequency = this.words;
            ArrayList<TagWord> tagCloudTextElements = new ArrayList<>();

            if (wordsWithFrequency.entrySet().isEmpty())
                throw new NullPointerException("Hashmap was empty");    //return empty tag cloud if nothing was passed

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
                double calculatedFontSize = rescalingNormalization(pair.getValue(), minWeight, maxWeight);

                tagCloudTextElements.add(new TagWord(pair.getKey(), pair.getValue(), calculatedFontSize));
            }

            return tagCloudTextElements;
        } catch (NullPointerException noWordsException){
            noWordsException.printStackTrace();
            return null;
        }
    }

    public double rescalingNormalization(int wordFrequency, double minWeight, double maxWeight) {
        double minSize = 10;
        double maxSize = 32;

        return (maxSize - minSize) * ((wordFrequency - minWeight) / (maxWeight - minWeight)) + minSize;
    }

    public HashMap<String, Integer> getWordFrequency(String[] words) {
        HashMap<String, Integer> countingMap = new HashMap<>();

        for (String word : words) {
            countingMap.compute(word, (k, v) -> v == null ? 1 : v + 1);
        }

        return countingMap;
    }

}

