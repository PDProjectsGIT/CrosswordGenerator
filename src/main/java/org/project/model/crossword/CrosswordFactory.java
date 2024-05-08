package org.project.model.crossword;

import org.jetbrains.annotations.NotNull;
import org.project.model.crossword.structures.DynamicMatrix;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class CrosswordFactory {

    private CrosswordMatrixModel crosswordModel;

    final private Stopwatch stopwatch;

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

    public CrosswordFactory(){
        crosswordModel = new CrosswordMatrixModel();
        stopwatch = new Stopwatch();
    }

    public Crossword build(){
        crosswordModel.setGenerationTime(stopwatch.elapsedMilliseconds());
        return crosswordModel;
    }

    public boolean insertWord(String word, String meaning){
        //return crosswordModel.insertWord(word, meaning);
        stopwatch.start();

        if(word == null || word.isEmpty() || meaning == null || meaning.isEmpty()) {
            stopwatch.stop();
            throw new CrosswordException("Provided value is null or empty");
        }

        final String wordUpperCase = word.toUpperCase();

        // Default insertion. First word is always HORIZONTAL
        if(crosswordModel.getSize() == 0){
            CrosswordWordPlacement cWP = new CrosswordWordPlacement(0, 0,
                    CrosswordWordPlacement.Direction.HORIZONTAL, wordUpperCase, 1);
            cWP.placeWord(crosswordModel.getCrosswordData());

        }else{

            List<CrosswordWordPlacement> placements = new ArrayList<>();

            // Main loop of the algorithm.
            IntStream.range(0, wordUpperCase.length()).forEach(letterIndex -> {

                AtomicInteger crosswordIndex = new AtomicInteger(0);

                char currentLetter = wordUpperCase.charAt(letterIndex);

                crosswordModel.streamLetters()
                        //.sequential()
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
                stopwatch.stop();
                return false;
            }else{
                setBestCrossword(placements);
            }
        }
        crosswordModel.addWordWithMeaning(wordUpperCase, meaning);
        stopwatch.stop();
        return true;
    }

    public boolean tryToInsertClueWord(String word, String definition){
        stopwatch.start();

        if(word == null || word.isEmpty() || definition == null || definition.isEmpty() ){
            stopwatch.stop();
            throw new CrosswordException("Provided word value is null or empty");
        }

        // exclude non-matching words
        if(word.length() > (crosswordModel.getLettersCount() - crosswordModel.getWordsCount())){
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
                crosswordModel.stream()
                      //  .sequential()
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
            if(crosswordModel.getCrosswordClueWord().isPresent()){
                possibleClueCrosswordLetters.stream()
                        .filter(CrosswordLetterModel::isClueLetter)
                        .forEach(CrosswordLetterModel::clearFirstOrClueLetterSetting);
            }

            // set letters as clue letters with number of
            matchingClueCrosswordLetters.forEach(crosswordLetterModel -> {
                char letter = crosswordLetterModel.getLetter();
                crosswordLetterModel.setClueLetter(wordUpperCase.indexOf(letter) + 1);
            });

            crosswordModel.setCrosswordClueWord(word);
            crosswordModel.setCrosswordClueDefinition(definition);
            stopwatch.stop();
            return true;
        }else{
            stopwatch.stop();
            return false;
        }
    }

    private Optional<CrosswordWordPlacement> getPlacement(int crosswordIndex, int letterIndex, String word){
        DynamicMatrix<CrosswordLetterModel> crosswordData = crosswordModel.getCrosswordData();
        int rowIndex = crosswordData.calculateRowIndex(crosswordIndex);
        int columnsIndex = crosswordData.calculateColumnIndex(crosswordIndex);
        Optional<CrosswordWordPlacement> optionalCWP = getVerticalPlacement(crosswordData, rowIndex, columnsIndex, letterIndex, word);
        if(optionalCWP.isEmpty()){
            optionalCWP = getHorizontalPlacement(crosswordData, rowIndex, columnsIndex, letterIndex, word);
        }
        return optionalCWP;
    }

    private Optional<CrosswordWordPlacement> getVerticalPlacement(DynamicMatrix<CrosswordLetterModel> crosswordData, int rowIndex, int columnIndex, int letterIndex, String word){
        final int numberOfRows = crosswordData.getNumberOfRows();
        final int numberOfColumns = crosswordData.getNumberOfColumns();

        int startRowIndex = rowIndex - letterIndex;

        Optional<CrosswordWordPlacement> optionalCWP = Optional.of( new CrosswordWordPlacement(
                startRowIndex,
                columnIndex,
                CrosswordWordPlacement.Direction.VERTICAL,
                word,
                crosswordModel.getWordsCount() + 1
        ));

        if(startRowIndex < 0){
            letterIndex = Math.abs(startRowIndex);
            startRowIndex = 0;
        }else{
            letterIndex = 0;
        }

        // check one behind
        if(startRowIndex - 1 > 0 && crosswordModel.getCrosswordLetter(startRowIndex - 1, columnIndex).isPresent()){
            return Optional.empty();
        }

        // check top, bottom, letter
        for(; letterIndex < word.length(); letterIndex++){

            Optional<CrosswordLetter> optionalLetter = crosswordModel.getCrosswordLetter(startRowIndex, columnIndex);

            if(optionalLetter.isEmpty()){
                if((columnIndex > 0 && crosswordModel.getCrosswordLetter(startRowIndex, columnIndex - 1).isPresent())
                        || (columnIndex < numberOfColumns - 1 && crosswordModel.getCrosswordLetter(startRowIndex, columnIndex + 1).isPresent())){
                    return Optional.empty();
                }
                if((columnIndex == 0 && crosswordModel.getCrosswordLetter(startRowIndex, columnIndex + 1).isPresent())
                        || (columnIndex == numberOfColumns - 1 && crosswordModel.getCrosswordLetter(startRowIndex, columnIndex - 1).isPresent())){
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
        if(startRowIndex >= numberOfRows || crosswordModel.getCrosswordLetter(startRowIndex, columnIndex).isEmpty()){
            return optionalCWP;
        }else{
            return Optional.empty();
        }
    }

    private Optional<CrosswordWordPlacement> getHorizontalPlacement(DynamicMatrix<CrosswordLetterModel> crosswordData, int rowIndex, int columnIndex, int letterIndex, String word){
        final int numberOfRows = crosswordData.getNumberOfRows();
        final int numberOfColumns = crosswordData.getNumberOfColumns();

        int startColumnIndex = columnIndex - letterIndex;

        Optional<CrosswordWordPlacement> optionalCWP = Optional.of( new CrosswordWordPlacement(
                rowIndex,
                startColumnIndex,
                CrosswordWordPlacement.Direction.HORIZONTAL,
                word,
                crosswordModel.getWordsCount() + 1
        ));

        if(startColumnIndex < 0){
            letterIndex = Math.abs(startColumnIndex);
            startColumnIndex = 0;
        }else{
            letterIndex = 0;
        }
        // check one behind
        if(startColumnIndex - 1 > 0 && crosswordModel.getCrosswordLetter(rowIndex, startColumnIndex - 1).isPresent()){
            return Optional.empty();
        }

        // check top, bottom, letter

        for(; letterIndex < word.length(); letterIndex++){

            Optional<CrosswordLetter> optionalLetter = crosswordModel.getCrosswordLetter(rowIndex, startColumnIndex);

            if(optionalLetter.isEmpty()){
                if((rowIndex > 0 && crosswordModel.getCrosswordLetter(rowIndex - 1, startColumnIndex).isPresent())
                        || (rowIndex < numberOfRows - 1 && crosswordModel.getCrosswordLetter(rowIndex + 1, startColumnIndex).isPresent())){
                    return Optional.empty();
                }
                if((rowIndex == 0 && crosswordModel.getCrosswordLetter(rowIndex + 1, startColumnIndex).isPresent())
                        || (rowIndex == numberOfRows - 1 && crosswordModel.getCrosswordLetter(rowIndex - 1, startColumnIndex).isPresent())){
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
        if(startColumnIndex >= numberOfColumns || crosswordModel.getCrosswordLetter(rowIndex, startColumnIndex).isEmpty()){
            return optionalCWP;
        }else{
            return Optional.empty();
        }
    }

    private void setBestCrossword(@NotNull List<CrosswordWordPlacement> placements){
        float bestScore = 0;
        //DynamicMatrix<CrosswordLetterModel> crosswordData = crosswordModel.getCrosswordData();
        //DynamicMatrix<CrosswordLetterModel> bestCrossword = new DynamicMatrix<>(crosswordData);
        CrosswordMatrixModel bestCrossword = new CrosswordMatrixModel(crosswordModel);

        for(CrosswordWordPlacement placement : placements){
            //DynamicMatrix<CrosswordLetterModel> newCrossword = new DynamicMatrix<>(crosswordData);
            CrosswordMatrixModel newCrossword = new CrosswordMatrixModel(crosswordModel);
            placement.placeWord(newCrossword);
            float newScore = getCrosswordScore(newCrossword);
            if(newScore > bestScore){
                bestScore = newScore;
                bestCrossword = newCrossword;
            }
        }
        if(bestScore > 0){
            crosswordModel = bestCrossword;
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
