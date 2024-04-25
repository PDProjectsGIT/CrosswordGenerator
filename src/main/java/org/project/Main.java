package org.project;

import org.project.model.crossword.Crossword;
import org.project.model.crossword.CrosswordBuilder;
import org.project.model.crossword.CrosswordException;
import org.project.model.sjp.SJPException;
import org.project.model.sjp.SJPGameParser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Main {

    public static Crossword getCrossword(int wordCounter, boolean withClue) throws CrosswordException{
        SJPGameParser sjpGameParser = new SJPGameParser();
        CrosswordBuilder crosswordBuilder = new CrosswordBuilder();
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

    public static double getTestTime(int crosswordSize, int probes){
        Crossword crossword = getCrossword(crosswordSize, false);
        double result = 0;
        for(int i = 0; i < probes; i++){
            System.out.println("crossword: " + (i + 1));
            try{
                crossword = getCrossword(crosswordSize, false);
            }catch (CrosswordException e){
                i--;
                System.out.println("Error: "+e.getMessage() + Arrays.toString(e.getStackTrace()));
                continue;
            }
            result += crossword.getTimeInMilliseconds();
        }
        result = result / (double) probes;
        return result;
    }

    public static double getSingleTestTime(int crosswordSize, boolean withClue){
        try{
            Crossword crossword = getCrossword(crosswordSize, withClue);
        }catch (CrosswordException _){}
        Crossword crossword;
        double time = 0.0;
        try{
            crossword = getCrossword(crosswordSize, withClue);
            time = crossword.getTimeInMilliseconds();
        }catch (CrosswordException e){
            System.out.println("Error: "+e.getMessage() + Arrays.toString(e.getStackTrace()));
        }
        return time;
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

        String fileName = "crossword_test_time_results.csv";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("CrosswordSize;P1t;P2;P3;P4;P5;P6;P7;P8;P9;P10\n");

            for (int crosswordSize = 1; crosswordSize <= 100; crosswordSize++) {
                System.out.println("Crossword number: " + crosswordSize);
                StringBuilder sb = new StringBuilder();
                sb.append(crosswordSize);

                for (int i = 0; i < 10; i++) {
                    double singleTestTime = getSingleTestTime(crosswordSize, false);
                    sb.append(";").append(singleTestTime);
                }

                writer.write(sb + "\n");
            }

            System.out.println("Wyniki eksperymentu zapisane do pliku: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}