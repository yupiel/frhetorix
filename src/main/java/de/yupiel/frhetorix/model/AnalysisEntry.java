package de.yupiel.frhetorix.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.time.LocalDate;

public class AnalysisEntry {
    public String market;
    public LocalDate releaseDate;
    public String language;
    public String word;
    public double totalOccurrences;

    public AnalysisEntry() {}
    public AnalysisEntry(String market, LocalDate releaseDate, String language, String word, double totalOccurrences){
        this.market = market;
        this.releaseDate = releaseDate;
        this.language = language;
        this.word = word;
        this.totalOccurrences = totalOccurrences;
    }

    public static AnalysisEntry fromJson(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String jsonMarket = jsonObject.get("Market").getAsString();
        LocalDate jsonDate = LocalDate.of(jsonObject.get("TrackYear").getAsInt(), jsonObject.get("TrackMonth").getAsInt(), 1);

        String jsonLanguage = "unknown";
        try{
            jsonLanguage = jsonObject.get("DetectedLanguage").getAsString();
        } catch(UnsupportedOperationException exception){
            System.out.println("Language string was null.");
        }

        String jsonWord = jsonObject.get("Word").getAsString();
        double jsonOccurrences = jsonObject.get("TotalOccurrences").getAsDouble();

        return new AnalysisEntry(
                jsonMarket,
                jsonDate,
                jsonLanguage,
                jsonWord,
                jsonOccurrences
        );
    }
}
