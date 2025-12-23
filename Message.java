import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A class representing a message in the system.
 * A message is an answer to a query sent by a node.
 * When the message returns to the node that sent the query, an Event will be printed to the console.
 */
public class Message extends Messenger {
    private final Event event;
    private List<SensorNode> route;
    private SensorNode currentSensorNode;
    private static HashSet<Integer> eventIds = new HashSet<>();

    /**
     * Initialise a Message object.
     * @param event The message to be delivered to the node that spawned the query.
     * @param route The route to follow.
     */
    public Message(Event event, List<SensorNode> route) {
        this.event = event;
        this.route = new ArrayList<>(route.subList(1, route.size()));
        currentSensorNode = route.get(0);
    }

    /**
     * Tells when a message has arrived to the node that sent
     * the query related to this message.
     * @return true if the message has arrived at its destination, false otherwise
     */
    public boolean hasArrived() {
        return route.isEmpty();
    }

    /**
     * Moves the message to next node (by using the route it got from query)
     * @return the list of the nodes that will be busy.
     */
    public ArrayList<SensorNode> move() {
        ArrayList<SensorNode> busyNodes = new ArrayList<>(2);
        busyNodes.add(currentSensorNode);

        if (currentSensorNode.isInQueue(this)) {
            return busyNodes;
        }

        IfTheRouteHasANextNodeMoveThere(busyNodes);

        return busyNodes;
    }

    /**
     * Moves the message to the next node in the route, if the route is not empty.
     * @param busyNodes the nodes that are currently busy (during this iteration).
     */
    private void IfTheRouteHasANextNodeMoveThere(ArrayList<SensorNode> busyNodes) {
        if (!route.isEmpty()) {
            SensorNode nextNode = route.remove(0);
            busyNodes.add(nextNode);
            currentSensorNode = nextNode;
            currentSensorNode.receiveMessenger(this);
        }
    }

    /**
     * If this message has reached its end destination it is no longer serves a purpose in the system.
     * Call this method to see if this message can be discarded.
     * @param amount placeholder.
     * @return true if this object is meant to "die". False if not.
     */
    @Override
    public boolean shouldDie(int amount) {
        return hasArrived();
    }

    /**
     * Prints the Event stored in this object when it has reached its destination.
     */
    public void handleAt() {
        if (hasArrived() && !eventIds.contains(event.getEventId())) {
            eventIds.add(event.getEventId());
            printEventInformation();
        }
    }

    /**
     * Prints the information of the event.
     */
    private void printEventInformation() {
        StringBuilder message = new StringBuilder(100);
        message.append("Message has arrived. ").append("Place where event occurred: ").append(event.getCord()).append(". ").append("Time event occurred: ").append(event.getTime()).append(". ").append("Event Id: ").append(event.getEventId());
        System.out.println(message);
    }

    /**
     * For testing purposes to be able to track the message in a Network.
     * @return the SensorNode where this message is located.
     */
    public SensorNode getSensorNode() {
        return currentSensorNode;
    }

    /**
     * For testing purposes.
     * @return the eventID of the event stored in this Message.
     */
    public int getEventID() {
        return event.getEventId();
    }
}
