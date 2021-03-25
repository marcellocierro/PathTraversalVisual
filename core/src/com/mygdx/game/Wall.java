package com.mygdx.game;

/**
 * Created by Marcello395 on 2/20/18.
 */

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;

public class Wall extends Node {

    Sprite wallTexture;

    public Wall() {
        super("F");
        wallTexture = new Sprite(new Texture(Gdx.files.internal("Textures/wall.png")));
    }

    /**
     * Helps us generate the impassible wall
     *
     * @return the wall texture.
     */
    @Override
    public Sprite getTexture() {
        return wallTexture;
    }

    /**
     * Sets the position of Texture for a given Node.
     *
     * @param row    The row of the Node, to be set to.
     * @param col    The column of the Node, to be set to.
     * @param height    The Maximum height of the Node
     */
    @Override
    public void setPosition(int row, int col, float height) {
        float y = height - ((float) (row * wallTexture.getHeight()));
        float x = (float) (col * unvisitedTexture.getWidth() - 5);
        wallTexture.setPosition(x, y);
        super.setPosition(row, col, height);

    }

    /**
     * Since walls are impassible, they should have a value of infinity.
     *
     * @return Int.max since it should never be considered.
     */
    @Override
    public int getDifficultyValue() {
        return Integer.MAX_VALUE;
    }
}