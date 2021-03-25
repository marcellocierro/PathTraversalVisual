package com.mygdx.game;

/**
 * Created by Marcello395 on 2/20/18.
 */

import java.util.PriorityQueue;
import java.util.ArrayList;

public class Grid {

    Node destination;
    boolean findingPath;
    Node[][] nodes;
    Teleporter teleports[];
    PriorityQueue<Path> bestPathQueue = new PriorityQueue<Path>(1000);

    public Grid(Node[][] nodes) {
        this.nodes = nodes;
        ArrayList<Teleporter> teleporterList = new ArrayList<Teleporter>(0);
        for (Node[] row : nodes)
            for (Node node : row)
                if (node instanceof Teleporter)
                    teleporterList.add((Teleporter) node);
        this.teleports = teleporterList.toArray(new Teleporter[teleporterList.size()]);
    }

    /**
     * This is a very flushed out version of Astar, which is a bitch of a hack but this is due to considerations of
     * teleports. Essentially we are storing paths in a priority queue and returning the current path if it is the destination.
     * If a teleport is used, we recalculate our best path and store that, otherwise we continue as usual.
     *
     * @param beginning      First node to start at.
     * @param endDestination Second Node to start at.
     * @return current path which is going to be the best path.
     */
    public Path Astar(Node beginning, Node endDestination) {
        Path currentPath;
        if (bestPathQueue.size() == 0 && !findingPath) {
            findingPath = true;
            this.destination = endDestination;
            currentPath = new Path(beginning, this);
            for (Node neighbor : currentPath.getHead().getNeighbors()) {
                if (neighbor == destination) {
                    currentPath.addNodetoList(neighbor);
                    bestPathQueue.clear();
                    destination = null;
                    return currentPath;
                } else {
                    bestPathQueue.add(new Path(currentPath, neighbor, this));
                }
            }
        }
        if (bestPathQueue.size() == 0) {
            destination = null;
            return new Path(new Difficulty(0), this);
        }
        //I bet you'd think this is the best place to start a new function.
        //Hahahaha you wish.
        currentPath = bestPathQueue.poll();
        Node c = currentPath.getHead();
        c.visit();
        if ((c instanceof Teleporter)) {
            if (!((Teleporter) c).isTeleported()) {
                Path newTeleporterPath = new Path(currentPath, ((Teleporter) c).getLinkedTeleporter(), this);
                c.visit();
                ((Teleporter) c).teleport();
                ((Teleporter) c).getLinkedTeleporter().visit();
                if (((Teleporter) c).getLinkedTeleporter() == destination) {
                    bestPathQueue.clear();
                    destination = null;
                    return newTeleporterPath;
                }
                currentPath = newTeleporterPath;
                for (Node neighbor : currentPath.getHead().getNeighbors()) {
                    if (!neighbor.wasVisited() || (neighbor instanceof Teleporter && !(((Teleporter) neighbor).isTeleported()))) {
                        if (neighbor == destination) {
                            currentPath.addNodetoList(neighbor);
                            bestPathQueue.clear();
                            destination = null;
                            return currentPath;
                        }
                        Path newPath = new Path(currentPath, neighbor, this);
                        boolean pathConsidered = false;
                        for (Path path : bestPathQueue.toArray(new Path[bestPathQueue.size()]))
                            if (path.getHead() == newPath.getHead()) {
                                if (newPath.compareTo(path) < 0) {
                                    bestPathQueue.remove(path);
                                    bestPathQueue.add(newPath);
                                    pathConsidered = true;
                                } else {
                                    pathConsidered = true;
                                }
                                break;
                            }
                        if (!pathConsidered)
                            bestPathQueue.add(newPath);
                    }
                }
            }
            //Try to beat this record of nested If-statements Landon
        } else {
            for (Node neighbor : currentPath.getHead().getNeighbors()) {
                if (!neighbor.wasVisited() || (neighbor instanceof Teleporter && !(((Teleporter) neighbor).isTeleported()))) {
                    if (neighbor == destination) {
                        currentPath.addNodetoList(neighbor);
                        bestPathQueue.clear();
                        destination = null;
                        return currentPath;
                    }
                    Path newPath = new Path(currentPath, neighbor, this);
                    boolean pathDealtWith = false;
                    for (Path path : bestPathQueue.toArray(new Path[bestPathQueue.size()]))
                        if (path.getHead() == newPath.getHead()) {
                            pathDealtWith = true;
                            if (newPath.compareTo(path) < 0) {
                                bestPathQueue.remove(path);
                                bestPathQueue.add(newPath);
                            }
                            break;
                        }
                    if (!pathDealtWith)
                        bestPathQueue.add(newPath);
                }
            }
        }
        return null;
    }


