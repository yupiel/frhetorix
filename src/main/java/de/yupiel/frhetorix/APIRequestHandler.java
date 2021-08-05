package de.yupiel.frhetorix;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class APIRequestHandler {
    private final String words;
    private LocalDate releaseDateFrom;
    private final LocalDate releaseDateTo;
    private final String language;
    private final String market;

    private final String BASE_URL = "https://kpforcqot3.execute-api.eu-central-1.amazonaws.com/dev/search";

    public APIRequestHandler(String words, LocalDate releaseDateFrom, LocalDate releaseDateTo, String language, String market){
        this.words = cleanupWordInput(words);
        this.releaseDateFrom = releaseDateFrom;
        this.releaseDateTo = releaseDateTo;
        this.language = getLanguageShorthand(language);
        this.market = getLanguageShorthand(market);
    }

    public CompletableFuture<HttpResponse<String>> sendRequest() {
        HttpClient client = HttpClient.newHttpClient();

        return client.sendAsync(createRequest(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest createRequest(){
        RequestURIBuilder builder = new RequestURIBuilder(this.BASE_URL);

        try {
            if (words != null && !words.equals("")) {
                builder.addParameter("words", words);
            }
            if(releaseDateFrom != null && releaseDateTo != null){
                List<Integer> months = new ArrayList<>();
                List<Integer> years = new ArrayList<>();

                while(releaseDateFrom.isBefore(releaseDateTo) || releaseDateFrom.getMonthValue() == releaseDateTo.getMonthValue()){
                    if(!months.contains(releaseDateFrom.getMonthValue()))
                        months.add(releaseDateFrom.getMonthValue());
                    if(!years.contains(releaseDateFrom.getYear()))
                        years.add(releaseDateFrom.getYear());

                    releaseDateFrom = releaseDateFrom.plus(1, ChronoUnit.MONTHS);
                }

                builder.addParameter("months", months.toArray());
                builder.addParameter("years", years.toArray());
            }
            if(!language.equals("unknown") && !language.equals("Any")){
                builder.addParameter("languages", language);
            }
            if(!market.equals("unknown") && !market.equals("Any")){
                builder.addParameter("markets", market);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(builder.toURI().toString());

        return HttpRequest.newBuilder()
                .uri(builder.toURI())
                .GET()
                .build();
    }

    private String cleanupWordInput(String words){
        String removedNewlines = words.replaceAll("[\\t\\n\\r]+", "");
        String removedWhitespace = removedNewlines.replaceAll("\\s+","");

        return removedWhitespace.toLowerCase();
    }

    private String getLanguageShorthand(String language){
        if(language == null)
            return "unknown";
        else if(language.equals("German") || language.equals("Germany"))
            return "de";
        else if (language.equals("English"))
            return "en";
        else
            return "Any";
    }
}
