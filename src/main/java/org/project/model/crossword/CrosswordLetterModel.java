package org.project.model.crossword;

import java.util.Optional;

final class CrosswordLetterModel implements CrosswordLetter{
    private boolean isFirstLetter;
    private boolean isGuessed;
    private boolean isClueLetter;
    final private char letter;
    private Integer wordNumber;


    CrosswordLetterModel(char letter){
        isFirstLetter = false;
        isGuessed = false;
        isClueLetter = false;
        this.letter = Character.toUpperCase(letter);
    }

    @Override
    public char getLetter(){
        return letter;
    }

    @Override
    public Optional<Integer> getWordNumber(){
        return Optional.ofNullable(wordNumber);
    }

    @Override
    public boolean isGuessed(){
        return isGuessed;
    }

    @Override
    public boolean guessLetter(char letter){
        if(this.letter == Character.toUpperCase(letter)){
            isGuessed = true;
        }
        return isGuessed;
    }

    @Override
    public boolean isFirstLetter(){
        return isFirstLetter;
    }

    @Override
    public boolean isClueLetter() {
        return isClueLetter;
    }

    void setClueLetter(int clueWordNumber){
        if(isFirstLetter) throw new CrosswordException("Cannot assign first letter as clue letter");
        this.wordNumber = clueWordNumber;
        this.isClueLetter = true;
    }

    void setFirstLetter(int firstWordNumber) {
        if(isClueLetter) throw new CrosswordException("Cannot assign clue letter as first letter");
        this.wordNumber = firstWordNumber;
        this.isFirstLetter = true;
    }

    void clearFirstOrClueLetterSetting(){
        this.wordNumber = null;
        this.isFirstLetter = false;
        this.isClueLetter = false;
    }
}
