import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents a network of nodes with functionality for
 * checking the max value of the networks 2d coordinate space's x and y values.
 * It also includes returning the positions of valid neighbours for a given position.
 * Other functionalities include giving a specific node in a network from a coordinate.
 *
 * @since 13-5-2025
 */
public class Network {

    private int maxX;
    private int maxY;
    private SensorNode[][] nodes;
    private int totalNodes;
    private int neighbourCount;
    private final int communicationDistance;
    private final int distanceBetweenNodes;


     /**
     * Constructs a network from a given input with the total amount of nodes in a network and 
     * each coordinate position for each node in that network.
     * @param s The scanner containing a file with inputs
      *@param nodeSpacing The space between each node in the network.
      * @param nodeCommunicationRange The maximum communication rage for each node in the network.
     */
     public Network(Scanner s, int nodeSpacing, int nodeCommunicationRange) {
         communicationDistance = nodeCommunicationRange;
         distanceBetweenNodes = nodeSpacing;
         neighbourCount = calcNeighbourCount(communicationDistance, distanceBetweenNodes);

         //check if the input is null.
         if (s == null){
             throw new NullPointerException();
         }

         //check if the input has coordinates in it.
         if (!s.hasNext()){
             throw new InputMismatchException();
         }


        ArrayList<Integer> xCordList = new ArrayList<>();
        ArrayList<Integer> yCordList = new ArrayList<>();

        totalNodes = s.nextInt();
        s.nextLine();

        int prevXCord = 0;
        int prevYCord = 0;

        while(s.hasNext()) {
            String line = s.nextLine().trim();
            String[] test = line.split(",");
            int CurrentXCord = Integer.parseInt(test[0].trim());
            int CurrentYCord = Integer.parseInt(test[1].trim());
            
            xCordList.add(CurrentXCord);
            yCordList.add(CurrentYCord);

            if (CurrentXCord > prevXCord) {
                this.maxX = CurrentXCord;
            }

            if (CurrentYCord > prevYCord) {
               this.maxY = CurrentYCord;
            }
            prevXCord = CurrentXCord;
            prevYCord = CurrentYCord;
        }

        nodes = new SensorNode[maxX+1][maxY+1];

        for (int i = 0; i < Math.max(xCordList.size(), yCordList.size()); i++) {
            int x = xCordList.get(i);
            int y = yCordList.get(i);
            if(isWithinBoundary(x, y)){
                nodes[x][y] = new SensorNode(new Coordinate(x, y), neighbourCount);
            }
        }
        for (int i = 0; i < Math.max(xCordList.size(), yCordList.size()); i++) {
             int x = xCordList.get(i);
             int y = yCordList.get(i);
             if(isWithinBoundary(x, y)){
                 nodes[x][y].addNeighbours(getNeighbours(nodes[x][y].getCoordinate()));
             }
        }

    }

    /**
     * Returns the total amount of nodes in the network.
     * @return the total amount of nodes in the network.
     */
    public int getTotalNodes(){
         return totalNodes;
    }


    /**
     * Given a coordinate to a node in a network, return a list of its neighbours.
     * @param cord The given coordinate to a node in a network.
     * @return A list with all the node from the given positions neighbours.
     */
    public ArrayList<SensorNode> getNeighbours(Coordinate cord) {

        // list pre-filled with nulls, one slot per wedge
        ArrayList<SensorNode> neighbours =
                new ArrayList<>(Collections.nCopies(neighbourCount, null));

        final int x0 = cord.getX();
        final int y0 = cord.getY();

        final double r  = (double) communicationDistance / distanceBetweenNodes;
        final int    mx = (int) Math.floor(r);
        final double r2 = r * r;

        for (int dx = -mx; dx <= mx; dx++) {
            for (int dy = -mx; dy <= mx; dy++) {

                if (dx == 0 && dy == 0) continue;              // skip the centre

                if (dx * dx + dy * dy > r2) continue;

                int nx = x0 + dx;
                int ny = y0 + dy;

                if (!isWithinBoundary(nx, ny)) continue;

                SensorNode candidate = nodes[nx][ny];
                if (candidate == null) continue;               // empty spot

                int dir = directionIndex(dx, dy, neighbourCount);
                neighbours.set(dir, candidate);
            }
        }
        return neighbours;
    }

