package de.yupiel.frhetorix;

import de.yupiel.frhetorix.model.TagWord;

import java.util.*;
import java.util.function.Supplier;

public class TagCloudTask implements Supplier<List<TagWord>> {
    private Map<String, Integer> words = new HashMap<>();

    public TagCloudTask() {
    }
    public TagCloudTask(String words) {
        this.words = getWordFrequency(cleanupInput(words));
    }

    public void setWords(String words) {
        this.words = getWordFrequency(cleanupInput(words));
    }

    @Override
    public List<TagWord> get() {
        try {
            Map<String, Integer> wordsWithFrequency = this.words;
            List<TagWord> tagCloudTextElements = new ArrayList<>();

            if (wordsWithFrequency.entrySet().isEmpty())
                throw new NullPointerException("Input was empty after cleanup");    //return empty tag cloud if nothing was passed

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

    public Map<String, Integer> getWordFrequency(List<String> words) {
        Map<String, Integer> countingMap = new HashMap<>();

        for (String word : words) {
            countingMap.compute(word, (k, v) -> v == null ? 1 : v + 1);
        }

        return countingMap;
    }

    public List<String> cleanupInput(String input){
        String removedNewlines = input.replaceAll("[\\t\\n\\r]+", " ");
        String removedPunctuation = removedNewlines.replaceAll("\\p{Punct}", "");
        //TODO add lemmatizer or similar

        List<String> wordCleanup = Arrays.stream(removedPunctuation.split(" ")).toList();
        List<String> cleanedUpInput = new ArrayList<String>();

        for(String word : wordCleanup){
            word = word.toLowerCase();
            word = word.trim();

            if(word.equals(""))
                continue;

            cleanedUpInput.add(word);
        }

        return cleanedUpInput;
    }
}

