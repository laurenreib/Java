# Tiles Game


## Overview
This is a javafx based game where players must match patterns across
three layers of tiles. To win you must clear the board, it tracks 
players scores and high score.
## Features
**Three-layered tile system**: Each tile has three pattern layers that must be matched.
- **Randomized tile distribution**: Ensures unique gameplay each time.
- **Score tracking**: Keeps track of the current and high scores.
- **Win detection**: Determines when all tiles have been matched.
- **Interactive GUI**: Built using JavaFX for an engaging user experience.
### Project Structure
* Main.java # Entry point of the application
* Board.java # Manages the grid of tiles and game logic 
* Gui.java # Handles the graphical user interface 
* Tile.java # Represents an individual tile with three layers 
* Layer.java # Interface for tile layers 
* Layer1.java # Enum for first layer patterns 
* Layer2.java # Enum for second layer patterns 
* Layer3.java # Enum for third layer patterns 
* assets/ # Directory for game assets 
* L1/ # Images for Layer 1 
* L2/ # Images for Layer 2 
* L3/ # Images for Layer 3 
* HighLight.png # Image for tile selection highlight