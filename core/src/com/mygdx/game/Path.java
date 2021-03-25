package com.mygdx.game;

/**
 * Created by Marcello395 on 2/20/18.
 */
import java.util.ArrayList;

public class Path implements Comparable<Path>{

    Node head;
    ArrayList<Node> nodeList = new ArrayList<Node>(0);
    int difficultyEncounters = 0;
    Grid gridToTraverse;

    public Path(Node beginning, Grid providedGrid){
        this.gridToTraverse = providedGrid;
        nodeList.add(beginning);
        head = beginning;
    }

    /**
     * This constructor should only be called if a path has already been established. This will allow
     * additional paths to formulate.
     * @param firstPath
     * @param addedNode
     * @param providedGrid
     */
    public Path(Path firstPath , Node addedNode, Grid providedGrid){
        this.gridToTraverse = providedGrid;
        for(Node n : firstPath.getOrderedNodeArray()){ //for every node in our given path, add it to our nodeList.
            nodeList.add(n);
        }
        difficultyEncounters = firstPath.getDifficultyEncounters();
        addNodetoList(addedNode);
    }

    /**
     * Adds a new node to our list of nodes. New node added becomes head as its the most recent node added.
     * @param nodeToAdd the node which we are adding to our list.
     */
    public void addNodetoList(Node nodeToAdd){
        head = nodeToAdd;
        nodeList.add(nodeToAdd);
        difficultyEncounters += nodeToAdd.getDifficultyValue();
    }


    public Node getHead() {
        return head;
    }

    public ArrayList<Node> getNodeList() {
        return nodeList;
    }

    public Grid getGridToTraverse() {
        return gridToTraverse;
    }

    /**
     * Builds an ordered array based on our ArrayList, since its ordered.
     * @return the Node[]
     */
    public Node[] getOrderedNodeArray(){
        return nodeList.toArray(new Node[nodeList.size()]);
    }

    /**
     * Get the weighted nodes to traverse.
     * @return the raw number of encounters.
     */
    public int getDifficultyEncounters() {
        return difficultyEncounters;
    }

    /**
     * Gets the weighted nodes to traverse with respect to the heuristic.
     * @return
     */
    public int getDEwHeuristic(){
        return difficultyEncounters + gridToTraverse.getHeuristic3(head);
    }

    /**
     * Compares our current path with a given path. This will help us in deciding best paths when teleports occur.
     * @param b The path supplied to compare our current path with.
     * @return -1 if Path A is shorter than path B.
     * @return 0 if Path A is equal to Path B.
     * @return 1 if Path A is longer than Path B.
     */
    @Override
    public int compareTo(Path b) {
        int pathA = this.getDEwHeuristic(), pathB = b.getDEwHeuristic();
        if (pathA - pathB < 0){
            return -1;
        } else if (pathA == pathB){
            return 0;
        }
        return 1;
    }
}