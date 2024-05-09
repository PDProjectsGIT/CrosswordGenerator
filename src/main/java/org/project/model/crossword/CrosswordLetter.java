package org.project.model.crossword;

import java.util.Optional;

/**
 * Represents a single letter in a crossword puzzle.
 * @author Pawe&#x142; Drzazga
 * @version 1.0
 */
public interface CrosswordLetter {

    /**
     * Retrieves the character representing the letter.
     * @return The character representing the letter.
     */
    char getLetter();

    /**
     * Retrieves the number of the word to which this letter belongs.
     * @return An Optional containing the word number if present, otherwise empty.
     */
    Optional<Integer> getWordNumber();

    /**
     * Checks if the letter has been guessed.
     * @return True if the letter has been guessed, otherwise false.
     */
    boolean isGuessed();

    /**
     * Attempts to guess the letter.
     * @param letter The letter to guess.
     * @return True if the guess was correct, otherwise false.
     */
    boolean guessLetter(char letter);

    /**
     * Checks if the letter is the first letter of a word.
     * @return True if the letter is the first letter of a word, otherwise false.
     */
    boolean isFirstLetter();

    /**
     * Checks if the letter is a clue letter (part of a clue word).
     * @return True if the letter is a clue letter, otherwise false.
     */
    boolean isClueLetter();
}
