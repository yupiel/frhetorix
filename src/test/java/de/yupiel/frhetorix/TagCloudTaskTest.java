package de.yupiel.frhetorix;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TagCloudTaskTest {

    private final TagCloudTask testTagCloudTask = new TagCloudTask();

    @Test
    public void get_takesStringArrayOfWords_and_shouldReturnATextElementForEachUniqueWord() {
        String[] testData = new String[]{"this", "is", "a", "test", "test"};

        testTagCloudTask.setWords(testData);

        var actual = testTagCloudTask.get();

        assertEquals(4, actual.size());
    }
    @Test
    public void get_takesEmptyStringArray_and_shouldPrintErrorAndReturnNull(){
        String[] testData = new String[0];

        testTagCloudTask.setWords(testData);

        var actual = testTagCloudTask.get();

        assertNull(actual);
    }

    @Test
    public void rescalingNormalization_takesValidInput_and_shouldReturnAnIntegerInRange(){
        int frequency = 1;
        double minWeight = 1;
        double maxWeight = 20;

        double actual = testTagCloudTask.rescalingNormalization(frequency, minWeight, maxWeight);

        assertEquals(10, actual);
    }
    @Test
    public void rescalingNormalization_takesFrequencyOverMaxWeight_and_shouldReturnIntegerAboveMaxSize() {
        int frequency = 30;
        double minWeight = 1;
        double maxWeight = 20;

        double actual = testTagCloudTask.rescalingNormalization(frequency, minWeight, maxWeight);

        assertTrue(actual > 32);
    }
    @Test
    public void rescalingNormalization_takesFrequencyUnderMinWeight_and_shouldReturnIntegerBelowMinSize() {
        int frequency = 5;
        double minWeight = 10;
        double maxWeight = 20;

        double actual = testTagCloudTask.rescalingNormalization(frequency, minWeight, maxWeight);

        assertTrue(actual < 10);
    }
    @Test
    public void rescalingNormalization_takesAllZeroes_and_shouldReturnIntegerBelowMinSize() {
        int frequency = 0;
        double minWeight = 0;
        double maxWeight = 0;

        double actual = testTagCloudTask.rescalingNormalization(frequency, minWeight, maxWeight);

        assertTrue(Double.isNaN(actual));
    }


    @Test
    public void getWordFrequency_takesStringArrayOfWords_and_shouldReturnCorrectAmountInFrequencyVariable() {
        String[] testData = new String[]{"this", "is", "a", "test", "test"};

        HashMap<String, Integer> actual = testTagCloudTask.getWordFrequency(testData);

        assertEquals(2, actual.get("test"));
    }
    @Test
    public void getWordFrequency_takesEmptyStringArray_and_shouldReturnEmptyHashmap() {
        String[] testData = new String[0];

        HashMap<String, Integer> actual = testTagCloudTask.getWordFrequency(testData);

        assertTrue(actual.isEmpty());
    }
}