# Sensor Network Simulation

A Java-based distributed computing application that simulates a network of sensor nodes communicating through mobile agents, queries, and messages. This simulator models how information propagates through large-scale sensor networks using autonomous agent exploration and intelligent query routing.

## Overview

This project simulates a sophisticated distributed sensor network where:
- **Sensor nodes** detect random environmental events across a spatial grid
- **Mobile agents** autonomously explore the network and build routing tables
- **Queries** search for specific events using learned routes or random walks
- **Messages** return event information back to query originators

The simulation is designed to study distributed information discovery in large-scale IoT and sensor network scenarios.

## Features

- **Scalable Network Topology**: Configurable grid-based sensor network (default: 50×50 = 2,500 nodes)
- **Probabilistic Event Generation**: Events occur randomly based on configurable probabilities
- **Autonomous Agent Exploration**: Mobile agents traverse the network and share routing information
- **Intelligent Query Routing**: Queries leverage routing tables or use random walk when no route exists
- **Distance-Based Communication**: Nodes communicate only within a defined radius
- **Performance Instrumentation**: Built-in timing and metrics tracking
- **Parallel Processing**: Uses Java parallel streams for efficient simulation

## Simulation Parameters

Default configuration (configurable in `Main.java`):

| Parameter | Default Value | Description |
|-----------|---------------|-------------|
| Grid Size | 50×50 | Network dimensions (2,500 nodes) |
| Time Steps | 10,000 | Number of simulation iterations |
| Event Detection Probability | 0.01% | Chance per node per timestep |
| Agent Spawn Probability | 50% | Chance to spawn agent when event detected |
| Max Agent Forwards | 50 | Maximum hops for agent exploration |
| Max Query Response Time | 45 | Steps before query times out |
| Query Generation Interval | 400 | Timesteps between query batches |
| Communication Distance | 15 | Maximum distance for node communication |
| Node Spacing | 10 | Distance between adjacent nodes |

## Architecture

### Core Components

```
Main.java              - Entry point and simulation orchestration
Environment.java       - Simulation engine managing time steps and events
Network.java           - Network topology and neighbor relationships
SensorNode.java        - Individual sensor nodes with event detection
Agent.java             - Mobile explorers that build routing tables
Query.java             - Search requests for specific events
Message.java           - Event information responses
RoutingTable.java      - Stores routes to known events
Event.java             - Detected environmental events
Coordinate.java        - 2D spatial positioning
```

### Simulation Flow

1. **Event Generation**: Events are randomly generated at nodes based on probabilistic distribution
2. **Agent Deployment**: When an event is detected, an agent may spawn and explore neighboring nodes
3. **Route Building**: Agents maintain and merge routing tables showing paths to known events
4. **Query Distribution**: Queries are periodically sent from predefined nodes to search for events
5. **Intelligent Routing**: Queries follow routing table paths or move randomly if no route exists
6. **Response Messages**: When a query finds an event, it generates a response message
7. **Return Path**: Messages follow the reverse path back to the originating node

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- JUnit 5 (Jupiter) for running tests
- Maven or Gradle (recommended)

### Running the Simulation

1. **Generate Network Layout**:
   ```bash
   java Main
   ```
   This creates `layout.txt` with the network topology.

2. **Run Simulation**:
   The main simulation executes automatically after layout generation.

3. **Output**:
   - Console displays simulation progress and performance metrics
   - `layout.txt` contains the generated network node coordinates

### Configuration

Modify simulation parameters in `Main.java`:

```java
// Grid dimensions
int rows = 50;
int cols = 50;

// Event detection probability
double eventProb = 0.0001;

// Agent spawn probability
double agentProb = 0.5;

// Communication distance
int commDist = 15;
```

## Testing

The project includes comprehensive test coverage:

```bash
# Run all tests
mvn test
# or
gradle test
```

**Test Classes**:
- `AgentTest.java` - Agent behavior and exploration
- `QueryTest.java` - Query path-finding and routing
- `MessageTests.java` - Message delivery and routing
- `NetworkTest.java` - Network topology and neighbors
- `SensorNodeTest.java` - Node functionality
- `EnvironmentTests.java` - Simulation integration
- `RoutingTableTest.java` - Route storage and merging
- `CommunicationDistanceTests.java` - Communication range validation

## Design Patterns

The simulation implements several design patterns:

- **Template Method**: `Messenger` abstract class defines structure for agents, queries, and messages
- **Observer**: Nodes react to incoming messengers
- **Strategy**: Different movement strategies (exploration vs. routing)
- **Factory**: Environment creates events and agents probabilistically

## Performance Features

- **Parallel Stream Processing**: Nodes are processed in parallel for efficiency
- **Poisson Distribution Sampling**: Optimized event generation for large networks
- **Efficient Neighbor Calculation**: Circular distance formula
- **Pre-allocated Data Structures**: HashMaps with appropriate initial capacities
- **Timing Instrumentation**: Per-iteration performance tracking

## Project Structure

```
.
├── Main.java                          # Entry point
├── Environment.java                   # Simulation engine
├── Network.java                       # Network topology
├── SensorNode.java                    # Sensor node implementation
├── Agent.java                         # Mobile agent
├── Query.java                         # Query implementation
├── Message.java                       # Response message
├── Messenger.java                     # Abstract base for mobile entities
├── RoutingTable.java                  # Route storage
├── Event.java                         # Event representation
├── EventValue.java                    # Route metadata
├── Coordinate.java                    # 2D coordinates
├── layout.txt                         # Generated network layout
└── README.md                          # This file
```

## Academic Context

This project was developed as part of a distributed computing course at Umeå University. It demonstrates concepts in:
- Distributed information propagation
- Autonomous agent systems
- Probabilistic event simulation
- Routing algorithm design
- Large-scale network simulation

## License

This project is part of academic coursework at Umeå University.
