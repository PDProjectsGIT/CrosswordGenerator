package org.project.model.crossword;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface Crossword {
    List<String> getDescriptions();
    Optional<CrosswordLetter> getCrosswordLetter(int index);
    Optional<CrosswordLetter> getCrosswordLetter(int rowIndex, int columnIndex);
    Optional<String> getCrosswordClueWord();
    Optional<String> getCrosswordClueDefinition();
    int getGuessedLettersCount();
    int getLettersCount();
    int getRemainingLettersCount();
    int getSize();
    int getNumberOfColumns();
    int getNumberOfRows();
    void printCrosswordInConsole();
    Stream<CrosswordLetter> streamLetters();
    double getTimeInMilliseconds();
    int getWordsCount();
}
