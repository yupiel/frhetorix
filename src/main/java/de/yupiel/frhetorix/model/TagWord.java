package de.yupiel.frhetorix.model;

public class TagWord {
    public String word;
    public double frequency;
    public double fontSize;

    public TagWord(String word, double frequency, double fontSize){
        this.word = word;
        this.frequency = frequency;
        this.fontSize = fontSize;
    }

    private TagWord() {}
}