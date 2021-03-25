package com.mygdx.game;

/**
 * Created by Marcello395 on 2/20/18.
 */

/**
 * This will keep track of a specific node's id.
 */
public class NodeProperties {
    String name;
    int row;
    int col;

    public NodeProperties(String name, int r, int c){
        this.name = name;
        row = r;
        col = c;
    }
    public String getName() {
        return name;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}