    /**
     * Calculate the number of nodes that lies within a circle of a node within the Network.
     *
     * @param communicationDistance nodes communication range.
     * @param distanceBetweenNodes the distance between
     * @return the number of neighbours that lies within a nodes communication radius in the 2d Network.
     */
    private int calcNeighbourCount(int communicationDistance, int distanceBetweenNodes) {
        double radius = (double) communicationDistance / distanceBetweenNodes;
        double radiusSquared = radius * radius;

        int maxStepX = (int) Math.floor(radius);

        int neighbourCount = 0;

        // Count neighbours within +- maxStepX
        for (int dx = - maxStepX; dx <= maxStepX; dx++) {
            int dxSquared = dx * dx;

            /*
                For every value of dx, calculate the maximum number of steps in y (dy)
                so that the point (dx, dy) is within the communication distance radius.
            */
            int maxStepY = (int) Math.floor(Math.sqrt(radiusSquared - dxSquared));
            
            /*
                For the current dx, calculate the largest possible integer dy such that (dx, dy) lies within
                the circular communication range.
            */
            neighbourCount += 2 * maxStepY + 1;
        }
        return neighbourCount - 1; // Subtract 1 to take itself into account.
    }

    /**
     * Returns a 0-based direction index for the neighbour that sits at
     * grid offset (dx,dy) relative to the current node.
     * 0  = South          4 = North
     * 1  = South-West     5 = North-East
     * 2  = West           6 = East
     * 3  = North-West     7 = South-East          (when neighbourCount == 8)
     * The formula works for ANY neighbourCount ≥ 1 — you get that many
     * equal wedges going clockwise from South.
     */
    private int directionIndex(int dx, int dy, int neighbourCount) {

        if (dx == 0 && dy == 0)           // the centre node itself
            throw new IllegalArgumentException("centre point has no direction");

        /* 1. angle measured CLOCKWISE from South, in the range 0 … 2π          *
         *    Swapping axes and flipping dy gives the wanted reference frame.   */
        double angle = Math.atan2(-dx, -dy);   // South = 0 rad
        if (angle < 0) angle += 2 * Math.PI;   // bring into 0 … 2π

        /* 2. split the circle into <neighbourCount> equal wedges               */
        return (int) (angle * neighbourCount / (2 * Math.PI));   // floor cast
    }




    /**
     * Returns the maximum allowed x value for a network.
     * @return the maximum allowed x value in the network 2d coordinate space.
     */
    public int getMaxX() {
        return maxX;
    }

    /**
     * Returns the maximum allowed y value for a network.
     * @return the maximum allowed y value in the network 2d coordinate space.
     */
    public int getMaxY() {
        return maxY;
    }

    /**
     * Given a valid coordinate in a network, return the node at the position
     * @param cord The coordinate given by the user.
     * @return the node stored at the given coordinates
     */
    public SensorNode getNode(Coordinate cord) {
        if (isWithinBoundary(cord.getX(), cord.getY())) {
            return nodes[cord.getX()][cord.getY()];
        } else {
           throw new IllegalArgumentException("The given coordinate must exist in the network!");
        }
    }

    /**
     * Determine 
     * @param xCord The given x coordinate
     * @param yCord The given y coordinate
     * @return True if the combined coordinate is valid, else false.
     */
    private boolean isWithinBoundary(int xCord, int yCord) {
        return xCord >= 0 && xCord < nodes.length && yCord >= 0 && yCord < nodes[0].length;
    }

}