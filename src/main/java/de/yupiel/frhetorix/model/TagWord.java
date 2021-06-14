package de.yupiel.frhetorix.model;

import java.util.Map;

public class TagWord {
    public String word;
    public int frequency;

    public TagWord(String word, int frequency){
        this.word = word;
        this.frequency = frequency;
    }

    public TagWord(Map.Entry<String, Integer> wordFrequencyPair){
        this.word = wordFrequencyPair.getKey();
        this.frequency = wordFrequencyPair.getValue();
    }

    private TagWord() {}
}