/**
 * This class represents a coordinate in the network.
 * Methods included a constructor for the coordinate, getter methods for its X and Y values,
 * a method that returns the distance between two nodes.
 * Also included are methods for comparing two coordinates to see if their equal,
 * a method that returns the hash value of a coordinate and a method that converts a coordinate to a string.
 */
public class Coordinate {
    private final int x;
    private final int y;

    /**
     * A constructor method that creates a coordinate
     * with a given X and Y value.
     * @param x The X position in the network.
     * @param y The Y position in the network.
     */
    public Coordinate(int x, int y){

        if (x < 0 || y < 0){
            throw new IllegalArgumentException();
        } else {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * The x part of the coordinate
     * @return x part of coordinate
     */
    public int getX(){
        return this.x;
    }

    /**
     * The y part of the coordinate
     * @return y part of coordinate
     */
    public int getY(){
        return this.y;
    }

    /**
     * Calculates the distance between 2 nodes using Euclidean distance.
     * (Where the distance between rows and columns are 10 length units)
     * @param cord coordinate for node to calculate distance to
     * @return the distance between 2 nodes
     */
    public double getDistance(Coordinate cord){
        int dx = this.x - cord.getX();
        int dy = this.y - cord.getY();
        return 10 * Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * For testing purposes
     * @param obj object to compare
     * @return true if object is equal to this, false if not.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Coordinate other = (Coordinate) obj;
        return this.x == other.x && this.y == other.y;
    }

    /**
     * @return this objects hash value.
     */
    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    /**
     * Converts a given coordinate to a string
     * @return The converted coordinate.
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
