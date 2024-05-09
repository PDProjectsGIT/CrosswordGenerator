package org.project.model.crossword;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An interface representing a crossword puzzle.
 * Provides methods to interact with the crossword (readonly), such as retrieving letters, clues, and statistics.
 * @author Pawe&#x142; Drzazga
 * @version 1.0
 */
public interface Crossword {

    /**
     * Retrieves the list of descriptions for the crossword words.
     * @return A list of crossword clue descriptions.
     */
    List<String> getDescriptions();

    /**
     * Retrieves the crossword letter at the specified index.
     * @param index The index of the crossword letter.
     * @return An Optional containing the crossword letter at the specified index if present, otherwise empty.
     */
    Optional<CrosswordLetter> getCrosswordLetter(int index);

    /**
     * Retrieves the crossword letter at the specified row and column index.
     * @param rowIndex The row index of the crossword letter.
     * @param columnIndex The column index of the crossword letter.
     * @return An Optional containing the crossword letter at the specified indices if present, otherwise empty.
     */
    Optional<CrosswordLetter> getCrosswordLetter(int rowIndex, int columnIndex);

    /**
     * Retrieves the clue word for the crossword.
     * @return An Optional containing the clue word if present, otherwise empty.
     */
    Optional<String> getCrosswordClueWord();

    /**
     * Retrieves the clue definition for the crossword.
     * @return An Optional containing the clue definition if present, otherwise empty.
     */
    Optional<String> getCrosswordClueDefinition();

    /**
     * Retrieves the count of guessed letters in the crossword.
     * @return The count of guessed letters.
     */
    int getGuessedLettersCount();

    /**
     * Retrieves the total count of letters in the crossword.
     * @return The total count of letters.
     */
    int getLettersCount();

    /**
     * Retrieves the count of remaining letters to be guessed in the crossword.
     * @return The count of remaining letters.
     */
    int getRemainingLettersCount();

    /**
     * Retrieves the size of the crossword grid.
     * @return The size of the crossword.
     */
    int getSize();

    /**
     * Retrieves the number of columns in the crossword grid.
     * @return The number of columns.
     */
    int getNumberOfColumns();

    /**
     * Retrieves the number of rows in the crossword grid.
     * @return The number of rows.
     */
    int getNumberOfRows();

    /**
     * Prints the crossword puzzle in the console.
     */
    void printCrosswordInConsole();

    /**
     * Retrieves a stream of crossword letters in the puzzle.
     * @return A stream of crossword letters.
     */
    Stream<CrosswordLetter> streamLetters();

    /**
     * Retrieves the time taken to solve the crossword puzzle in milliseconds.
     * @return The time taken to solve the puzzle in milliseconds.
     */
    double getTimeInMilliseconds();

    /**
     * Retrieves the count of words in the crossword puzzle.
     * @return The count of words.
     */
    int getWordsCount();
}
