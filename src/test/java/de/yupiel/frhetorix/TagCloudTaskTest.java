package de.yupiel.frhetorix;

import de.yupiel.frhetorix.model.TagWord;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TagCloudTaskTest {

    private final TagCloudTask testTagCloudTask = new TagCloudTask();

    private final String validTestData = "this .is. a test,.. \n .. . \ntest";
    private final String invalidTestData = "..//::\n;:../";
    private final String emptyTestData = "\n";

    @Test
    public void get_takesStringOfWords_and_shouldReturnATextElementForEachUniqueWord() {
        testTagCloudTask.setWords(validTestData);

        List<TagWord> actual = testTagCloudTask.get();

        assertEquals(4, actual.size());
    }
    @Test
    public void get_takesStringWithoutWords_and_shouldPrintErrorAndReturnNull(){
        testTagCloudTask.setWords(invalidTestData);

        List<TagWord> actual = testTagCloudTask.get();

        assertNull(actual);
    }
    @Test
    public void get_takesEmptyString_and_shouldPrintErrorAndReturnNull(){
        testTagCloudTask.setWords(emptyTestData);

        List<TagWord> actual = testTagCloudTask.get();

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
    public void getWordFrequency_takesListOfWords_and_shouldReturnCorrectAmountInFrequencyVariable() {
        List<String> validTestData = new ArrayList<>();
        validTestData.add("test");
        validTestData.add("test");
        validTestData.add("moreTests");
        validTestData.add("moreTests");
        validTestData.add("moreTests");

        Map<String, Integer> actual = testTagCloudTask.getWordFrequency(validTestData);

        assertEquals(2, actual.get("test"));
        assertEquals(3, actual.get("moreTests"));
    }
    @Test
    public void getWordFrequency_takesEmptyList_and_shouldReturnEmptyMap() {
        List<String> invalidTestData = new ArrayList<>();

        Map<String, Integer> actual = testTagCloudTask.getWordFrequency(invalidTestData);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void cleanupInput_takesStringOfWords_and_shouldReturnListOfWords() {
        List<String> actual = testTagCloudTask.cleanupInput(validTestData);

        assertEquals(5, actual.size());
    }
    @Test
    public void cleanupInput_takesStringWithoutWords_and_shouldReturnEmptyList() {
        List<String> actual = testTagCloudTask.cleanupInput(invalidTestData);

        assertEquals(0, actual.size());
    }
    @Test
    public void cleanupInput_takesEmptyString_and_shouldReturnEmptyList() {
        List<String> actual = testTagCloudTask.cleanupInput(emptyTestData);

        assertEquals(0, actual.size());
    }
}