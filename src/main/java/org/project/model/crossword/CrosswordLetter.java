package org.project.model.crossword;

import java.util.Optional;

public interface CrosswordLetter {
    public char getLetter();
    Optional<Integer> getWordNumber();
    public boolean isGuessed();
    public boolean guessLetter(char letter);
    public boolean isFirstLetter();
    public boolean isClueLetter();
}
