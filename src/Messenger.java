import java.util.ArrayList;

/**
 * An abstract class that is used to implement certain methods in sub messenger classes.
 * This includes the methods handleAt, move and shouldDie.
 * Please see each implementation of each method for more information.
 */
public abstract class Messenger {
    /**
     * An abstract method intended to handle an action or operation when called,
     * specific to the behavior defined within classes that extend the Messenger class.
     */
    public abstract void handleAt();

    /**
     * Moves the messenger within the network based on a given starting sensor node.
     * The movement logic and resulting behavior are defined in the subclass implementations.
     *
     * @return an ArrayList of SensorNode objects that are now marked as "busy"
     *         as a result of this movement.
     */
    public abstract ArrayList<SensorNode> move();

    /**
     *  An abstract method that handles when a messenger should die in the network.
     * @param amount The amount of turns the messenger has existed for.
     * @return True if the entity should die, false if not.
     */
    public abstract boolean shouldDie(int amount);
}