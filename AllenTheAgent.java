/**
 * Assignment 2 Submission - CPE 480 Fall 2013
 * 
 * Can we find the goald?
 * 
 * Tag Ashby & Robert Campbell
 * tgashby & rfcampbe
 */

package Agents; // This is not a TODO: rename me to your Cal Poly username! :)

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
    public class Probe extends BotSearch {

        public Probe() {
            super();
        }
        
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
            setStartingLocation(current);
            
            returnList.add(getNorthOfSearchLocation());
            returnList.add(getEastOfSearchLocation());
            returnList.add(getSouthOfSearchLocation());
            returnList.add(getWestOfSearchLocation());
            
            return returnList;
        }
        
    }
    
    protected static class Pair {
        private final Integer first;
        private final Integer second;

        public Pair(Integer first, Integer second) {
            super();
            this.first = first;
            this.second = second;
        }

        public int hashCode() {
            int hashFirst = first != null ? first.intValue() : 0;
            int hashSecond = second != null ? second.intValue() : 0;

            return (hashFirst + hashSecond) * hashSecond + hashFirst;
        }

        public boolean equals(Object other) {
            if (other instanceof Pair) {
                    Pair otherPair = (Pair) other;
                    return 
                    ((  this.first == otherPair.first ||
                            ( this.first != null && otherPair.first != null &&
                              this.first.equals(otherPair.first))) &&
                     (	this.second == otherPair.second ||
                            ( this.second != null && otherPair.second != null &&
                              this.second.equals(otherPair.second))) );
            }

            return false;
        }

        public String toString()
        { 
               return "(" + first + ", " + second + ")"; 
        }
    }
    
    public static final int WUMPUS_I = 0;
    public static final int MINION_I = 1;
    public static final int PIT_I = 2;
    public static final int WALL_I = 3;
    public static final int GOLD_I = 4;
    public static final int SAFE_I = 5;
    
    LinkedList<Node> stack;
    HashMap<Pair, LinkedList<Integer>> paths;
    
    int lastMovement;
    Node start;
    Probe probe = new Probe();
    
    
    public AllenTheAgent()
    {
        super();
        setDeveloperName("Allen the Agent");
        stack = new LinkedList<Node>();
        start = getStartingLocation();
    }

    public void step()
    {
        Node current = getCurrentNode();
        
        if (paths == null) {
            paths = new HashMap<Pair, LinkedList<Integer>>();
            paths.put(new Pair(new Integer(current.getX()), new Integer(current.getY())), new LinkedList<Integer>());
        }
        
        boolean hasDangerousPercepts = false;
        Pair currentPair = new Pair(new Integer(current.getX()), new Integer(current.getY()));
        
        ArrayList<Node> neighbors = probe.getNodes(current);
        
        //Get the current beliefs about adjacent Nodes.
        int [] beliefsNorth = getNodeBeliefs(NORTH);
        int [] beliefsEast = getNodeBeliefs(EAST);
        int [] beliefsSouth = getNodeBeliefs(SOUTH);
        int [] beliefsWest = getNodeBeliefs(WEST);
        
        //Make all the paths, we will add them to the hashmap if it ends up to be valid to go to them.
        LinkedList<Integer> northPath = (LinkedList<Integer>) paths.get(currentPair).clone();
        northPath.add(new Integer(NORTH));
        LinkedList<Integer> eastPath = (LinkedList<Integer>) paths.get(currentPair).clone();
        eastPath.add(new Integer(EAST));
        LinkedList<Integer> southPath = (LinkedList<Integer>) paths.get(currentPair).clone();
        southPath.add(new Integer(SOUTH));
        LinkedList<Integer> westPath = (LinkedList<Integer>) paths.get(currentPair).clone();
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
            paths.put(new Pair(neighbors.get(NORTH).getX(), neighbors.get(NORTH).getY()), northPath);
            stack.addFirst(neighbors.get(EAST));
            paths.put(new Pair(neighbors.get(EAST).getX(), neighbors.get(EAST).getY()), eastPath);
            stack.addFirst(neighbors.get(SOUTH));
            paths.put(new Pair(neighbors.get(SOUTH).getX(), neighbors.get(SOUTH).getY()), southPath);
            stack.addFirst(neighbors.get(WEST));
            paths.put(new Pair(neighbors.get(WEST).getX(), neighbors.get(WEST).getY()), westPath);
        }
        
        //Set beliefs
        setNodeBeliefs(NORTH, beliefsNorth);
        setNodeBeliefs(EAST, beliefsEast);
        setNodeBeliefs(SOUTH, beliefsSouth);
        setNodeBeliefs(WEST, beliefsWest);
        
        //Move
        Node nextDestination = stack.removeFirst();
        int movementResult = doMovement(constructPath(nextDestination));
        
        if (movementResult == HIT_WALL) {
            setBelief(nextDestination, WALL_HERE, YES);
        } else if (movementResult == SAFE) {
            setBelief(nextDestination, SAFE_HERE, YES);
        } else if (movementResult == HURT) {
            setBelief(nextDestination, MINION_HERE, YES);
        }
        //Else if died....
    }

    private int doMovement(LinkedList<Integer> directions) {
        Integer temp;
        int retVal = 0;
        
        while(directions.size() > 0){
            temp = directions.removeFirst();
            if (temp.equals(new Integer(NORTH))) {
                retVal = moveNorth();
            } else if (temp.equals(new Integer(EAST))) {
                retVal = moveEast();
            } else if (temp.equals(new Integer(SOUTH))) {
                retVal = moveSouth();
            } else if (temp.equals(new Integer(WEST))) {
                retVal = moveWest();
            }
        }
        
        return retVal;
    }
    
    private LinkedList<Integer> constructPath(Node n) {
        LinkedList<Integer> pathToCurrent = paths.get(new Pair(getBotLocation().getX(), getBotLocation().getY()));
        LinkedList<Integer> pathToNext = paths.get(new Pair(n.getX(), n.getY()));
        LinkedList<Integer> path = new LinkedList<Integer>();
        Integer temp;
        
        int index = 0;
        
        //Lets find the part of the path where they branch.
        if (pathToNext.size() > 0 && pathToCurrent.size() > 0){
            while (pathToNext.get(index).equals(pathToCurrent.get(index))){
                index++;
            }
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
        start = getStartingLocation();
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