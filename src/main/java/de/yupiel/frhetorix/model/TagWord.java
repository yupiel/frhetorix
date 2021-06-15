package de.yupiel.frhetorix.model;

import java.util.Map;

public class TagWord {
    public String word;
    public int frequency;
    public double fontSize;

    public TagWord(String word, int frequency, double fontSize){
        this.word = word;
        this.frequency = frequency;
        this.fontSize = fontSize;
    }

    public TagWord(Map.Entry<String, Integer> wordFrequencyPair, double fontSize){
        this.word = wordFrequencyPair.getKey();
        this.frequency = wordFrequencyPair.getValue();
        this.fontSize = fontSize;
    }

    private TagWord() {}
}