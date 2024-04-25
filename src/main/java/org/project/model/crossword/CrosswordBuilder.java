package org.project.model.crossword;

public class CrosswordBuilder {

    final private CrosswordModel crosswordModel;

    public CrosswordBuilder(){
        crosswordModel = new CrosswordModel();
    }

    public Crossword build(){
        return crosswordModel;
    }

    public boolean insertWord(String word, String meaning){
        return crosswordModel.insertWord(word, meaning);
    }

    public boolean tryToInsertClueWord(String word, String definition){
        return crosswordModel.tryToInsertClueWord(word, definition);
    }
}
