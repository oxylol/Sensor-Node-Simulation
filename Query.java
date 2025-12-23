import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * A class that represents a query in the system.
 * A query will look for a certain event in the system.
 * If the query finds the event it was looking for it will send a response containing this Querys route,
 * and the information of the Event that was picked up from the Node that detected the Event.
 */
public class Query extends Messenger{
    private final int eventId;
    private final List<SensorNode> route = new ArrayList<>();
    private int timeAlive;
    private boolean shouldDie;
    private SensorNode currentSensorNode;
    private Event eventInfo;
    private boolean queryHasNotFoundARoute;
    private static final Random random = new Random();


    /**
     *
     * @param eventId the ID of the Event to search for.
     * @param startNode the node where the query starts
     */
    public Query(int eventId, SensorNode startNode) {
        if (startNode == null) {
            throw new NullPointerException("StartNode is null");
        }
        else {
            route.add(startNode);
            currentSensorNode = startNode;
            this.eventId = eventId;
            shouldDie = false;
            timeAlive = 0;
            queryHasNotFoundARoute = true;
        }
    }

    /**
     * Returns the current node the query is on.
     * @return the current node the query is on.
     */
    public SensorNode getCurrentNode () {
        return currentSensorNode;
    }

    /**
     * Send back a Message consisting of the Event searched for to the node that spawned this Query.
     * The message will follow the path this Query has traveled in the Network.
     * @return a reference to the Message for Environment to use when simulating turns.
     */
    public Message sendResponse() {
        Collections.reverse(route);
        return new Message(eventInfo, route);
    }

    /**
     * handleAt method for query.
     * Checks if the query has arrived to the location where the event
     * it's looking for is located.
     */
    public void handleAt() {
        if (hasArrived()) {
            shouldDie = true;
        }
    }

    /**
     * Checks if query has arrived to the correct place.
     * (The place where the event is that the query is looking for).
     * @return true if the query has found the event, otherwise false
     */
    public boolean hasArrived() {
        eventInfo = currentSensorNode.getEvent(eventId);
        return eventInfo != null;
    }

    /**
     * Move method for query.
     * It starts by checking the nodes routingTable for next node to move to.
     * If there is no nextNode in routingTable, the query will move to a random neighbour of the node.
     * @return a list of nodes that should now be busy
     */
    public ArrayList<SensorNode> move(){
        if (this.queryHasNotFoundARoute) {
            timeAlive++;
        }
        ArrayList<SensorNode> busyNodes = new ArrayList<>(2);
        busyNodes.add(currentSensorNode);

        if(currentSensorNode.isInQueue(this)){
            return busyNodes;
        }

        SensorNode direction = currentSensorNode.getRoutingTable().getDirection(eventId);
        if (direction != null) {
            moveToTheNextNodeGivenByTheRoutingTable(direction, busyNodes);
        }
        else {
            moveRandomlyIfNoNextStepInRoutingTable(busyNodes);
        }
        return busyNodes;
    }


    /**
     * Moves the query to the next node given by the routingTable
     * @param direction the node the query moves to
     * @param busyNodes the nodes that are currently busy (during this iteration).
     */
    private void moveToTheNextNodeGivenByTheRoutingTable(SensorNode direction, ArrayList<SensorNode> busyNodes) {
        route.add(direction);
        currentSensorNode = direction;
        currentSensorNode.receiveMessenger(this);
        busyNodes.add(direction);
        this.queryHasNotFoundARoute = false;
    }

    /**
     * If there is no query does not get a node to move to from the routingTable,
     * the query will move randomly.
     * @param busyNodes the nodes that are currently busy (during this iteration).
     */
    private void moveRandomlyIfNoNextStepInRoutingTable(ArrayList<SensorNode> busyNodes) {
        List<SensorNode> validNeighbors = new ArrayList<>(currentSensorNode.getNeighbours().size());
        for (SensorNode neighbor : currentSensorNode.getNeighbours()) {
            if (neighbor != null) {
                validNeighbors.add(neighbor);
            }
        }
        onlyMoveIfTheNeighboursAreValid(busyNodes, validNeighbors);
    }

    /**
     * Checks that the neighbours of the nodes are valid and moves the query to a random valid neighbour
     * @param busyNodes the nodes that are currently busy (during this iteration).
     * @param validNeighbors a list of the valid neighbours.
     */
    private void onlyMoveIfTheNeighboursAreValid(ArrayList<SensorNode> busyNodes, List<SensorNode> validNeighbors) {
        if (!validNeighbors.isEmpty()) {
            int randomIndex = random.nextInt(validNeighbors.size());
            SensorNode nextNode = validNeighbors.get(randomIndex);
            route.add(nextNode);
            currentSensorNode = nextNode;
            currentSensorNode.receiveMessenger(this);
            busyNodes.add(nextNode);
        }
    }

    /**
     * Looks if query should stop moving around in the network.
     * @param amount maximum amount of time the query can be alive
     * @return true if query has reached the maximum time it can be alive, otherwise false
     */
    public boolean shouldDie(int amount) {
        return shouldDie || hasArrived() || timeAlive > amount;
    }
}
