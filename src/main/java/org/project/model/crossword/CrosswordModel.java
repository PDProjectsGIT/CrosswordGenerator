package org.project.model.crossword;

import org.jetbrains.annotations.NotNull;
import org.project.model.crossword.structures.DynamicMatrix;

import java.util.*;
import java.util.stream.Stream;

/**
 * Deprecated: This class represents a model for a crossword puzzle.
 * It provides methods to interact with the crossword puzzle, such as retrieving letters and statistics.
 * It is recommended to use {@link CrosswordMatrixModel} class instead.
 * @author Pawe&#x142; Drzazga
 * @version 1.0
 */
@Deprecated
final class CrosswordModel implements Crossword {

    /**
     * A map storing words with their meanings for clues.
     */
    final private HashMap<String, String> wordsWithMeanings;

    /**
     * The time taken to generate the crossword puzzle in milliseconds.
     */
    private double generationTime;

    /**
     * The clue word for the crossword puzzle.
     */
    private String wordClue;

    /**
     * The definition of the clue word for the crossword puzzle.
     */
    private String wordClueDefinition;

    /**
     * The matrix representing the crossword puzzle grid.
     */
    private DynamicMatrix<CrosswordLetterModel> crossword;

    /**
     * Constructs a new CrosswordModel object with default values.
     */
    CrosswordModel(){
        generationTime = 0;
        wordsWithMeanings = new HashMap<>();
        crossword = new DynamicMatrix<>();
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
        return wordsWithMeanings.values()
                .stream()
                .toList();
    }

    @Override
    public @NotNull Stream<CrosswordLetter> streamLetters(){
        Stream.Builder<CrosswordLetter> builder = Stream.builder();
        crossword.forEach(builder::add);
        return builder.build();
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
        return (int)this.streamLetters()
                        .filter(Objects::nonNull)
                        .filter(CrosswordLetter::isGuessed)
                        .count();
    }

    @Override
    public int getLettersCount(){
        return (int)this.streamLetters()
                .filter(Objects::nonNull)
                .count();
    }

    @Override
    public int getRemainingLettersCount(){
        return getLettersCount() - getGuessedLettersCount();
    }

    @Override
    public double getTimeInMilliseconds(){
        return generationTime;
    }

    @Override
    public int getWordsCount(){
        return wordsWithMeanings.keySet().size();
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

    /**
     * Retrieves the data of the crossword puzzle as a dynamic matrix of CrosswordLetterModel objects.
     * @return The crossword puzzle data.
     */
    DynamicMatrix<CrosswordLetterModel> getCrosswordData(){
        return crossword;
    }

    /**
     * Retrieves a stream of CrosswordLetterModel objects representing the crossword puzzle data.
     * @return A stream of CrosswordLetterModel objects.
     */
    @NotNull
    Stream<CrosswordLetterModel> streamCrosswordData(){
        Stream.Builder<CrosswordLetterModel> builder = Stream.builder();
        crossword.forEach(builder::add);
        return builder.build();
    }

    /**
     * Adds a word with its meaning to the map of words with meanings for clues.
     * @param word The word.
     * @param meaning The meaning of the word.
     */
    void addWordWithMeaning(String word, String meaning){
        wordsWithMeanings.put(word, meaning);
    }

    /**
     * Sets the time taken to generate the crossword puzzle.
     * @param time The generation time in milliseconds.
     */
    void setGenerationTime(double time){
        generationTime = time;
    }

    /**
     * Sets the data of the crossword puzzle.
     * @param crossword The crossword puzzle data.
     */
    void setCrosswordData(DynamicMatrix<CrosswordLetterModel> crossword){
        this.crossword = crossword;
    }

    /**
     * Sets the clue word for the crossword puzzle.
     * @param word The clue word.
     */
    void setCrosswordClueWord(String word){
        wordClue = word;
    }

    /**
     * Sets the definition of the clue word for the crossword puzzle.
     * @param definition The definition of the clue word.
     */
    void setCrosswordClueDefinition(String definition){
        wordClueDefinition = definition;
    }
}
