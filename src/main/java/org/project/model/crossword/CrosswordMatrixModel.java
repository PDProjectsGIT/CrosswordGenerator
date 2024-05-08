package org.project.model.crossword;

import org.jetbrains.annotations.NotNull;
import org.project.model.crossword.structures.DynamicMatrix;

import java.util.*;
import java.util.stream.Stream;

final class CrosswordMatrixModel extends DynamicMatrix<CrosswordLetterModel> implements Crossword {

    final private HashMap<String, String> wordsWithMeanings;

    private double generationTime;

    private String wordClue;

    private String wordClueDefinition;

    CrosswordMatrixModel(){
        super();
        generationTime = 0;
        wordsWithMeanings = new HashMap<>();
    }

    CrosswordMatrixModel(CrosswordMatrixModel crosswordMatrixModel){
        super(crosswordMatrixModel);
        this.generationTime = crosswordMatrixModel.generationTime;
        this.wordsWithMeanings = crosswordMatrixModel.wordsWithMeanings;
        this.wordClue = crosswordMatrixModel.wordClue;
        this.wordClueDefinition = crosswordMatrixModel.wordClueDefinition;
    }

    @Override
    public List<String> getDescriptions(){
        return wordsWithMeanings.values()
                .stream()
                .toList();
    }

    @Override
    public @NotNull Stream<CrosswordLetter> streamLetters(){
        return stream().map(letter -> letter);
    }

    @Override
    public Optional<CrosswordLetter> getCrosswordLetter(int index){
        Optional<CrosswordLetterModel> optionalCrosswordLetterModel = getValue(index);
        return optionalCrosswordLetterModel.map(letter -> letter);
    }

    @Override
    public Optional<CrosswordLetter> getCrosswordLetter(int rowIndex, int columnIndex){
        Optional<CrosswordLetterModel> optionalCrosswordLetterModel = getValueIfInBounds(rowIndex, columnIndex);
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
        for(int i = 0; i < getSize(); i++){
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
            if(isLastIndexInRow(i))
                System.out.println();
        }
    }

    DynamicMatrix<CrosswordLetterModel> getCrosswordData(){
        return this;
    }

    void addWordWithMeaning(String word, String meaning){
        wordsWithMeanings.put(word, meaning);
    }

    void setGenerationTime(double time){
        generationTime = time;
    }

    void setCrosswordClueWord(String word){
        wordClue = word;
    }

    void setCrosswordClueDefinition(String definition){
        wordClueDefinition = definition;
    }
}
