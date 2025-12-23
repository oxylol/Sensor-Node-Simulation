import java.util.*;

/**
 * A SensorNode in a network. It detects Events, and occasionally spawns an Agent when events occurs.
 * SensorNode handles incoming messengers by adding them to an internal Queue,
 * which then can be handled when the node is instructed to do so.
 * A SensorNode can send, or receive one messenger per time-tick.
 */
public class SensorNode {
    private final Coordinate coordinate;
    private LinkedList<Messenger> messengerQueue;
    private RoutingTable routingTable;
    private Map<Integer, Event> eventMap;;
    private final List<SensorNode> neighbours;
    private int queriesSent;
    private boolean busy;
    private int neighbourCount;
    private static final Random random = new Random();
    private Integer pendingEventId = null;

    /**
     * Creates a new SensorNode with a Coordinate. Initialises all internal attributes.
     * @param coordinate the coordinate this node will have.
     * @param neighbourCount number of neighbouring nodes.
     */
    public SensorNode(Coordinate coordinate, int neighbourCount) {
        this.coordinate = coordinate;
        messengerQueue = new LinkedList<>();
        routingTable = new RoutingTable();
        eventMap = new HashMap<>();
        neighbours = new ArrayList<>();
        queriesSent = 0;
        busy = false;
        this.neighbourCount = neighbourCount;
    }

    /**
     * Call this node to detect an Event.
     * @param detectedEvent the Event.
     * @param probability the probability of an Agent spawning in response to the detected Event.
     * @return a reference to the spawned Agent. Null if no Agent was spawned.
     */
    public Agent detectEvent(Event detectedEvent, double probability) {
        int eventID = detectedEvent.getEventId();
        EventValue eventValue = new EventValue(0, this);

        eventMap.put(eventID, detectedEvent);
        routingTable.addRoute(eventID, eventValue);

        if (random.nextDouble() < probability) {
            return new Agent(this,neighbourCount);
        }
        return null;
    }

    /**
     * Adds the messenger to this SensorNodes internal Queue.
     * @param newMessenger the messenger to be received by this SensorNode.
     */
    public void receiveMessenger(Messenger newMessenger) {
        messengerQueue.addLast(newMessenger);
    }

    /**
     * Sets this node as busy, and processes the first messenger in the internal queue.
     */
    public void handleQueue() {
        if (busy) {
            return;
        }

        busy = true;
        Messenger messenger = messengerQueue.poll();
        if (messenger != null) {
            messenger.handleAt();
        }
    }

    /**
     * Set this node as idle.
     */
    public void setIdle() {
        busy = false;
    }

    /**
     * @return true if this SensorNode is busy, false if not.
     */
    public boolean isBusy() {
        return busy;
    }

    /**
     * Determines if a messenger is in this SensorNodes queue.
     * @param messenger the messenger to search for.
     * @return true if the messenger is in the queue. False if not.
     */
    public boolean isInQueue(Messenger messenger) {
        return messengerQueue.contains(messenger);
    }

    /**
     * Spawns a Query at this node and puts it first in the queue.
     * @param eventID the unique ID that the query will search for.
     * @return a reference to the Query.
     */
    public Query spawnQuery(int eventID) {
        Query newQuery;
        if (pendingEventId != null) {
            newQuery = new Query(pendingEventId, this);
        }
        else {
            newQuery = new Query(eventID, this);
            pendingEventId = eventID;
        }
        messengerQueue.addFirst(newQuery);
        queriesSent++;
        return newQuery;
    }

    /**
     * Returns the number of queries that has been sent to search for a specific event by this node.
     * @return the number of queries that has been sent.
     */
    public int getQueriesSent() {
        return queriesSent;
    }

    /**
     * Resets the number of queries sent from this SensorNode.
     */
    public void resetQueriesSent() {
        queriesSent = 0;
        pendingEventId = null;
    }

    public Integer getPendingEventId() {
        return pendingEventId;
    }

    /**
     * Returns a reference to this SensorNodes RoutingTable.
     * @return this SensorNodes RoutingTable.
     */
    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    /**
     * Returns this SensorNodes Coordinate in the Network.
     * @return the coordinate of this node.
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Returns a list that contains all SensorNodes within this nodes transmission radius.
     * @return all neighbours of the node.
     * @throws RuntimeException error if this node had no neighbours. (Should be added in Enviroment on creation)
     */
    public List<SensorNode> getNeighbours() {
        if (neighbours.isEmpty()) {
            throw new RuntimeException("Tried fetching neighbours from a node with no neighbours in it.");
        }
        return neighbours;
    }

    /**
     * Return an event that has been detected by this node.
     * @param eventId the unique eventID tied to the event.
     * @return the event tied to the eventID, otherwise null.
     */
    public Event getEvent(int eventId) {
        Event event = eventMap.get(eventId);
        if (event != null && routingTable.getDistance(eventId) == 0) {
            return event;
        }
        return null;
    }

    /**
     * Adds neighbours to this nodes internal neighbour list.
     * @param neighbours the list of all SensorNodes that are within this SensorNodes transmission radius.
     * @throws RuntimeException if trying to add neighbours to a node that already had neighbours.
     */
    public void addNeighbours(List<SensorNode> neighbours) {
        if (!this.neighbours.isEmpty()) {
            throw new RuntimeException("Tried adding neighbours to a node that already had neighbours.");
        }
        this.neighbours.addAll(neighbours);
    }

    /**
     * When comparing two SensorNodes we can compare its Coordinates.
     * @param obj object to be compared.
     * @return true if objects are equal, false if not.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SensorNode other = (SensorNode) obj;
        return coordinate.equals(other.coordinate); // Compare the Coordinates of the SensorNodes.
    }

    /**
     * Use the Coordinates hashCode.
     * @return this objects hash value based on its coordinate.
     */
    @Override
    public int hashCode() {
        return coordinate.hashCode();
    }
}
