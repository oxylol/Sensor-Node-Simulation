/**
 * Represents an immutable Event with distance and direction values
 */
public final class EventValue {
    private int distance;
    private SensorNode direction;

    public EventValue(int distance, SensorNode direction) {
        if (distance < 0) {
            throw new IllegalArgumentException("EventValue got created with negative values");
        }
        else {
            this.direction = direction;
            this.distance = distance;
        }
    }

    /**
     * Returns the distance value, extracted from the packed integer.
     *
     * @return the distance (lower 28 bits)
     */
    public int getDistance() {
        // Extract the lower 28 bits for the distance
        return distance;
    }

    /**
     * Returns the direction value, extracted from the packed integer.
     *
     * @return the direction (upper 4 bits)
     */
    public SensorNode getDirection() {
        // Extract the upper 4 bits for the direction
        return direction;
    }

    /**
     *
     * @param newDistance
     */
    public void setDistance(int newDistance) {
        this.distance = newDistance;
    }

    /**
     * Compares this EventValue with another based solely on distance.
     * Direction is not considered in the comparison.
     *
     * @param other the EventValue to compare with
     * @return true if this EventValue's distance is less than the other's distance
     */
    public boolean compareTo(EventValue other) {
        if (other == null) return true;
        return this.getDistance() < other.getDistance();
    }

    /**
     * For testing purposes.
     * @param obj object to compare.
     * @return true if obj is equal to this, false if not.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        EventValue other = (EventValue) obj;
        return this.distance == other.distance && this.direction == other.direction;
    }

    /**
     * @return this objects hash value.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(distance);
    }
}