import java.util.*;

/**
 * This class acts as the environment for the network. Through the methods in the class,
 * It handles the simulation of the network, which includes creating and managing different
 * entities in the network such as events, agents, query's and responses.
 *
 * The methods in the class include creating an environment, simulating the environment,
 * creating a list of random nodes from the environment, creating a list of all nodes in a network,
 * generate events in the network, calculate the number of events in the network,
 * handle quires, agents and responses.
 */
public class Environment {
    private int time;
    private Set<SensorNode> activeNodes;
    private List<Messenger> activeMessengers;
    private List<Event> events;
    private HashSet<Integer> eventIds = new HashSet<>();
    private HashSet<SensorNode> currentNodesWithEventsActive = new HashSet<>();

    // Timing statistics
    private long totalInitTime = 0;
    private long totalEventGenTime = 0;
    private long totalQueryHandlingTime = 0;
    private long totalNodeProcessingTime = 0;
    private long totalMessengerProcessingTime = 0;
    private int iterationCount = 0;

    //total stats
    private int totalEvents = 0;
    private int totalQueries = 0;
    private int totalAgents = 0;
    private int totalMessages = 0;

    /**
     * This method acts as the environment for the network.
     * It helps simulate events, agents and queries in the network
     * by being given the probabilities of those entities happening by the user.
     * The user also provides how many iterations the network should run and
     * how long interval between each query as well as their maximum response time.
     *
     * @param network The network that the user wants to use for the simulation.
     * @param iterations How many iterations the simulation should perform.
     * @param eventDetectionProbability The probability for an event to be detected.
     * @param agentSpawnProbability  The probability for an agent to spawn.
     * @param maxAgentForwards The amount of turns an agent can exist before dying.
     * @param maxQueryResponseTime The max amount of turns a query can exist before dying.
     * @param queryInterval A given intervals of turns. After the interval is finished a new query is generated.
     */
    public Environment(Network network, int iterations, double eventDetectionProbability, double agentSpawnProbability,
                       int maxAgentForwards, int maxQueryResponseTime, int queryInterval) {
        long startTotalTime = System.nanoTime();

        time = 0;
        activeNodes = Collections.newSetFromMap(new HashMap<>());
        activeMessengers = new ArrayList<>();
        events = new ArrayList<>();

        long startCollectTime = System.nanoTime();
        List<SensorNode> allNodes = collectAllNodes(network);
        long collectTime = System.nanoTime() - startCollectTime;
        System.out.println("Time to collect all nodes: " + collectTime / 1_000_000 + " ms");

        long startRandomTime = System.nanoTime();
        List<SensorNode> randomNodes = selectRandomNodes(allNodes, Math.min(4, allNodes.size()));
        long randomTime = System.nanoTime() - startRandomTime;
        System.out.println("Time to select random nodes: " + randomTime / 1_000_000 + " ms");

        totalInitTime = collectTime + randomTime;

        // Run the simulation
        long simStartTime = System.nanoTime();
        runSimulation(network, iterations, allNodes, randomNodes, eventDetectionProbability, queryInterval,
                maxQueryResponseTime, maxAgentForwards,agentSpawnProbability);
        long simTime = System.nanoTime() - simStartTime;
        System.out.println("Total simulation time: " + simTime / 1_000_000 + " ms");

        // Print summary statistics

        System.out.println("\n----- PERFORMANCE SUMMARY -----");
        System.out.println("Initialization time: " + totalInitTime / 1_000_000 + " ms");
        System.out.println("Average per iteration:");
        System.out.println("  Event generation: " + (totalEventGenTime / iterationCount) / 1_000_000.0 + " ms");
        System.out.println("  Query handling: " + (totalQueryHandlingTime / iterationCount) / 1_000_000.0 + " ms");
        System.out.println("  Node processing: " + (totalNodeProcessingTime / iterationCount) / 1_000_000.0 + " ms");
        System.out.println("  Messenger processing: " + (totalMessengerProcessingTime / iterationCount) / 1_000_000.0 + " ms");
        System.out.println("Total time: " + (System.nanoTime() - startTotalTime) / 1_000_000 + " ms");
        System.out.println("Total events: " + totalEvents);
        System.out.println("Total queries: " + totalQueries);
        System.out.println("Total agents: " + totalAgents);
        System.out.println("Total messages received, (not printed): " + totalMessages);
        System.out.println("----- END PERFORMANCE SUMMARY -----\n");
    }

    /**
     * Collects all the nodes in a network and returns them in a list.
     *
     * @param network The network of nodes the user wants to use.
     * @return A list with all the node in the given network.
     */
    private List<SensorNode> collectAllNodes(Network network) {
        List<SensorNode> nodes = new ArrayList<>();

        for (int x = 0; x < network.getMaxX(); x++) {
            for (int y = 0; y < network.getMaxY(); y++) {
                SensorNode node = network.getNode(new Coordinate(x, y));
                if (node != null) {
                    nodes.add(node);
                }
            }
        }

        return nodes;
    }

