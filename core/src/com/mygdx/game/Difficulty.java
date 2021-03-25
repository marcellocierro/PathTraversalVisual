package com.mygdx.game;

/**
 * Created by Marcello395 on 2/20/18.
 */
public class Difficulty extends Node {

    int difficultyValue;

    public Difficulty(int difficultyValue) {
        super(difficultyValue + "");
        this.difficultyValue = difficultyValue;
    }

    /**
     * This will help us create weights for all standard nodes.
     * @return the node's weight.
     */
    @Override
    public int getDifficultyValue() {
        return difficultyValue;
    }
}