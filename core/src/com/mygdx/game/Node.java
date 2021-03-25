package com.mygdx.game;

/**
 * Created by Marcello395 on 2/20/18.
 */

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;

/**
 * Sprites are used because you can set their respective x and y coordinates.
 * <p>
 * Abstract so children can be made with special properties to reduce overhead.
 */

public abstract class Node {

    Node predecessor;

    ArrayList<Node> neighbors = new ArrayList<Node>(0);
    boolean visited, chosen, selected, badSelect;
    String textWeight;
    Sprite unvisitedTexture, badSelectTexture, selectedTexture, visitedTexture, chosenTexture;

    public Node(String textWeight) {
        this.textWeight = textWeight;
        visitedTexture = new Sprite(new Texture(Gdx.files.internal("Textures/visited.png")));
        unvisitedTexture = new Sprite(new Texture(Gdx.files.internal("Textures/unvisited.png")));
        badSelectTexture = new Sprite(new Texture(Gdx.files.internal("Textures/badselect.png")));
        selectedTexture = new Sprite(new Texture(Gdx.files.internal("Textures/selected.png")));
        chosenTexture = new Sprite(new Texture(Gdx.files.internal("Textures/chosen.png")));
    }


    /**
     * Sets the position of Texture for a given Node.
     *
     * @param row    The row of the Node, to be set to.
     * @param col    The column of the Node, to be set to.
     * @param height The Maximum height of a given Node.
     */
    public void setPosition(int row, int col, float height) {
        float y = height - ((float) (row * unvisitedTexture.getHeight()));
        float x = (float) (col * unvisitedTexture.getWidth() - 5);

        visitedTexture.setPosition(x, y);
        unvisitedTexture.setPosition(x, y);
        badSelectTexture.setPosition(x, y);
        selectedTexture.setPosition(x, y);
        chosenTexture.setPosition(x, y);
    }

    /**
     * Checks which texture to the return based on what action has been performed.
     *
     * @return The respective texture.
     */
    public Sprite getTexture() {
        if (selected) {
            return selectedTexture;
        } else if (badSelect) {
            return badSelectTexture;
        } else if (!visited) {
            return unvisitedTexture;
        } else if (chosen) {
            return chosenTexture;
        } else {
            return visitedTexture;
        }
    }

    /**
     * Either selects or deselects a given Node.
     *
     * @return true if selected.
     */
    public boolean isSelected() {
        selected = !selected;
        return selected;
    }

    public void deSelect() {
        selected = false;
        badSelect = false;
    }

    public void badSelect() {
        selected = false;
        badSelect = true;
    }

    /**
     * This will help us keep track of our node's properties for our path finding.
     */
    public void visit() {
        visited = true;
    }

    public boolean wasVisited() {
        return visited;
    }

    public void chosen() {
        chosen = true;
    }

    public void resetProperties() {
        visited = false;
        chosen = false;
    }

    /**
     * Adds a neighbor to the neighbors arrayList.
     *
     * @param neighbor Node to be added to arrayList of Nodes.
     */
    public void addNeighbor(Node neighbor) {
        neighbors.add(neighbor);
    }

    /**
     * Stores neighbors in a Node array based on our neighbors arrayList. This will allow us to make multiple nodes
     * based on our neighbors.
     *
     * @return A Node array of size neighbors.size
     */
    public Node[] getNeighbors() {
        return neighbors.toArray(new Node[neighbors.size()]);
    }

    /**
     * This will be a graphic we render on our nodes to show a respective weight.
     *
     * @return A string representing weight.
     */
    public String getTextWeight() {
        return textWeight;
    }

    /**
     * To be implemented in every instance of Node. This will allow us to separate teleports and impassibles
     * from regular Nodes.
     *
     * @return The value of difficulty. ie) Impassibles should be INTEGER.MAX.
     */
    public abstract int getDifficultyValue();

}