    /**
     * Given a list of nodes and the lists size, return
     * a new list with randomly picked nodes from the old list.
     *
     * @param allNodes A list of nodes the user wants to pick from.
     * @param count The amount of nodes in the list.
     * @return A list of randomly picked nodes from the given node list.
     */
    private List<SensorNode> selectRandomNodes(List<SensorNode> allNodes, int count) {
        if (allNodes.isEmpty() || count <= 0) {
            return new ArrayList<>();
        }

        List<SensorNode> nodesCopy = new ArrayList<>(allNodes);
        List<SensorNode> selectedNodes = new ArrayList<>(count);

        // Fisher-Yates shuffle and take first 'count' elements
        Random random = new Random();
        for (int i = 0; i < count && i < nodesCopy.size(); i++) {
            int randomIndex = i + random.nextInt(nodesCopy.size() - i);
            // Swap elements
            SensorNode temp = nodesCopy.get(i);
            nodesCopy.set(i, nodesCopy.get(randomIndex));
            nodesCopy.set(randomIndex, temp);

            selectedNodes.add(nodesCopy.get(i));
        }

        return selectedNodes;
    }

    /**
     * This method acts as the main simulation for the network.
     * The simulations runs for the given iterations amount.
     * It also generates events, agents and query's with the given probabilities.
     *
     * @param network The network the user wants to simulate.
     * @param iterations How many iterations the simulation should perform.
     * @param allNodes A list of all the nodes in the given network.
     * @param randomNodes A list of randomly picked nodes in the network.
     * @param probabilityPerNode The probability for each node in a network to detect a event.
     * @param queryInterval A given intervals of turns. After the interval is finished a new query is generated.
     * @param queryMaxResponseTime The maximum amount of time a query can exist for.
     * @param agentMaxForwards The maximum amount of steps an agent can exist.
     * @param agentSpawnProbability The probability to spawn an agent in the network after an event.
     */
    private void runSimulation(Network network, int iterations, List<SensorNode> allNodes,
                               List<SensorNode> randomNodes, double probabilityPerNode, int queryInterval,
                               int queryMaxResponseTime, int agentMaxForwards,double agentSpawnProbability) {
        Random random = new Random();
        int totalNodes = network.getTotalNodes();

        for (int i = 0; i < iterations; i++) {
            iterationCount++;

            if (i % 1000 == 0) {
                System.out.println("Iteration " + i + " of " + iterations);
            }

            long startEventGenTime = System.nanoTime();
            generateEventsForEachIteration(allNodes, random, totalNodes, probabilityPerNode,agentSpawnProbability);
            long eventGenTime = System.nanoTime() - startEventGenTime;
            totalEventGenTime += eventGenTime;

            // Handle periodic query generation
            long startQueryTime = System.nanoTime();
            if (i % queryInterval == 0) {
                handlePeriodicQueries(randomNodes, true);
            }

            if (i % (queryMaxResponseTime * 8) == 0) {
                handlePeriodicQueries(randomNodes, false);
            }
            long queryTime = System.nanoTime() - startQueryTime;
            totalQueryHandlingTime += queryTime;

            // Process active nodes
            long startNodeTime = System.nanoTime();
            if (!activeNodes.isEmpty()) {
                // Create a copy to avoid concurrent modification
                Set<SensorNode> nodesToProcess = new HashSet<>(activeNodes);
                nodesToProcess.parallelStream().forEach(node -> {
                    node.handleQueue();
                    node.setIdle();
                });
                activeNodes.clear();
            }
            long nodeTime = System.nanoTime() - startNodeTime;
            totalNodeProcessingTime += nodeTime;

            long startMessengerTime = System.nanoTime();
            processMessengers(agentMaxForwards, queryMaxResponseTime);
            long messengerTime = System.nanoTime() - startMessengerTime;
            totalMessengerProcessingTime += messengerTime;

            currentNodesWithEventsActive.clear();
            time++;
        }
    }

    /**
     *This method generates events in the effected network.
     * To generate events in a network the following parameters are needed:
     * a list of all nodes in a network, the total amount of nodes in the same network,
     * the probability of an event spawning per node and the probability of an agent spawning.
     * Depending on the total amount of nodes in the network
     * is more or less than 1000 nodes, one of two different algorithms may be chosen.
     *
     * @param allNodes A list with all the nodes in a network.
     * @param random A random number.
     * @param totalNodes The total amounts of nodes in a network.
     * @param probabilityPerNode The probability to create an event per node.
     * @param agentSpawnProbability The probability to create an agent in the network.
     */
    //Parallelize event generation for large networks
    private void generateEventsForEachIteration(List<SensorNode> allNodes, Random random, int totalNodes,
                                                double probabilityPerNode , double agentSpawnProbability) {
            int numEventsThisIteration = calculateNumberOfEvents(random, totalNodes, probabilityPerNode);
            for (int j = 0; j < numEventsThisIteration; j++) {
                generateEventsForNetworks(allNodes, random, agentSpawnProbability);
            }
    }

