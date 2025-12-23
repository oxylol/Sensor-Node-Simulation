import java.util.*;

/**
 * The Agent class represents a mobile entity that explores a network of sensor nodes,
 * maintains a routing table, and records its movement history. The agent is initially
 * spawned at a specified node and is capable of merging routing tables, moving between
 * nodes, and tracking the number of steps it has taken.
 * This class extends the abstract Messenger class and provides implementations for
 * movement and routing table merge functions.
 */
public class Agent extends Messenger {
    private RoutingTable table;
    private int stepCount = 0;
    private HashSet<Coordinate> previousCoordinates;
    private SensorNode currentSensorNode;
    private SensorNode previous;
    private int lastDirection;
    private int neighbourCount;

    /**
     * Constructs an Agent object with the given spawn node.
     * Initializes the agent's routing table, current node,
     * and previous coordinates based on the provided spawn node.
     *
     * @param spawnNode the SensorNode where the agent is initially spawned.
     *                  The spawn node's coordinate is added to the agent's
     *                  list of visited coordinates, and its routing table is
     *                  used to initialize the agent's routing table.
     * @param neighbourCount The amount of neighbours nodes for one node.
     */
    public Agent(SensorNode spawnNode, int neighbourCount){
        this.previousCoordinates = new HashSet<>(51);
        this.previousCoordinates.add(spawnNode.getCoordinate());
        currentSensorNode = spawnNode;
        this.table = new RoutingTable(spawnNode.getRoutingTable());
        this.lastDirection = 0;
        this.neighbourCount = neighbourCount;
        this.previous = currentSensorNode;
    }

    /**
     * handleAt method for agent.
     * Merges the agents routingTable with the routingTable
     * from the sensorNode its standing on.
     */
    @Override
    public void handleAt() {
        currentSensorNode.getRoutingTable().mergeTables(this.table);
    }

    /**
     * Moves the agent to a new sensor node and returns a list of nodes that are now busy.
     * The movement considers neighboring nodes and avoids revisiting previously visited coordinates.
     * If no valid move is available, it defaults to the first neighboring node.
     * Updates the agent's current position and tracking of previous coordinates.
     *
     * @return a list of sensor nodes that are now busy as a result of the movement
     */
    public ArrayList<SensorNode> move() {
        // Check if already in queue
        if(currentSensorNode.isInQueue(this)){
            ArrayList<SensorNode> list = new ArrayList<>();
            list.add(currentSensorNode);
            return list;
        }

        // Create result list with current node
        ArrayList<SensorNode> busyNodes = new ArrayList<>();
        busyNodes.add(currentSensorNode);

        List<SensorNode> neighbors = currentSensorNode.getNeighbours();
        ArrayList<SensorNode> validMoves = new ArrayList<>();
        ArrayList<SensorNode> noNullMoves = new ArrayList<>();

        // Get valid moves in one pass (non-null nodes not previously visited)
        for (SensorNode neighbor : neighbors) {
            if (neighbor != null) {
                noNullMoves.add(neighbor);
            }
        }
        
        for (SensorNode move : noNullMoves) {
            if (!previousCoordinates.contains(move.getCoordinate())) {
                validMoves.add(move);
            }
        }

        // Handle case when no valid moves are available
        if (validMoves.isEmpty() && !neighbors.isEmpty()) {
            SensorNode fallbackNode = neighbors.get(0);
            if (fallbackNode != null) {
                validMoves.add(fallbackNode);
            }
        }

        // Only proceed if there are valid moves available
        if (!validMoves.isEmpty()) {
            moveToARandomNode(validMoves, neighbors, busyNodes);
        }

        return busyNodes;
    }

    private void moveToARandomNode(ArrayList<SensorNode> validMoves, List<SensorNode> neighbors, ArrayList<SensorNode> busyNodes) {
        Random random = new Random();
        SensorNode nextNode = validMoves.get(random.nextInt(validMoves.size()));

        lastDirection = (neighbors.indexOf(nextNode));
        previous = currentSensorNode;
        currentSensorNode = nextNode;
        currentSensorNode.receiveMessenger(this);
        previousCoordinates.add(nextNode.getCoordinate());
        busyNodes.add(nextNode);

        updateRoutingTable();
        stepCount++;
    }

    /**
     * Returns either true or false depending on if
     * the agent has taken more or less than the maximum amount of steps.
     *
     * @param amount The maximum steps an agent can take before dying.
     * @return Returns false if the agent has taken
     *         less then the maximum allowed steps else returns true.
     */
    @Override
    public boolean shouldDie(int amount) {
        return stepCount>amount;
    }

    /**
     * Updates the agents routing table.
     */
    private void updateRoutingTable() {
        HashMap<Integer, EventValue> routes = (HashMap<Integer, EventValue>) table.getAllRoutes();
        routes.replaceAll((eventId, oldValue) ->
                new EventValue(oldValue.getDistance() + 1, this.previous)
        );
    }

    /**
     * Returns the number of steps the agent has taken.
     *
     * @return the count of steps as an integer.
     */
    public int getStepCount() {
        return stepCount;
    }

    /**
     * @return The agents routing table.
     */
    public RoutingTable getRoutingTable() {
        return table;
    }

    /**
     * @return The current node that the agents is at.
     */
    public SensorNode getSensorNode() {
        return currentSensorNode;
    }

    /**
     * Returns the node that was previously visited by this Agent.
     * @return this Agents previous node.
     */
    public SensorNode getPreviousNode() {
        return previous;
    }

    /**
     * Manually moves this Agent. Meant for testing purposes.
     * @param target node to move to.
     * @return the list of active nodes.
     */
    ArrayList<SensorNode> targetMove(SensorNode target) {
        ArrayList<SensorNode> busyNodes = new ArrayList<>();
        busyNodes.add(currentSensorNode);

        if (currentSensorNode.isInQueue(this)) {
            return busyNodes;
        }

        List<SensorNode> neighbors = currentSensorNode.getNeighbours();

        lastDirection = neighbors.indexOf(target);
        previous = currentSensorNode;
        currentSensorNode = target;
        currentSensorNode.receiveMessenger(this);
        previousCoordinates.add(currentSensorNode.getCoordinate());
        busyNodes.add(currentSensorNode);

        updateRoutingTable();
        stepCount++;

        return busyNodes;
    }
}