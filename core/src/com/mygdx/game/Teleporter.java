package com.mygdx.game;

/**
 * Created by Marcello395 on 2/20/18.
 */

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;

public class Teleporter extends Node {

    Teleporter linkedTeleporter;
    String teleportID;
    boolean teleported;
    Sprite teleportPadTexture;


    public Teleporter(String tpID) {
        super("T" + tpID);
        teleportID = tpID;
        teleportPadTexture = new Sprite(new Texture(Gdx.files.internal("Textures/chosen.png")));
    }

    /**
     * Sets the position of Texture for a given Node.
     *
     * @param row    The row of the Node, to be set to.
     * @param col    The column of the Node, to be set to.
     * @param height
     */
    @Override
    public void setPosition(int row, int col, float height) {
        float y = height - ((float) (row * teleportPadTexture.getHeight()));
        float x = (float) (col * unvisitedTexture.getWidth() - 5);
        teleportPadTexture.setPosition(x, y);
        super.setPosition(row, col, height);
    }

    /**
     * Checks which texture to the return based on what action has been performed. Teleporter instance adds
     * teleported texture.
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
        } else if (teleported) {
            return teleportPadTexture;
        } else {
            return visitedTexture;
        }
    }

    /**
     * Since teleports should be prioritized, set their weight to 0.
     *
     * @return the weight 0.
     */
    @Override
    public int getDifficultyValue() {
        return 0;
    }

    /**
     * Links two teleporters together.
     *
     * @param linkedTeleporter The teleporter to be linked.
     */
    public void setLinkedTeleporter(Teleporter linkedTeleporter) {
        this.linkedTeleporter = linkedTeleporter;
    }

    public Teleporter getLinkedTeleporter() {
        return linkedTeleporter;
    }

    public String getTeleportID() {
        return teleportID;
    }

    public boolean isTeleported() {
        return teleported;
    }

    public Sprite getTeleportPadTexture() {
        return teleportPadTexture;
    }

    /**
     * This will help us keep track of our node's property for our path finding.
     */
    public void teleport() {
        teleported = true;
    }

    /**
     * This will help us keep track of our node's property for our path finding.
     */
    public void reset() {
        teleported = false;
    }

}
