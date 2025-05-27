package Dominos;

import java.util.*;

/**
 * @author lauren
 * CS251
 * Dominos

 * This console based implementation plays against a computer.
 * Turns are taken and games ends when no moves are possible.
 * Winner is determined by total vlaue of dominos remaining.
 * Command line arguments has a maximum value of 6 dots on a domino,
 * but inputs range from 3-9
  */
public class Main {
    private static boolean compTurnSkipped;
    private static boolean playerTurnSkipped;

    public static void main(String[] args) {
        // Process command-line argument for max dots
        int maxDots = 6; // default
        if (args.length > 0) {
            try {
                int inputDots = Integer.parseInt(args[0]);
                if (inputDots >= 3 && inputDots <= 9) {
                    maxDots = inputDots;
                } else {
                    System.out.println("Invalid input! Using default max dots (6). Please use a number between 3 and 9.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Using default max dots (6). Please enter a valid integer.");
            }
        }

        // Initialize the boneyard with specified max dots
        BoneYard boneyard = new BoneYard(maxDots);
        Tray compDoms = new Tray(true);
        Tray playerDoms = new Tray(false);
        Board board = new Board();

        // Deal 7 dominoes to each tray
        for (int i = 0; i < 7; i++) {
            if (!boneyard.isEmpty())
                compDoms.add(boneyard.pop());
            if (!boneyard.isEmpty())
                playerDoms.add(boneyard.pop());
        }

        // Show the player's initial tray
        System.out.println("Your Tray:");
        playerDoms.show();

        // Main game loop
        Scanner scanner = new Scanner(System.in);
        while (true) {
            board.showBoard();
            System.out.println("Your Tray:");
            playerDoms.show();

            // Player turn
            boolean turnDone = false;
            while (!turnDone) {
                char action = getAction();
                switch (action) {
                    case 'p':
                        if (playDomino(board, playerDoms))
                            turnDone = true;
                        break;
                    case 'd':
                        if (drawBoneYard(boneyard, playerDoms, board))
                            turnDone = true;
                        break;
                    case 'q':
                        System.exit(0);
                }
            }
            if (checkWin(compDoms, playerDoms, true))
                break;

            // Computer turn
            board.showBoard();
            System.out.println("The computer is making a move...");
            compLogic(board, compDoms, boneyard);
            if (checkWin(compDoms, playerDoms, false))
                break;
        }
    }

    private static boolean drawBoneYard(Stack<Domino> boneYard, Tray tray, Board board) {
        boolean valReturn = true;
        if (boneYard.size() == 0) {
            System.out.println("The boneyard is empty.");
            if (!board.checkPossibleMove(tray)) {
                if (tray.isCompTray())
                    compTurnSkipped = true;
                else
                    playerTurnSkipped = true;
                System.out.println("Turn is skipped.");
                return true;
            }
        } else if (!board.checkPossibleMove(tray)) {
            tray.add(boneYard.pop());
            System.out.printf("The boneyard has %d dominos left.\n", boneYard.size());
        } else {
            System.out.println("You have a playable domino.\nYou must play all playable dominos before drawing from the boneyard.");
            valReturn = false;
        }
        return valReturn;
    }

    private static boolean playDomino(Board board, Tray playerDoms) {
        if (!board.checkPossibleMove(playerDoms)) {
            System.out.println("You have no valid move.\nDraw from the boneyard.");
            return false;
        }
        boolean validMove = false;
        while (!validMove) {
            board.showBoard();
            int index = pickDom(playerDoms);
            // If board is empty, place the domino directly
            if (board.size() == 0) {
                board.addToBoard(playerDoms.get(index), false);
                playerDoms.remove(index);
                return true;
            }
            boolean left = leftOrRight();
            boolean rotate = chooseRotate();
            if (rotate)
                playerDoms.get(index).rotate();
            if (!board.checkValidMove(playerDoms.get(index), left)) {
                System.out.println("That domino doesn't work there!");
            } else {
                validMove = true;
                board.addToBoard(playerDoms.get(index), left);
                playerDoms.remove(index);
            }
        }
        playerTurnSkipped = false;
        return true;
    }

    private static int pickDom(Tray playerDoms) {
        boolean goodInput = false;
        int input = 0;
        Scanner scan = new Scanner(System.in);
        while (!goodInput) {
            System.out.println("Your Tray:");
            playerDoms.show();
            System.out.println("[index] Play a Domino.");
            try {
                input = scan.nextInt();
                if (input < 0 || input >= playerDoms.size())
                    throw new InputMismatchException();
                goodInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Input not valid. Try again.");
                scan.nextLine();
            }
        }
        return input;
    }

    private static boolean leftOrRight() {
        boolean goodInput = false;
        boolean left = false;
        Scanner scan = new Scanner(System.in);
        while (!goodInput) {
            System.out.println("[l/r] left or right.");
            try {
                String userIn = scan.next();
                if (userIn.equalsIgnoreCase("l")) {
                    goodInput = true;
                    left = true;
                } else if (userIn.equalsIgnoreCase("r")) {
                    goodInput = true;
                } else {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e) {
                System.out.println("Input not valid. Try again.");
            }
        }
        return left;
    }

    private static boolean chooseRotate() {
        Scanner scan = new Scanner(System.in);
        boolean goodInput = false;
        boolean rotate = false;
        while (!goodInput) {
            System.out.println("[y/n] rotate?");
            try {
                String userIn = scan.next();
                if (userIn.equalsIgnoreCase("y")) {
                    goodInput = true;
                    rotate = true;
                } else if (userIn.equalsIgnoreCase("n")) {
                    goodInput = true;
                } else {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e) {
                System.out.println("Input not valid. Try again.");
            }
        }
        return rotate;
    }

    private static char getAction() {
        Scanner scan = new Scanner(System.in);
        boolean goodInput = false;
        char userIn = 0;
        while (!goodInput) {
            System.out.println("""
                    [p] Play Domino
                    [d] Draw from boneyard
                    [q] Quit""");
            try {
                userIn = scan.next().charAt(0);
                if (userIn != 'p' && userIn != 'd' && userIn != 'q')
                    throw new InputMismatchException();
                goodInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Try Again.");
            }
        }
        return userIn;
    }

    private static void compLogic(Board board, Tray compDoms, Stack<Domino> boneYard) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            System.out.println(ie);
        }
        if (board.checkPossibleMove(compDoms)) {
            for (Iterator<Domino> it = compDoms.iterator(); it.hasNext(); ) {
                Domino d = it.next();
                if (board.checkValidMove(d, true)) {
                    board.addToBoard(d, true);
                    it.remove();
                    return;
                }
                if (board.checkValidMove(d, false)) {
                    board.addToBoard(d, false);
                    it.remove();
                    return;
                }
                d.rotate();
                if (board.checkValidMove(d, true)) {
                    board.addToBoard(d, true);
                    it.remove();
                    return;
                }
                if (board.checkValidMove(d, false)) {
                    board.addToBoard(d, false);
                    it.remove();
                    return;
                }
            }
            compTurnSkipped = false;
        } else {
            System.out.println("The computer drew from the boneyard.");
            drawBoneYard(boneYard, compDoms, board);
        }
    }

    private static boolean checkWin(Tray compDoms, Tray playerDoms, boolean playerWin) {
        if (compTurnSkipped && playerTurnSkipped) {
            int compScore = 0;
            int playerScore = 0;
            for (Domino d : compDoms)
                compScore += d.getValues()[0] + d.getValues()[1];
            for (Domino d : playerDoms)
                playerScore += d.getValues()[0] + d.getValues()[1];
            System.out.println("Computer's Tray:");
            compDoms.show();
            System.out.println("Your Tray:");
            playerDoms.show();
            if (compScore > playerScore)
                System.out.println("Congratulations you won!");
            else if (compScore < playerScore)
                System.out.println("Better luck next time!");
            else {
                System.out.println("It was a tie");
                if (playerWin)
                    System.out.println("You played the last Domino so\nYou Win!");
                else
                    System.out.println("The computer played the last Domino so\nBetter luck next time!");
            }
            System.out.printf("Computer's Score: %d\nYour Score: %d\n", compScore, playerScore);
            return true;
        }
        return false;
    }
}
