## * Lauren Bustamante
## * No known bugs ..

# Scrabble Game

This project is a Java-based Scrabble game and solver. It includes a graphical user interface (GUI)
for playing Scrabble and a solver that can determine the best possible move given a board state and a player's tray of letters.

## Features

- **Scrabble Game**: Play Scrabble against a computer opponent.
- **Solver**: Automatically determine the highest-scoring move for a given board state and tray of letters.
- **Graphical Interface**: Interactive GUI built using JavaFX.
- **Dictionary Support**: Uses a Trie (prefix tree) for efficient word lookup and validation.

## Classes Overview

### `BoardSquare`
Represents a square on the Scrabble board. It stores the word and letter multipliers, as well as the current letter on the square.

### `Tray`
Represents the player's tray of tiles. It manages the tiles that the player can use to make moves.

### `Tile`
Represents a graphical tile on the board. It includes methods for setting and getting the letter on the tile, as well as handling user interactions.

### `Solver`
Contains the logic for determining the best move. It uses permutations of the tray letters and checks all possible placements on the board to find the highest-scoring move.

### `ScoreTracker`
Tracks the scores of moves and keeps the highest score and best move.

### `Scrabble`
The main application class that sets up the GUI and handles the game logic.

### `Tree`
Implements a Trie data structure for efficient word lookup and validation. It uses the `TreeNode` class to represent nodes in the Trie.

### `TreeNode`
Represents a node in the Trie data structure. Each node stores a character, a list of child nodes, and a flag indicating whether it can be the end of a valid word. It provides methods for adding words to the Trie, searching for words, and handling blank tiles.

### `Board`
Represents the Scrabble board. It initializes the board state and manages the board's tiles.

### `Anchor`