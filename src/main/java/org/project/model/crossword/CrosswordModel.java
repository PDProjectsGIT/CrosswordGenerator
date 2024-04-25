package org.project.model.crossword;

import org.jetbrains.annotations.NotNull;
import org.project.model.crossword.structures.DynamicMatrix;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

final class CrosswordModel implements Crossword {

    final private List<String> meanings;

    final private Stopwatch stopwatch;

    private String wordClue;

    private String wordClueDefinition;

    private DynamicMatrix<CrosswordLetterModel> crossword;

    private int wordsCount;

    private static class Stopwatch{

        private long totalTime = 0;

        private long temporalTime = 0;

        private void start(){
            temporalTime = System.nanoTime();
        }

        private void stop(){
            long endTime = System.nanoTime();
            totalTime += endTime - temporalTime;
            temporalTime = endTime;
        }

        private double elapsedMilliseconds(){
            return totalTime / (double) 1_000_000;
        }

    }

    CrosswordModel(){
        stopwatch = new Stopwatch();
        meanings = new ArrayList<>();
        crossword = new DynamicMatrix<>();
        wordsCount = 0;
    }

    @Override
    public int getNumberOfRows(){
        return crossword.getNumberOfRows();
    }

    @Override
    public int getNumberOfColumns(){
        return crossword.getNumberOfColumns();
    }

    @Override
    public int getSize() {
        return crossword.getSize();
    }

    @Override
    public List<String> getDescriptions(){
        return meanings;
    }

    @Override
    public @NotNull Stream<CrosswordLetter> stream(){
        Stream.Builder<CrosswordLetter> builder = Stream.builder();
        crossword.forEach(builder::add);
        return builder.build().sequential();
    }

    @Override
    public Optional<CrosswordLetter> getCrosswordLetter(int index){
        Optional<CrosswordLetterModel> optionalCrosswordLetterModel = crossword.getValue(index);
        return optionalCrosswordLetterModel.map(letter -> letter);
    }

    @Override
    public Optional<CrosswordLetter> getCrosswordLetter(int rowIndex, int columnIndex){
        Optional<CrosswordLetterModel> optionalCrosswordLetterModel = crossword.getValueIfInBounds(rowIndex, columnIndex);
        return optionalCrosswordLetterModel.map(letter -> letter);
    }

    @Override
    public int getGuessedLettersCount(){
        return (int)this.stream()
                        .filter(Objects::nonNull)
                        .filter(CrosswordLetter::isGuessed)
                        .count();
    }

    @Override
    public int getLettersCount(){
        return (int)this.stream()
                .filter(Objects::nonNull)
                .count();
    }

    @Override
    public int getRemainingLettersCount(){
        return getLettersCount() - getGuessedLettersCount();
    }

    @Override
    public double getTimeInMilliseconds(){
        return stopwatch.elapsedMilliseconds();
    }

    @Override
    public int getWordsCount(){
        return wordsCount;
    }


    @Override
    public Optional<String> getCrosswordClueWord() {
        return Optional.ofNullable(wordClue);
    }

    @Override
    public Optional<String> getCrosswordClueDefinition() {
        return Optional.ofNullable(wordClueDefinition);
    }

    @Override
    public void printCrosswordInConsole(){
        for(int i = 0; i < crossword.getSize(); i++){
            Optional<CrosswordLetter> optionalLetter = getCrosswordLetter(i);
            if(optionalLetter.isPresent()){
                CrosswordLetter temp = optionalLetter.get();
                if(temp.isFirstLetter()){
                    System.out.print("{"+temp.getLetter() +"}");
                }else if (temp.isClueLetter()){
                    System.out.print("(" + temp.getLetter() + ")");
                }else{
                    System.out.print("[" + temp.getLetter() + "]");
                }
            }else{
                System.out.print("   ");
            }
            if(crossword.isLastIndexInRow(i))
                System.out.println();
        }
    }

