package de.yupiel.frhetorix;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class APIRequestHandler {
    private final String words;
    private final LocalDate releaseDateFrom;
    private final boolean anyReleaseDateFrom;
    private final LocalDate releaseDateTo;
    private final boolean anyReleaseDateTo;
    private final String language;
    private final String market;

    private final String BASE_URL = "https://kpforcqot3.execute-api.eu-central-1.amazonaws.com/dev/search";

    public APIRequestHandler(String words, LocalDate releaseDateFrom, boolean anyReleaseDateFrom, LocalDate releaseDateTo, boolean anyReleaseDateTo, String language, String market){
        this.words = cleanupWordInput(words);
        this.releaseDateFrom = releaseDateFrom;
        this.anyReleaseDateFrom = anyReleaseDateFrom;
        this.releaseDateTo = releaseDateTo;
        this.anyReleaseDateTo = anyReleaseDateTo;
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
            if(releaseDateFrom != null && !anyReleaseDateFrom) {
                builder.addParameter("fromdate", String.format("%s-%s", releaseDateFrom.getYear(), formatMonth(releaseDateFrom)));
            }
            if (releaseDateTo != null && !anyReleaseDateTo){
                builder.addParameter("todate", String.format("%s-%s", releaseDateTo.getYear(), formatMonth(releaseDateTo)));
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

    private String formatMonth(LocalDate sourceDate){
        String formattedMonth = String.valueOf(sourceDate.getMonthValue());
        if(formattedMonth.length() == 1)
            return String.format("0%s", formattedMonth);

        return formattedMonth;
    }
}
