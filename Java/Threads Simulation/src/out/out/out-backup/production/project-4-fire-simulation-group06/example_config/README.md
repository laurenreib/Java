# Mobile Agent Fire Simulation

This Java project simulates a forest fire monitoring system using sensor network with 
mobile agents and a base station. It includes a GUI built with JavaFX that visually
shows the fire spread and agent activity in real time.

# Features:

* Sensor Network: Sensors are placed in a 2D grid and connected as neighbors
* Fire Propagation: Fire Spreads from a starting sensor node through the network
* Mobile Agents: Agents are dispatched from the base station to monitor the 
fires spread and report back findings.
* Interactive GUI: Start fires manually, controls fire speed, and visualizes the
status of all nodes and agents
* Configuration Loader: Loads sensor network structures from .txt files
* Memory Management: After the simulation ends, cleans up sensor connections
to assist garbage collection

# Project Structure

* `BaseStation.java` Manages mobile agents, dispatches them to neighbors, & logs their archives
* `MobileAgent.java` Mobile agent that explores the network, reacts to fire, and clones
itself when spreading
* `Sensor.java` Represents individual sensor nodes, handles status changes (normal, burning,dead,ect)
* `FireSimulationGUI` JavaFX GUI to visualize the simulation, start the fire, and adjust speed
* `Configuration.java` Builds the sensor network from a text file configuration
* `MarkForeNegation` Cleans up sensor links after simulation to prevent memory leaks
* `StationMessages.java` Defines common message constants used between components
* `SensorStatus.java` Defines constant statuses for the sensors (normal, burning, dead, ect)

# Notes:

* Fire will only start after the "Start Fire" button is pressed
* Mobile agents automatically spread, observe, and report back to the base station
* You can monitor agent activities in the console logs


* `sample.txt`
  Simple configuration as shown in the project description
* `sample2.txt`
  Same as above, but lists configuration in a different order
