package org.project.model.crossword;

import org.jetbrains.annotations.NotNull;
import org.project.model.crossword.structures.DynamicMatrix;

import java.util.*;
import java.util.stream.Stream;

final class CrosswordModel implements Crossword {

    final private HashMap<String, String> wordsWithMeanings;

    private double generationTime;

    private String wordClue;

    private String wordClueDefinition;

    private DynamicMatrix<CrosswordLetterModel> crossword;

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
    public @NotNull Stream<CrosswordLetter> stream(){
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

    DynamicMatrix<CrosswordLetterModel> getCrosswordData(){
        return crossword;
    }

    @NotNull
    Stream<CrosswordLetterModel> streamCrosswordData(){
        Stream.Builder<CrosswordLetterModel> builder = Stream.builder();
        crossword.forEach(builder::add);
        return builder.build();
    }


    void addWordWithMeaning(String word, String meaning){
        wordsWithMeanings.put(word, meaning);
    }

    void setGenerationTime(double time){
        generationTime = time;
    }

    void setCrosswordData(DynamicMatrix<CrosswordLetterModel> crossword){
        this.crossword = crossword;
    }

    void setCrosswordClueWord(String word){
        wordClue = word;
    }

    void setCrosswordClueDefinition(String definition){
        wordClueDefinition = definition;
    }
}
