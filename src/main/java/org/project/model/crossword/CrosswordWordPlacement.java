package org.project.model.crossword;

import org.project.model.crossword.structures.DynamicMatrix;

import java.util.Optional;

final class CrosswordWordPlacement {
    enum Direction {
        HORIZONTAL,
        VERTICAL
    }
    final private Direction direction;
    final private int rowIndex;
    final private int columnIndex;
    final private String word;
    final private int wordNumber;

    CrosswordWordPlacement(int rowIndex, int columnIndex, Direction direction, String word, int wordNumber){
        this.direction = direction;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.word = word;
        this.wordNumber = wordNumber;
    }

    void placeWord(DynamicMatrix<CrosswordLetterModel> crossword){
        int tempRowIndex = rowIndex;
        int tempColumnIndex = columnIndex;
        CrosswordLetterModel firstLetter = new CrosswordLetterModel(word.charAt(0));
        firstLetter.setFirstLetter(wordNumber);
        if(direction == Direction.HORIZONTAL){
            if(columnIndex < 0){
                crossword.setValueOutOfBounds(tempRowIndex, tempColumnIndex, firstLetter);
                tempColumnIndex = 1;
            }else{
                crossword.setValueOutOfBounds(tempRowIndex, tempColumnIndex++, firstLetter);
            }
            for(int i = 1; i < word.length(); i++){
                Optional<CrosswordLetterModel> optionalLetter = crossword.getValueIfInBounds(tempRowIndex, tempColumnIndex);
                if(optionalLetter.isPresent() && optionalLetter.get().isFirstLetter()){
                    tempColumnIndex++;
                }else{
                    crossword.setValueOutOfBounds(tempRowIndex, tempColumnIndex++, new CrosswordLetterModel(word.charAt(i)));
                }

            }
        }else if (direction == Direction.VERTICAL){
            if(rowIndex < 0){
                crossword.setValueOutOfBounds(tempRowIndex, tempColumnIndex, firstLetter);
                tempRowIndex = 1;
            }else{
                crossword.setValueOutOfBounds(tempRowIndex++, tempColumnIndex, firstLetter);
            }
            for(int i = 1; i < word.length(); i++){
                Optional<CrosswordLetterModel> optionalLetter = crossword.getValueIfInBounds(tempRowIndex, tempColumnIndex);
                if(optionalLetter.isPresent() && optionalLetter.get().isFirstLetter()){
                    tempRowIndex++;
                }else{
                    crossword.setValueOutOfBounds(tempRowIndex++, tempColumnIndex, new CrosswordLetterModel(word.charAt(i)));
                }

            }
        }
    }
}
