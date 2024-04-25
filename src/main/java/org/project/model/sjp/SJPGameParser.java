package org.project.model.sjp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SJPGameParser {

    final private String SEPARATOR = "[END]";

    private String word;

    private List<String> meanings;

    private boolean isForbiddenInGames;

    public SJPGameParser(){
        word = "default";
        meanings = new ArrayList<>();
        meanings.add("Use method nextWord() to get new word and meaning");
        isForbiddenInGames = false;
    }

    public boolean isForbiddenInGames() {
        return isForbiddenInGames;
    }

    public String getWord(){
        return word;
    }

    public Optional<String> getRandomMeaning() {
        if (meanings.isEmpty()) {
            return Optional.empty();
        }
        Random random = new Random();
        String meaning = meanings.get(random.nextInt(meanings.size()));
        return Optional.ofNullable(meaning);
    }

    public List<String> getMeanings(){
        return meanings;
    }

    public Map<String, String> getWordsWithMeanings(int wordCount, boolean isForbiddenInGames) throws SJPException{
        Map<String, String> wordsWithMeanings = new HashMap<>();
        while (wordCount > 0) {
            if(nextWord().isForbiddenInGames()){
                if(!isForbiddenInGames){
                    wordCount--;
                    wordsWithMeanings.put(this.word, getRandomMeaning().orElse("default"));
                }
                continue;
            }
            wordCount--;
            wordsWithMeanings.put(this.word, getRandomMeaning().orElse("default"));
        }
        return wordsWithMeanings;
    }

    public SJPGameParser nextWord() throws SJPException{
        try{
            // Using Jsoup api to parse html documents
            String sjpURL = "https://sjp.pl/sl/los/";
            Document doc = Jsoup.connect(sjpURL).get();
            String htmlContent = doc.body().text();
            isForbiddenInGames = isForbiddenInGames(htmlContent);
            meanings.clear();
            word = processWord(htmlContent);
            Optional<String> definitions = getDefinitions(htmlContent);
            definitions.ifPresent(string -> meanings = extractDefinitions(string));
        }catch (IOException e){
            throw new SJPException("An error occurred when connecting to server");
        }
        return this;
    }


    private String processWord(String text) throws SJPException{
        if(text == null || text.isEmpty())
            throw new SJPException("Provided String value is null or empty");

        Pattern pattern = Pattern.compile("\\(i\\) (\\S+)");
        Matcher matcher = pattern.matcher(text);
        String word = null;
        while(matcher.find()){
            word = matcher.group(1);
        }
        return word;
    }

    private boolean isForbiddenInGames(String input) throws SJPException{
        if(input == null || input.isEmpty())
            throw new SJPException("Provided String value is null or empty");

        // Checks if "niedopuszczalne w grach" is before the first "(i)".
        Pattern pattern = Pattern.compile("niedopuszczalne w grach(?=.*\\(i\\))");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    private String removeComments(String input) throws SJPException{
        if(input == null || input.isEmpty())
            throw new SJPException("Provided String value is null or empty");

        int index = input.indexOf("KOMENTARZE"); // Need first index
        if (index != -1) {
            return input.substring(0, index);
        } else {
            return input;
        }
    }

    private Optional<String> getDefinitions(String input) throws SJPException{
        if(input == null || input.isEmpty())
            throw new SJPException("Provided String value is null or empty");

        final String forbiddenString = "niedopuszczalne w grach (i)";
        final String allowedString = "dopuszczalne w grach (i)";
        final String relatedString = "POWIĄZANE";
        final String meaningString = "znaczenie: info (";
        final String dash = " - ";

        // cut the first occurrence of allowed
        Optional<String> result = findFirstAndCutItOut(input, allowedString);

        // if there is no allowed cut first occurrence of forbidden
        if (result.isEmpty()) {
            result = findFirstAndCutItOut(input, forbiddenString);

            // return empty if there is nothing else :<
            if (result.isEmpty()) {
                return Optional.empty();
            }
        }

        input = result.get();
        input = removeComments(input);

        StringBuilder resultInput = new StringBuilder();

        String tempString;
        int forbiddenIndex;
        int allowedIndex;
        int relatedIndex;

        // while any znaczenie: info ( is still in input
        while ((result = findFirstAndCutItOut(input, meaningString)).isPresent()) {
            tempString = result.get();

            //calc and get first occurrence of key word
            forbiddenIndex = tempString.indexOf(forbiddenString);
            allowedIndex = tempString.indexOf(allowedString);
            relatedIndex = tempString.indexOf(relatedString);
            int minIndex = minIndex(forbiddenIndex, allowedIndex, relatedIndex);

            // if there is keyword
            if (minIndex != -1) {

                String resultWithoutDash = tempString.substring(3, minIndex);

                // we are going to delete " - "
                if (forbiddenIndex == minIndex || allowedIndex == minIndex) {

                    // some magic
                    StringBuilder dashCutter = new StringBuilder(resultWithoutDash);
                    dashCutter.reverse();
                    result = findFirstAndCutItOut(dashCutter.toString(), dash);
                    if (result.isPresent()) {
                        dashCutter = new StringBuilder(result.get());
                        resultWithoutDash = dashCutter.reverse().toString();
                    }
                }

                // now we have ready output
                resultInput.append(resultWithoutDash);
            } else {

                // no keyword, no problem
                tempString = tempString.trim();
                resultInput.append(tempString, 3, tempString.length());
            }
            // set separator
            resultInput.append(SEPARATOR);
            input = tempString;
        }

        return Optional.of(resultInput.toString());
    }

    public List<String> extractDefinitions(String input){

        List<String> definitions = new ArrayList<>();

        // pattern to match text within [END] blocks
        Pattern pattern = Pattern.compile(Pattern.quote(SEPARATOR));

        // split input by [END] and process each block
        String[] blocks = pattern.split(input);
        for (String block : blocks) {
            // remove leading and trailing whitespace
            block = block.trim();
            // skip empty blocks
            if (block.isEmpty()) continue;

            // pattern to match text within [czyt.] blocks
            Pattern innerPattern = Pattern.compile("\\[([^\\[\\]]*?)]");
            Matcher innerMatcher = innerPattern.matcher(block);
            while (innerMatcher.find()) {
                block = block.replace(innerMatcher.group(), "");
            }

            // split block into individual definitions
            String[] definitionsArray = block.split("\\d+\\. ");
            for (String definition : definitionsArray) {
                // remove leading and trailing whitespace
                definition = definition.trim();
                // remove trailing semicolon if present
                if (definition.endsWith(";")) {
                    definition = definition.substring(0, definition.length() - 1);
                }
                // skip empty definitions
                if (!definition.isEmpty()) {
                    definitions.add(definition);
                }
            }
        }
        return definitions;
    }

    private Optional<String> findFirstAndCutItOut(String input, String text){
        if(text == null || text.isEmpty() || input == null || input.isEmpty()) return Optional.empty();
        int startIndex = input.indexOf(text);
        if(startIndex != -1){
            return Optional.of(input.substring(startIndex + text.length()));
        }
        return Optional.empty();
    }

    private int minIndex(int ... arguments){
        int length = arguments.length;
        if(length == 0) return -1;
        int min = -1;
        for(int index : arguments){
            if(index != -1 && min == -1){
                min = index;
            }else if(index != -1 && index < min){
                min = index;
            }
        }
        return min;
    }
}
