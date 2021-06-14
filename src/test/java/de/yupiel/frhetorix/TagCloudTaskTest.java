package de.yupiel.frhetorix;

import org.junit.jupiter.api.Test;

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
}