    /**
     * Resets the state of the grid, so that we can start over.
     */
    public void resetGridState() {
        findingPath = false;
        for (Node[] row : nodes) {
            for (Node node : row) {
                node.resetProperties();
                node.deSelect();
                if (node instanceof Teleporter)
                    ((Teleporter) node).reset();
            }
        }
    }


    /**
     * Astar using manhattan distance, adjusted so it is always less than the distance to the chosen point.
     *
     * If the path crosses a teleport we recalculate the best distance to and from the teleport and compare it to our distance
     * without the teleporter and pick the the better value.
     *
     * @param hNode The node to which will be used to find our distance metric.
     * @return The best path.
     */
    public int getHeuristic(Node hNode) {
        //Get all linked teleports
        if (hNode instanceof Teleporter) {
            hNode = ((Teleporter) hNode).getLinkedTeleporter();
        }

        float distance = Math.abs((destination.getTexture().getX() - hNode.getTexture().getX())) / hNode.getTexture().getWidth()
                + Math.abs((destination.getTexture().getY() - hNode.getTexture().getY())) / hNode.getTexture().getHeight();

        for (Teleporter teleporter : teleports) {
            if (!teleporter.isTeleported()) {
                float toTeleporter = Math.abs((teleporter.getTexture().getX() - hNode.getTexture().getX())) / hNode.getTexture().getWidth()
                        + Math.abs((teleporter.getTexture().getY() - hNode.getTexture().getY())) / hNode.getTexture().getHeight();
                float fromTeleporter = Math.abs((destination.getTexture().getX() - teleporter.getLinkedTeleporter().getTexture().getX())) / hNode.getTexture().getWidth()
                        + Math.abs((destination.getTexture().getY() - teleporter.getLinkedTeleporter().getTexture().getY())) / hNode.getTexture().getHeight();
                float distanceWithTeleporter = toTeleporter + fromTeleporter;
                if (distanceWithTeleporter < distance)
                    distance = distanceWithTeleporter;
            }
        }
        return (int) distance;
//        return 0;
    }

    /**
     * Greedy Best First.
     * @param hNode
     * @return The shortest distance to the point.
     */
    public int getHeuristic2(Node hNode) {
        if (hNode instanceof Teleporter) {
            hNode = ((Teleporter) hNode).getLinkedTeleporter();
        }
            float distance = Math.abs((destination.getTexture().getX() - hNode.getTexture().getX()))
                    + Math.abs((destination.getTexture().getY() - hNode.getTexture().getY()));

            for (Teleporter teleporter : teleports) {
                if (!teleporter.isTeleported()) {
                    float toTeleporter = Math.abs((teleporter.getTexture().getX() - hNode.getTexture().getX()))
                            + Math.abs((teleporter.getTexture().getY() - hNode.getTexture().getY()));
                    float fromTeleporter = Math.abs((destination.getTexture().getX() - teleporter.getLinkedTeleporter().getTexture().getX()))
                            + Math.abs((destination.getTexture().getY() - teleporter.getLinkedTeleporter().getTexture().getY()));
                    float distanceWithTeleporter = toTeleporter + fromTeleporter;
                    if (distanceWithTeleporter < distance)
                        distance = distanceWithTeleporter;
                }
            }
            return (int) distance;
        }

    /**
     * Dijkstra
     * @param hNode
     * @return The best path
     */
    public int getHeuristic3(Node hNode) {
        return 0;
    }
}