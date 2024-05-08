package org.project.model.crossword;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface Crossword {
    public List<String> getDescriptions();
    public Optional<CrosswordLetter> getCrosswordLetter(int index);
    public Optional<CrosswordLetter> getCrosswordLetter(int rowIndex, int columnIndex);
    public Optional<String> getCrosswordClueWord();
    public Optional<String> getCrosswordClueDefinition();
    public int getGuessedLettersCount();
    public int getLettersCount();
    public int getRemainingLettersCount();
    public int getSize();
    public int getNumberOfColumns();
    public int getNumberOfRows();
    public void printCrosswordInConsole();
    public Stream<CrosswordLetter> streamLetters();
    public double getTimeInMilliseconds();
    public int getWordsCount();
}
