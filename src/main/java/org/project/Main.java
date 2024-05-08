package org.project;

import org.project.model.crossword.Crossword;
import org.project.model.crossword.CrosswordFactory;
import org.project.model.crossword.CrosswordException;
import org.project.model.sjp.SJPException;
import org.project.model.sjp.SJPGameParser;

import java.util.List;
import java.util.stream.IntStream;

public class Main {

    public static Crossword getCrossword(int wordCounter, boolean withClue) throws CrosswordException{
        SJPGameParser sjpGameParser = new SJPGameParser();
        CrosswordFactory crosswordBuilder = new CrosswordFactory();
        String clueWord = "default";
        String clueDefinition = "default";

        try{

            while (wordCounter > 0) {
                if(!sjpGameParser.nextWord().isForbiddenInGames()
                        && crosswordBuilder.insertWord(sjpGameParser.getWord(), sjpGameParser.getRandomMeaning().orElse("default"))){
                    wordCounter--;
                }
            }

            if(withClue){
                do {
                    sjpGameParser.nextWord();
                } while (!crosswordBuilder.tryToInsertClueWord(clueWord = sjpGameParser.getWord(),
                        clueDefinition = sjpGameParser.getRandomMeaning().orElse("default") ));
            }



        }catch (SJPException e) {
            System.out.println(e.getMessage());
        }catch (CrosswordException e){
            throw new CrosswordException(e.getMessage());
        }

        return crosswordBuilder.build();
    }

    public static void testPrint(int crosswordSize){

        Crossword crossword = getCrossword(crosswordSize, true);

        System.out.println();
        System.out.println("Generation time (crossword): " + crossword.getTimeInMilliseconds());
        System.out.println("Words count: " + crossword.getWordsCount());
        System.out.println("Letters: " + crossword.getLettersCount());
        System.out.println("Size: " + crossword.getSize());
        System.out.println();
        crossword.printCrosswordInConsole();
        System.out.println();
        System.out.println("Hasło: " + crossword.getCrosswordClueWord().orElse("default") + " - " + crossword.getCrosswordClueDefinition().orElse("default"));
        System.out.println();
        List<String> descriptions = crossword.getDescriptions();
        IntStream.range(0, descriptions.size())
                .forEach(index -> System.out.println((index + 1) + ". " + descriptions.get(index)));

    }

    public static void main(String[] args) {

        testPrint(15);
        testPrint(15);
        testPrint(15);
        testPrint(15);
        testPrint(15);

    }
}