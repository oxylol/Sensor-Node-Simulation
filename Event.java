/**
 * A class that represents an event in the network.
 * The event contains a unique ID, X and Y coordinates in the network
 * and its time of birth in the network.
 */
public class Event {
    private int eventId;
    private Coordinate cord;
    private int time;
    private static int lastAssignedId = 0;

    /**
     * Creates an event at the given coordinate
     * Saves the time of birth in the simulation.
     * @param coord The position the event should happen at.
     * @param timeOfBirth Its time of birth.
     */
    public Event(Coordinate coord, int timeOfBirth) {
        lastAssignedId ++;
        eventId = lastAssignedId;
        cord = coord;
        time = timeOfBirth;
    }

    /**
     * @return the event ID for the event.
     */
    public int getEventId() {
        return this.eventId;
    }

    /**
     * @return The events time of birth.
     */
    public int getTime() {
        return this.time;
    }

    /**
     * @return the events coordinate.
     */
    public Coordinate getCord() {
        return this.cord;
    }

    /**
     * Converts the event's id, coordinate and time of birth to a string.
     * @return A string with event ID, coordinates and time of birth.
     */
    public String toString() {
        return "\nEventID: " + eventId + "\nEvent location: " + cord + "\nOccurred at turn: " + time;
    }

    /**
     * Compare two Event objects to see if they're equal.
     * @param obj the object to compare.
     * @return true if object is equal to this one. False if not.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Event other = (Event) obj;
        return eventId == other.eventId;
    }

    /**
     * @return this objects hashcode.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(eventId);
    }
}