    /**
     * Generates events for the relevant network.
     * @param allNodes A list of all nodes in network.
     * @param random
     * @param agentSpawnProbability The probability to create an event per node.
     */
    private void generateEventsForNetworks(List<SensorNode> allNodes, Random random, double agentSpawnProbability) {
        if (!allNodes.isEmpty()) {
            SensorNode node;
            do {
                int randomIndex = random.nextInt(allNodes.size());
                node = allNodes.get(randomIndex);
            } while (currentNodesWithEventsActive.contains(node));

            Event newEvent = new Event(node.getCoordinate(), time);
            events.add(newEvent);
            totalEvents++;
            currentNodesWithEventsActive.add(node);

            Agent spawn = node.detectEvent(newEvent, agentSpawnProbability);
            if (spawn != null) {
                totalAgents++;
                activeMessengers.add(spawn);
            }
        }
    }

    /**
     * This method calculates the number of events in a given network
     * with a given amount of nodes in the network
     * and the probability of an event spawing on a node.
     * A random number is also required.
     *
     * @param random A random number.
     * @param totalNodes The total amount of nodes in the network.
     * @param probabilityPerNode The chance of an event spawning per node in the network.
     * @return The amount of events in the network.
     */
    private int calculateNumberOfEvents(Random random, int totalNodes, double probabilityPerNode) {
        //Binomial approximation for large networks
        if (totalNodes > 100) {
            // For large n and small p, binomial distribution is approximated by Poisson
            double lambda = totalNodes * probabilityPerNode;
            return (int) poissonSample(random, lambda);
        } else {
            // For smaller networks, use explicit Bernoulli trials
            int count = 0;
            for (int p = 0; p < totalNodes; p++) {
                if (random.nextDouble() < probabilityPerNode) {
                    count++;
                }
            }
            return count;
        }
    }

    /**
     * Helper method to sample from Poisson distribution
     *
     * @param random A random number.
     * @param lambda the mean rate of which an event can occur.
     * @return returns a random number that follows the Poisson distribution.
     */
    private double poissonSample(Random random, double lambda) {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);

        return k - 1;
    }

    /**
     * Given a list of random nodes in a network and a boolean flag.
     * Reset the count of how many queries a node have sent out if the flag is true.
     * A random node is then picked from the list. If the flag is true,
     * create another query and increment that nodes query count with one.
     * If the flag is false don't do anything.
     *
     * @param randomNodes A list of random nodes taken from a network.
     * @param resetQueries A boolean flag to reset the send out queries in the network.
     */
    private void handlePeriodicQueries(List<SensorNode> randomNodes, boolean resetQueries) {
        for (SensorNode node : randomNodes) {
            if (resetQueries) {
                node.resetQueriesSent();
            }

            // If this node already has 2 attempts in flight (queriesSent >= 2), skip it.
            if (node.getQueriesSent() >= 2) {
                continue;
            }

            // If node.pendingEventId is null, pick a new random eventId; otherwise reuse it.
            int candidateId = 0;
            Random random = new Random();
            if (node.getPendingEventId() == null) {
                if (!events.isEmpty()) {
                    candidateId = events.get(random.nextInt(events.size())).getEventId();
                }
            } else {
                candidateId = node.getPendingEventId();
            }

            // If candidateId is zero or has already been answered, skip spawning a new query.
            if (candidateId == 0 || eventIds.contains(candidateId)) {
                continue;
            }

            totalQueries++;
            activeMessengers.add(node.spawnQuery(candidateId));
        }
    }

    /**
     * Given the maximum amount of step an agent can exist for
     * and the maximum response time allowed for a query in a network.
     * Remove the messenger entities that should be removed.
     * If a query has arrived to its destination, transform it to a response.
     * After the relevant entities have been updated, move all alive entities.
     *
     * @param agentMaxForwards The maximum amount of steps an agent can exist.
     * @param queryMaxResponseTime The maximum amount of time a query can exist for.
     */
    private void processMessengers(int agentMaxForwards, int queryMaxResponseTime) {
        List<Messenger> messengersToRemove = new ArrayList<>();
        List<Messenger> messengersToAdd = new ArrayList<>();

        for (Iterator<Messenger> it = activeMessengers.iterator(); it.hasNext();) {
            Messenger messenger = it.next();

            if (messenger instanceof Query) {
                Query query = (Query) messenger;
                if (query.shouldDie(queryMaxResponseTime)) {
                    it.remove();
                    if (query.hasArrived()) {
                        Message message = query.sendResponse();
                        messengersToAdd.add(message);
                        eventIds.add(message.getEventID());
                        totalMessages++;
                    }
                }
            } else {// Agent or other Messenger type
                if (messenger.shouldDie(agentMaxForwards)) {
                    if (messenger instanceof Message) {
                        Message message = (Message) messenger;
                    }
                    it.remove();
                }
            }

        }

        activeMessengers.removeAll(messengersToRemove);
        activeMessengers.addAll(messengersToAdd);

        for (Messenger messenger : activeMessengers) {
            activeNodes.addAll(messenger.move());
        }
    }
}