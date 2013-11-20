/**
 * Assignment 2 Submission - CPE 480 Fall 2013
 * 
 * Can we find the goald?
 * 
 * Tag Ashby & Robert Campbell
 * tgashby & rfcampbe
 */

package Agents.tgashby; // This is not a TODO: rename me to your Cal Poly username! :)

import BotEnvironment.SearchBot.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * public static final int NORTH = 0;
 * public static final int EAST = 1;
 * public static final int SOUTH = 2;
 * public static final int WEST = 3;
 */

public class AllenTheAgent extends WumpusAgent
{
    private class Probe extends BotSearch {

        @Override
        public void searchStep() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void movementStep() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        public ArrayList<Node> getNodes(Node current) {
            ArrayList<Node> returnList = new ArrayList<Node>();
            moveSearchLocation(current);
            
            returnList.add(getNorthOfSearchLocation());
            returnList.add(getEastOfSearchLocation());
            returnList.add(getSouthOfSearchLocation());
            returnList.add(getWestOfSearchLocation());
            
            return returnList;
        }
        
    }
    
    public static final int WUMPUS_I = 0;
    public static final int MINION_I = 1;
    public static final int PIT_I = 2;
    public static final int WALL_I = 3;
    public static final int GOLD_I = 4;
    public static final int SAFE_I = 5;
    
    LinkedList<Node> stack;
    HashMap<Node, LinkedList<Integer>> paths;
    
    int lastMovement;
    Node start;
    Probe probe = new Probe();
    
    public AllenTheAgent()
    {
        super();
        setDeveloperName("Allen the Agent");
        stack = new LinkedList<Node>();
        paths = new HashMap<Node, LinkedList<Integer>>();
        start = getBotLocation();
        paths.put(start, new LinkedList<Integer>());
    }

    public void step()
    {
        boolean hasDangerousPercepts = false;
        Node current = getBotLocation();
        
        ArrayList<Node> neighbors = probe.getNodes(current);
        
        //Get the current beliefs about adjacent Nodes.
        int [] beliefsNorth = getNodeBeliefs(NORTH);
        int [] beliefsEast = getNodeBeliefs(EAST);
        int [] beliefsSouth = getNodeBeliefs(SOUTH);
        int [] beliefsWest = getNodeBeliefs(WEST);
        
        //Make all the paths, we will add them to the hashmap if it ends up to be valid to go to them.
        LinkedList<Integer> northPath = (LinkedList<Integer>) paths.get(current).clone();
        northPath.add(new Integer(NORTH));
        LinkedList<Integer> eastPath = (LinkedList<Integer>) paths.get(current).clone();
        eastPath.add(new Integer(EAST));
        LinkedList<Integer> southPath = (LinkedList<Integer>) paths.get(current).clone();
        southPath.add(new Integer(SOUTH));
        LinkedList<Integer> westPath = (LinkedList<Integer>) paths.get(current).clone();
        westPath.add(new Integer(WEST));
        
        
        //Check percepts
        if(nearMinion()){
            hasDangerousPercepts = true;
        } else {
            
        }
        if(nearWumpus()) {
            hasDangerousPercepts = true;
        } else {
            
        }
        if(nearPit()) {
            hasDangerousPercepts = true;
        } else {
        
        }
        if(nearGold()) {
            
        }
        
        if (!hasDangerousPercepts) {
            stack.addFirst(neighbors.get(NORTH));
            paths.put(neighbors.get(NORTH), northPath);
            stack.addFirst(neighbors.get(EAST));
            paths.put(neighbors.get(EAST), eastPath);
            stack.addFirst(neighbors.get(SOUTH));
            paths.put(neighbors.get(SOUTH), southPath);
            stack.addFirst(neighbors.get(WEST));
            paths.put(neighbors.get(WEST), westPath);
        }
        
        //Set beliefs
        setNodeBeliefs(NORTH, beliefsNorth);
        setNodeBeliefs(EAST, beliefsEast);
        setNodeBeliefs(SOUTH, beliefsSouth);
        setNodeBeliefs(WEST, beliefsWest);
        
        //Move
        //did we just loose 1000 points?
        doMovement(constructPath(stack.removeFirst()));
        setBelief(SAFE_HERE, YES);
    }

    private void doMovement(LinkedList<Integer> directions) {
        Integer temp;
        while(directions.size() > 0){
            temp = directions.removeFirst();
            if (temp.equals(new Integer(NORTH))) {
                moveNorth();
            } else if (temp.equals(new Integer(EAST))) {
                moveEast();
            } else if (temp.equals(new Integer(SOUTH))) {
                moveSouth();
            } else if (temp.equals(new Integer(WEST))) {
                moveWest();
            }
        }
    }
    
    private LinkedList<Integer> constructPath(Node n) {
        LinkedList<Integer> pathToCurrent = paths.get(getBotLocation());
        LinkedList<Integer> pathToNext = paths.get(n);
        LinkedList<Integer> path = new LinkedList<Integer>();
        Integer temp;
        
        int index = 0;
        
        //Lets find the part of the path where they branch.
        while (pathToNext.get(index).equals(pathToCurrent.get(index))){
            index++;
        }
        //Construct the backtracking part of the path... harder...
        for (int i = pathToCurrent.size() - 1; i >= index; i--){
            temp = pathToCurrent.get(i);
            if (temp.equals(new Integer(NORTH))){
                path.addLast(new Integer(SOUTH));
            } else if (temp.equals(new Integer(SOUTH))){
                path.addLast(new Integer(NORTH));
            } else if (temp.equals(new Integer(EAST))){
                path.addLast(new Integer(WEST));
            } else if (temp.equals(new Integer(WEST))){
                path.addLast(new Integer(EAST));
            } else {
                log("wtf, directions other than the cardinals in the path?");
            }
        }
        //Construct that last part of the path (this is easy)
        for (int i = index; i < pathToNext.size(); i++){
            path.addLast(pathToNext.get(i));
        }
        
        return path;
    }
    
    public void reset()
    {
        super.reset();
        stack = new LinkedList<Node>();
        paths = new HashMap<Node, LinkedList<Integer>>();
        start = getBotLocation();
    }
    
    private int[] getNodeBeliefs(int direction){
        int[] beliefs = new int[6];
        beliefs[WUMPUS_I] = getBelief(direction, WUMPUS_HERE);
        beliefs[MINION_I] = getBelief(direction, MINION_HERE);
        beliefs[PIT_I] = getBelief(direction, PIT_HERE);
        beliefs[SAFE_I] = getBelief(direction, SAFE_HERE);
        beliefs[WALL_I] = getBelief(direction, WALL_HERE);
        beliefs[GOLD_I] = getBelief(direction, GOLD_HERE);
        
        return beliefs;
    }
    
    private void setNodeBeliefs(int direction, int [] beliefs){
        setBelief(direction, beliefs[WUMPUS_I]);
        setBelief(direction, beliefs[MINION_I]);
        setBelief(direction, beliefs[PIT_I]);
        setBelief(direction, beliefs[SAFE_I]);
        setBelief(direction, beliefs[WALL_I]);
        setBelief(direction, beliefs[GOLD_I]);
    }
}