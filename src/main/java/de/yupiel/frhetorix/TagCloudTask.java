package de.yupiel.frhetorix;

import de.yupiel.frhetorix.model.AnalysisEntry;
import de.yupiel.frhetorix.model.TagWord;

import java.util.*;

public class TagCloudTask {
    public static List<TagWord> generate(List<AnalysisEntry> entries) {
        try {
            List<TagWord> tagCloudTextElements = new ArrayList<>();

            if (entries.isEmpty())
                throw new NullPointerException("Input was empty after cleanup");    //return empty tag cloud if nothing was passed

            double minWeight = Double.POSITIVE_INFINITY;
            double maxWeight = 0;

            for (AnalysisEntry entry : entries) {
                double w = entry.totalOccurrences;
                if (w > maxWeight)
                    maxWeight = w;
                if (w < minWeight)
                    minWeight = w;
            }

            for (AnalysisEntry entry : entries) {
                double calculatedFontSize = rescalingNormalization(entry.totalOccurrences, minWeight, maxWeight);

                tagCloudTextElements.add(new TagWord(entry.word, entry.totalOccurrences, calculatedFontSize));
            }

            return tagCloudTextElements;
        } catch (NullPointerException noWordsException){
            noWordsException.printStackTrace();
            return null;
        }
    }

    private static double rescalingNormalization(double wordFrequency, double minWeight, double maxWeight) {
        double minSize = 10;
        double maxSize = 32;

        return (maxSize - minSize) * ((wordFrequency - minWeight) / (maxWeight - minWeight)) + minSize;
    }
}