    boolean tryToInsertClueWord(String word, String definition) {

        stopwatch.start();

        if(word == null || definition == null || word.isEmpty()){
            stopwatch.stop();
            throw new CrosswordException("Provided word value is null or empty");
        }

        // exclude non-matching words
        if(word.length() > (getLettersCount() - wordsCount)){
            stopwatch.stop();
            return false;
        }

        final String wordUpperCase = word.toUpperCase();

        // create map with each letter occurrence
        Map<Character, Integer> clueWordLetterMap = new HashMap<>();
        for(char letter : wordUpperCase.toCharArray()){
            clueWordLetterMap.put(letter, clueWordLetterMap.getOrDefault(letter, 0) + 1);
        }

        // I will modify CrosswordLetterModel objects, so we need to use DynamicMatrix stream method
        ArrayList<CrosswordLetterModel> possibleClueCrosswordLetters = new ArrayList<>(
                crossword.stream()
                        .filter(letter -> (letter != null && !letter.isFirstLetter() && wordUpperCase.indexOf(letter.getLetter()) != -1))
                        .toList()

        );

        // shuffle to get random positions of clue letters
        Collections.shuffle(possibleClueCrosswordLetters);

        // list with matching objects
        List<CrosswordLetterModel> matchingClueCrosswordLetters = new ArrayList<>() ;

        // count letters occurrence and fill list of matching CrosswordLetters
        possibleClueCrosswordLetters.forEach(crosswordLetterModel ->
            clueWordLetterMap.compute(crosswordLetterModel.getLetter(), (_, currentCount) -> {
                if(currentCount == null || currentCount <= 0){
                    return 0;
                }else{
                    matchingClueCrosswordLetters.add(crosswordLetterModel);
                    return currentCount - 1;
                }
            })
        );

        // check if is possible to insert clue word
        if(matchingClueCrosswordLetters.size() == wordUpperCase.length()){

            // erase previous clue number letters
            if(Optional.ofNullable(wordClue).isPresent()){
                possibleClueCrosswordLetters.stream()
                        .filter(CrosswordLetterModel::isClueLetter)
                        .forEach(CrosswordLetterModel::clearFirstOrClueLetterSetting);
            }

            // set letters as clue letters with number of
            matchingClueCrosswordLetters.forEach(crosswordLetterModel -> {
                char letter = crosswordLetterModel.getLetter();
                crosswordLetterModel.setClueLetter(wordUpperCase.indexOf(letter) + 1);
            });

            wordClue = word;

            wordClueDefinition = definition;

            stopwatch.stop();

            return true;
        }else{

            stopwatch.stop();

            return false;
        }
    }

    boolean insertWord(String word, String meaning){

        stopwatch.start();

        if(word == null || word.isEmpty() || meaning == null || meaning.isEmpty()) {
            stopwatch.stop();
            throw new CrosswordException("Provided value is null or empty");
        }
        final String wordUpperCase = word.toUpperCase();
        wordsCount++;

        // Default insertion. First word is always HORIZONTAL
        if(wordsCount == 1){
            CrosswordWordPlacement cWP = new CrosswordWordPlacement(0, 0,
                    CrosswordWordPlacement.Direction.HORIZONTAL, wordUpperCase, wordsCount);
            cWP.placeWord(crossword);
        }else{

            List<CrosswordWordPlacement> placements = new ArrayList<>();

            // Main loop of the algorithm.
            IntStream.range(0, wordUpperCase.length()).forEach(letterIndex -> {

                AtomicInteger crosswordIndex = new AtomicInteger(0);

                char currentLetter = wordUpperCase.charAt(letterIndex);

                this.stream()
                    .map(Optional::ofNullable)
                    .forEachOrdered(tempLetterOptional ->{
                        if(tempLetterOptional.isPresent()){
                            CrosswordLetter tempLetter = tempLetterOptional.get();
                            if((!tempLetter.isFirstLetter() || letterIndex != 0) && tempLetter.getLetter() == currentLetter){
                                Optional<CrosswordWordPlacement> optionalCWP = getPlacement(crosswordIndex.get(), letterIndex, wordUpperCase);
                                optionalCWP.ifPresent(placements::add);
                            }
                        }
                        crosswordIndex.incrementAndGet();
                    });
            });

            if(placements.isEmpty()){
                wordsCount--;
                stopwatch.stop();
                return false;
            }else{
                setBestCrossword(placements);
            }
        }
        meanings.add(meaning);
        stopwatch.stop();
        return true;
    }

    private Optional<CrosswordWordPlacement> getPlacement(int crosswordIndex, int letterIndex, String word){
        int rowIndex = crossword.calculateRowIndex(crosswordIndex);
        int columnsIndex = crossword.calculateColumnIndex(crosswordIndex);
        Optional<CrosswordWordPlacement> optionalCWP = getVerticalPlacement(rowIndex, columnsIndex, letterIndex, word);
        if(optionalCWP.isEmpty()){
            optionalCWP = getHorizontalPlacement(rowIndex,columnsIndex,letterIndex,word);
        }
        return optionalCWP;
    }

