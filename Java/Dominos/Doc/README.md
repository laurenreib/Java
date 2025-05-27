# Dominos
## Overview
This project is a JavaFX-based implementation of the classic Domino game. 
It allows a player to compete against a computer opponent.
The game includes a graphical user interface (GUI) for a more interactive experience, 
as well as a command-line interface (CLI) for simpler gameplay.
The game supports customizable maximum domino values (default is 6, but 
can be set between 3 and 9). The player and computer take turns placing dominoes on the board,
drawing from the boneyard when necessary, and the game ends when no more moves are possible.
The winner is determined by the total value of the dominoes remaining in each player's tray.


### Features
Graphical User Interface (GUI):
* Interactive domino placement using mouse clicks
* Visual representation of board, player tray, & boneyard
* Resizing of game window based on board size

### Command line interface
* Text based gameplay
* player can chose a domino, draw from boneyard, or quit
### Customizable domino set
* The max number of dots on a domino can be set via CML args

## Game logic
* Valid move checking for both player & computer
* Automatic drawing from boneyard when no valid moves
* Win condition detection & score calculation

### Command Line Args
* Game accepts optional CML args to set the max number of dots
* Default max doms is 6 but valid inputs range from 3-9


### Core Classes
**JFXMain:** The main class for the GUI implementation. Handles user input, game logic, and rendering.

**Main:** The main class for the CLI implementation. Handles text-based gameplay.

**Domino:** Represents a single domino with two values.

**Tray:** Represents a player's or computer's collection of dominoes.

**Board:** Represents the playing field where dominoes are placed.

**BoneYard:** Represents the collection of unused dominoes that players can draw from.

**DispDom:** A graphical representation of a domino for the GUI.

**TrayDisp:** A graphical representation of a player's tray for the GUI.


## Notes:
* The computer's logic is simple, it will always play the first valid move it finds
* The GUI version provides a more immersive experience
* Must ensure the png's are available (made sure to make them as resource root when creating the jar)

