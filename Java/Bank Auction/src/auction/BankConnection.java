package auction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * captures the socket connection between an agent program and bank server
 * Handles the communication between bank and auction house
 */
public class BankConnection implements Runnable {
    private static boolean registered = false;
    private static Socket socket;
    private static PrintWriter toBank;
    private static BufferedReader fromBank;

    /**
     * Creates a new bank connection object
     * @param host Host name of bank's server
     * @param port Port number of bank's server
     * @throws IOException If an error occurrs while attempting to connect
     * to the bank server
     */
    public BankConnection(String host, int port) throws IOException {
        socket = new Socket(host, port);
        toBank = new PrintWriter(socket.getOutputStream(),true);
        InputStreamReader isr = new InputStreamReader(socket.getInputStream());
        fromBank = new BufferedReader(isr);
    }

    /**
     * A thread loop to check for new messages to or from the bank
     */
    @Override
    public void run() {
        String input;

        while (!socket.isClosed() && !Auction.getItems().isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException exc){
                Thread.currentThread().interrupt();
            }

            try {
                input = fromBank.readLine();
            } catch (IOException exc) {
                input = null;
            }

            if (input != null) {
                processBankMsg(input);
            }
        }

        if (!socket.isClosed() && Auction.getItems().isEmpty())
            toBank.println("auctionClosing soldOut");
    }

    /**
     * @return True if bank account is successfully logged into ; otherwise false
     */
    public static boolean isRegistered() {
        return registered;
    }

    /**
     * Processes new message from bank
     * @param input Message from bank
     */
    private synchronized void processBankMsg(String input) {
        String[] args = input.split(" ");
        System.out.println("Message received from bank" + input);
        switch (args[0]) {
            case "SuccessfulLogin" -> {
                registered = true;
                toBank.println("open "  + Auction.getPort());
            }
            case "FailedLogin" -> {
                System.err.println("Unable to login to bank");
            }
            case "fundsTransferred", "holdSuccessful", "holdFailed" ->
                    BidManager.sendMessage(input);
            case "Balances" -> Menu.sendBalance(Double.parseDouble(args[1]));
            case "FailedReg" -> {
                System.err.println("User already has a user with your name or the same password");
            }
        }
    }

    /**
     * Sends a message to bank
     * @param msg Message to the bank
     */
    protected static void sendMessage(String msg) {
        toBank.println(msg);
    }

    /**
     * Closes connection with the bank
     * @throws IOException If an error occurrs while disconnecting from
     * the bank server
     */
    protected static void close() throws IOException {
        toBank.println("auctionClosing noActivity");
        toBank.close();
        fromBank.close();
        socket.close();
    }
}