    private Optional<CrosswordWordPlacement> getVerticalPlacement(int rowIndex, int columnIndex, int letterIndex, String word){
        final int numberOfRows = crossword.getNumberOfRows();
        final int numberOfColumns = crossword.getNumberOfColumns();

        int startRowIndex = rowIndex - letterIndex;

        Optional<CrosswordWordPlacement> optionalCWP = Optional.of( new CrosswordWordPlacement(
                startRowIndex,
                columnIndex,
                CrosswordWordPlacement.Direction.VERTICAL,
                word,
                wordsCount
        ));

        if(startRowIndex < 0){
            letterIndex = Math.abs(startRowIndex);
            startRowIndex = 0;
        }else{
            letterIndex = 0;
        }

        // check one behind
        if(startRowIndex - 1 > 0 && getCrosswordLetter(startRowIndex - 1, columnIndex).isPresent()){
            return Optional.empty();
        }

        // check top, bottom, letter
        for(; letterIndex < word.length(); letterIndex++){

            Optional<CrosswordLetter> optionalLetter = getCrosswordLetter(startRowIndex, columnIndex);

            if(optionalLetter.isEmpty()){
                if((columnIndex > 0 && getCrosswordLetter(startRowIndex, columnIndex - 1).isPresent())
                        || (columnIndex < numberOfColumns - 1 && getCrosswordLetter(startRowIndex, columnIndex + 1).isPresent())){
                    return Optional.empty();
                }
                if((columnIndex == 0 && getCrosswordLetter(startRowIndex, columnIndex + 1).isPresent())
                        || (columnIndex == numberOfColumns - 1 && getCrosswordLetter(startRowIndex, columnIndex - 1).isPresent())){
                    return Optional.empty();
                }
            }else if((optionalLetter.get().getLetter() != word.charAt(letterIndex) || (optionalLetter.get().isFirstLetter() && letterIndex == 0))){
                return Optional.empty();
            }

            if(startRowIndex >= numberOfRows){
                return optionalCWP;
            }
            startRowIndex++;

        }
        // check one forward
        if(startRowIndex >= numberOfRows || getCrosswordLetter(startRowIndex, columnIndex).isEmpty()){
            return optionalCWP;
        }else{
            return Optional.empty();
        }
    }

    private Optional<CrosswordWordPlacement> getHorizontalPlacement(int rowIndex, int columnIndex, int letterIndex, String word){
        final int numberOfRows = crossword.getNumberOfRows();
        final int numberOfColumns = crossword.getNumberOfColumns();

        int startColumnIndex = columnIndex - letterIndex;

        Optional<CrosswordWordPlacement> optionalCWP = Optional.of( new CrosswordWordPlacement(
                rowIndex,
                startColumnIndex,
                CrosswordWordPlacement.Direction.HORIZONTAL,
                word,
                wordsCount
        ));

        if(startColumnIndex < 0){
            letterIndex = Math.abs(startColumnIndex);
            startColumnIndex = 0;
        }else{
            letterIndex = 0;
        }
        // check one behind
        if(startColumnIndex - 1 > 0 && getCrosswordLetter(rowIndex, startColumnIndex - 1).isPresent()){
            return Optional.empty();
        }

        // check top, bottom, letter

        for(; letterIndex < word.length(); letterIndex++){

            Optional<CrosswordLetter> optionalLetter = getCrosswordLetter(rowIndex, startColumnIndex);

            if(optionalLetter.isEmpty()){
                if((rowIndex > 0 && getCrosswordLetter(rowIndex - 1, startColumnIndex).isPresent())
                        || (rowIndex < numberOfRows - 1 && getCrosswordLetter(rowIndex + 1, startColumnIndex).isPresent())){
                    return Optional.empty();
                }
                if((rowIndex == 0 && getCrosswordLetter(rowIndex + 1, startColumnIndex).isPresent())
                        || (rowIndex == numberOfRows - 1 && getCrosswordLetter(rowIndex - 1, startColumnIndex).isPresent())){
                    return Optional.empty();
                }
            }else if((optionalLetter.get().getLetter() != word.charAt(letterIndex) || (optionalLetter.get().isFirstLetter() && letterIndex == 0))){
                return Optional.empty();
            }

            if(startColumnIndex >= numberOfColumns){
                return optionalCWP;
            }
            startColumnIndex++;
        }
        // check one forward
        if(startColumnIndex >= numberOfColumns || getCrosswordLetter(rowIndex, startColumnIndex).isEmpty()){
            return optionalCWP;
        }else{
            return Optional.empty();
        }
    }

    private void setBestCrossword(@NotNull List<CrosswordWordPlacement> placements){
        float bestScore = 0;
        DynamicMatrix<CrosswordLetterModel> bestCrossword = new DynamicMatrix<>(crossword);
        for(CrosswordWordPlacement placement : placements){
            DynamicMatrix<CrosswordLetterModel> newCrossword = new DynamicMatrix<>(crossword);
            placement.placeWord(newCrossword);
            float newScore = getCrosswordScore(newCrossword);
            if(newScore > bestScore){
                bestScore = newScore;
                bestCrossword = newCrossword;
            }
        }
        if(bestScore > 0){
            crossword = bestCrossword;
        }
    }

    private float getCrosswordScore(@NotNull DynamicMatrix<CrosswordLetterModel> crossword){
        final int crosswordRows = crossword.getNumberOfRows();
        final int crosswordColumns = crossword.getNumberOfColumns();
        if(crosswordRows == 0 || crosswordColumns == 0) return 0;
        int filled = 0;
        int empty = 0;
        float sizeRatio = crosswordColumns / (float) crosswordRows;
        if(sizeRatio > 1){
            sizeRatio = crosswordRows / (float) crosswordColumns;
        }
        for(CrosswordLetterModel letter : crossword){
            if(letter == null){
                empty++;
            }else{
                filled++;
            }
        }
        float filledRatio = filled / (float) empty;
        return (sizeRatio * 10) + (filledRatio * 20);
    }
}
