import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main class that sets up and runs the sensor network simulation.
 * The simulation consists of a 50x50 grid of sensor nodes with the following parameters:
 * - 10,000 time steps
 * - 0.01% chance of event detection per node per time step
 * - 50% chance of agent message generation per detected event
 * - Agent messages can be forwarded a maximum of 50 times
 * - Queries can be forwarded a maximum of 45 steps
 * - Random queries from 4 predefined nodes every 400 time steps
 * - Communication distance between nodes <= 15 length units
 */
public class Main {

    // Simulation parameters as specified in the requirements
    private static final int GRID_SIZE = 50;
    private static final int TIME_STEPS = 10000;
    private static final double EVENT_DETECTION_PROBABILITY = 0.0001; // 0.01%
    private static final double AGENT_MESSAGE_PROBABILITY = 0.5; // 50%
    private static final int MAX_AGENT_FORWARDS = 50;
    private static final int MAX_QUERY_FORWARDS = 45;
    private static final int QUERY_INTERVAL = 400;
    private static final int NODE_COMMUNICATION_DISTANCE = 15;
    private static final int NODE_SPACING = 10; // Distance between nodes

    public static void main(String[] args) {
        try {
            // Generate network layout file if it doesn't exist
            File layoutFile = new File("layout.txt");
            if (!layoutFile.exists()) {
                generateNetworkLayout(layoutFile);
            }

            // Read the network layout from the file
            Scanner scanner = new Scanner(layoutFile);
            Network network = new Network(scanner, NODE_SPACING, NODE_COMMUNICATION_DISTANCE);
            scanner.close();

            // Create and run the environment simulation
            System.out.println("Starting simulation with the following parameters:");
            System.out.println("- Grid size: " + GRID_SIZE + "x" + GRID_SIZE);
            System.out.println("- Time steps: " + TIME_STEPS);
            System.out.println("- Event detection probability: " + EVENT_DETECTION_PROBABILITY * 100 + "%");
            System.out.println("- Agent message probability: " + AGENT_MESSAGE_PROBABILITY * 100 + "%");
            System.out.println("- Max agent forwards: " + MAX_AGENT_FORWARDS);
            System.out.println("- Max query forwards: " + MAX_QUERY_FORWARDS);
            System.out.println("- Query interval: " + QUERY_INTERVAL);
            System.out.println("- Node communication distance: " + NODE_COMMUNICATION_DISTANCE);

            // Initialize and run the environment with the network and time steps
            Environment environment = new Environment(network, TIME_STEPS,EVENT_DETECTION_PROBABILITY,
                    AGENT_MESSAGE_PROBABILITY,MAX_AGENT_FORWARDS,MAX_QUERY_FORWARDS,QUERY_INTERVAL);

            System.out.println("Simulation completed successfully.");

        } catch (FileNotFoundException e) {
            System.err.println("Error reading layout file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error generating layout file: " + e.getMessage());
        }
    }

    /**
     * Generates and writes a networkLayout to a file
     * with the size depending on a set "grid_size".
     *
     * @param file the file where the networkLayout will be written to
     * @throws IOException If there is an error writing to the file
     */
    private static void generateNetworkLayout(File file) throws IOException {
        FileWriter writer = new FileWriter(file);

        // Calculate the total number of nodes in the grid
        int totalNodes = GRID_SIZE * GRID_SIZE;
        writer.write(Integer.toString(totalNodes) + "\n");

        // Write each node's coordinates to the file
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                writer.write(x + "," + y + "\n");
            }
        }

        writer.close();
        System.out.println("Generated network layout file with " + totalNodes + " nodes.");
    }

    /**
     * Alternative method to create a smaller test network for debugging purposes.
     * This creates a smaller grid (e.g., 5x5) which is easier to visualize and debug.
     *
     * @param file The file to write the layout to
     * @throws IOException If there is an error writing to the file
     */
    private static void generateTestNetworkLayout(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        int testGridSize = 5; // Smaller grid for testing
        int totalNodes = testGridSize * testGridSize;

        writer.write(Integer.toString(totalNodes) + "\n");

        for (int x = 0; x < testGridSize; x++) {
            for (int y = 0; y < testGridSize; y++) {
                writer.write(x + "," + y + "\n");
            }
        }

        writer.close();
        System.out.println("Generated test network layout file with " + totalNodes + " nodes.");
    }
}