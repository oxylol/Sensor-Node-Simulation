import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Represents a RoutingTable, which stores event routes identified by a unique event ID.
 * Each route is associated with an EventValue containing distance and direction information.
 * The RoutingTable supports adding, retrieving, and merging routes with other tables.
 */
public class RoutingTable {
    private HashMap<Integer, EventValue> routes;

    public RoutingTable() {
        routes = new HashMap<>(3334);
    }

    public RoutingTable(RoutingTable other) {
        this.routes = new HashMap<>(other.routes);
    }

    /**
     * Adds a new route to the routing table.
     *
     * @param eventID the ID of the event associated with the route
     * @param eventValue the EventValue containing distance and direction information for the event
     */
    public void addRoute(int eventID, EventValue eventValue) {
        routes.put(eventID, eventValue);
    }

    /**
     * Retrieves the route associated with the given event ID from the routing table.
     *
     * @param eventID the ID of the event for which the route is being retrieved
     * @return the EventValue associated with the specified event ID, or null if no route exists for the given ID
     */
    public EventValue getRoute(int eventID) {
        return routes.get(eventID);
    }

    /**
     * Retrieves the direction associated with the specified event ID from the routing table.
     * The direction is extracted from the EventValue object stored in the routing table for the given event ID.
     *
     * @param eventID the ID of the event for which the direction is being retrieved
     * @return the direction of the event as an integer, or -1 if no route exists for the given event ID
     */
    public SensorNode getDirection(int eventID){
        if (getRoute(eventID) != null) {
            return getRoute(eventID).getDirection();
        }
        else return null;
    }

    /**
     * Returns the distance to a specified event, null if the event is not found.
     * @param eventID the events ID to search for
     * @return the distance to the event that is being searched for. Returns null if the event ID is not found.
     */
    public int getDistance(int eventID){
        return getRoute(eventID).getDistance();
    }

    /**
     * Returns all routes in the routing table.
     *
     * @return a Map containing all routes with their event IDs as keys
     * and EventValue as values
     */
    public Map<Integer, EventValue> getAllRoutes() {
        return routes;
    }


    /**
     * Increments the distance of every stored route by 1, and sets
     * each route’s direction to the given newDirection.
     * Instead of copying the entire map, this iterates over routes.entrySet()
     * and replaces each EventValue exactly once.
     *
     * @param newDirection the direction to set for every event
     */
    /*
    public void incrementAllDistancesAndSetDirection(int newDirection) {
        // Iterate over the actual HashMap’s entrySet. This does not create a copy.
        for (Map.Entry<Integer, EventValue> entry : routes.entrySet()) {
            EventValue oldVal = entry.getValue();
            int updatedDistance = oldVal.getDistance() + 1;
            // overwrite with a new EventValue (distance+1, newDirection)
            entry.setValue(new EventValue(updatedDistance, newDirection));
        }
    }
    */

    /**
     * Merges the current RoutingTable with another RoutingTable by comparing distances.
     * For each event ID, keeps the route with the lowest distance.
     * Both tables will be updated to have the same routes after merging.
     *
     * @param other the other RoutingTable to merge with
     */
    public void mergeTables(RoutingTable other) {
        if (other.routes == null) {
            other.routes = new HashMap<>(this.routes);
            return;
        }

        // BiFunction for choosing the best value
        BiFunction<EventValue, EventValue, EventValue> chooseBest =
                (v1, v2) -> v2.compareTo(v1) ? v2 : v1;

        // Merge in both directions
        other.routes.forEach((k, v) -> this.routes.merge(k, v, chooseBest));
        this.routes.forEach((k, v) -> other.routes.merge(k, v, chooseBest));
    }

    /**
     * For testing purposes
     * @param obj the object to compare
     * @return true if objects are equal, false if not.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; // Samma referens
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false; // Null eller annan typ
        }

        RoutingTable other = (RoutingTable) obj;
        return routes.equals(other.routes); // Jämför innehållet i HashMap
    }

    /**
     * According to "Java Contract" of implementing Equals.
     * @return this objects hashcode.
     */
    @Override
    public int hashCode() {
        return routes.hashCode();
    }
}