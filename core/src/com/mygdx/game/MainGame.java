package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.BitmapFont;



public class MainGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;
    private Node nodeGrid[][];
    private Node selection1, selection2;
    private Grid grid;
    private int gridRows;
    private int gridCols;
    private boolean clicked, pathFound, pathFinding;
    private float screenHeight, screenWidth;

    int count;

    @Override
    public void create () {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        batch = new SpriteBatch();
        File inputFile = Gdx.files.internal("Input3.txt").file();
        createGridFromFile(inputFile);
        setNeighborReferencesForGrid(nodeGrid);
        grid = new Grid(nodeGrid);
        camera = new OrthographicCamera(screenHeight, screenWidth);
    }

    @Override

    public void render () {
        Gdx.gl.glClearColor(112, 128, 144, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (!clicked) {
                select(Gdx.input.getX(),Gdx.input.getY());
                clicked = true;
            }
        } else {
            clicked = false;
        }
        if (pathFinding && count++ > 0) {
            count = 0;
            Path path = grid.Astar(selection1, selection2);
            if (path != null) {
                if (path.getHead() == selection2) {
                    chosen(path);
                } else {
                    selection1.badSelect();
                    selection2.badSelect();
                }
                selection1 = null;
                selection2 = null;
                pathFinding = false;
                pathFound = true;
            }
        }
        drawGrid();
        batch.end();
    }

    private void chosen(Path path) {
        for (Node node : path.getOrderedNodeArray()) {
            node.chosen();
        }
    }

    private void select(int x, int y) {
        if (pathFound) {
            grid.resetGridState();
            pathFound = false;
        }
        if (!pathFinding) {
            Vector3 input = new Vector3(x,y,0);
            camera.unproject(input);
            for (int row = 0; row < gridRows; row++)
                for (int col = 0; col < gridCols; col++)
                    if (nodeGrid[row][col].getTexture().getBoundingRectangle().contains(input.x,input.y) && !(nodeGrid[row][col] instanceof Wall)) {
                        if (nodeGrid[row][col].isSelected()) {
                            if (selection1 != null) {
                                selection2 = nodeGrid[row][col];
                                pathFinding = true;
                            } else
                                selection1 = nodeGrid[row][col];
                        } else {
                            selection1 = null;
                        }
                        break;
                    }
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom += 0.25;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            camera.zoom -= 0.25;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.translate(-50, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            camera.translate(50, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            camera.translate(0, -50, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            camera.translate(0, 50, 0);
        }
        camera.update();
    }

    private void drawGrid() {
        for (int row = 0; row < nodeGrid.length; row++)
            for (int col = 0; col < nodeGrid[row].length; col++) {
                Node node = nodeGrid[row][col];
                Sprite sprite = node.getTexture();
                sprite.draw(batch);
                float x = sprite.getX() + (sprite.getWidth()/2);
                float y = sprite.getY() + (sprite.getHeight()/2);
                font.draw(batch, node.getTextWeight(), x, y);
            }
    }

    private void setDimensions(File f) {
        try {
            Scanner reader = new Scanner(f);
            int rows = 1;
            int cols = 0;
            String[] tokens = reader.nextLine().split(" +");
            cols = tokens.length;
            while (reader.hasNextLine()) {
                rows++;
                reader.nextLine();
            }
            gridRows = rows;
            gridCols = cols;
            nodeGrid = new Node[gridRows][gridCols];
        } catch (FileNotFoundException e) {
            System.out.println("Error -- File Not Found");
            System.exit(0);
        }
    }

    private void createGridFromFile(File f) {
        try {
            setDimensions(f);
            Scanner reader = new Scanner(f);
            int row = 0;
            while (reader.hasNextLine()) {
                String[] tokens = reader.nextLine().split(" +");
                for (int col = 0; col < gridCols; col++) {
                    if (tokens[col].equalsIgnoreCase("F") || tokens[col].equalsIgnoreCase("B")) {
                        nodeGrid[row][col] = new Wall();
                    } else if (tokens[col].substring(0,1).equalsIgnoreCase("T")) {
                        nodeGrid[row][col] = new Teleporter(tokens[col].substring(1));
                    } else {
                        nodeGrid[row][col] = new Difficulty(Integer.parseInt(tokens[col]));
                    }
                    nodeGrid[row][col].setPosition(row, col, screenHeight);
                }
                row++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error -- File Not Found");
            System.exit(0);
        }
    }


    private void setNeighborReferencesForGrid(Node[][] nodeGrid) {
        ArrayList<NodeProperties> linkerList = new ArrayList<NodeProperties>(0);

        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                Node node = nodeGrid[row][col];
                if (! (node instanceof Wall)) {
                    if (node instanceof Teleporter) {
                        boolean linkNotCompleted = true;
                        for (Object linkerObject : linkerList.toArray()) {
                            NodeProperties link = (NodeProperties) linkerObject;
                            if (link.getName().equals(node.getTextWeight())) {
                                Teleporter partner = (Teleporter)nodeGrid[link.getRow()][link.getCol()];
                                ((Teleporter)node).setLinkedTeleporter(partner);
                                partner.setLinkedTeleporter((Teleporter)node);
                                linkNotCompleted = false;
                                break;
                            }
                        }
                        if (linkNotCompleted) {
                            linkerList.add(new NodeProperties(node.getTextWeight(), row, col));
                        }
                    }
                    if (row > 0)
                        if (! (nodeGrid[row-1][col] instanceof Wall))
                            node.addNeighbor(nodeGrid[row-1][col]);
                    if (row < gridRows - 1)
                        if (! (nodeGrid[row+1][col] instanceof Wall))
                            node.addNeighbor(nodeGrid[row+1][col]);
                    if (col > 0)
                        if (! (nodeGrid[row][col-1] instanceof Wall))
                            node.addNeighbor(nodeGrid[row][col-1]);
                    if (col < gridCols - 1)
                        if (! (nodeGrid[row][col+1] instanceof Wall))
                            node.addNeighbor(nodeGrid[row][col+1]);
                }
            }
        }
    }

}