package agent;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * object that handles a connection to the auction, it stores auction
 * the info in an auction object
 * Uses an ArrayBlockingQueue for message processing
 */
public class AuctionConnection implements Runnable{
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final PrintWriter printWriter;
    private final BlockingQueue<String> inbox;
    private final Auction auction;

    public AuctionConnection(String hostName, int portNumber,Auction auction) throws Exception{
        socket = new Socket(hostName,portNumber);
        this.auction = auction;
        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        bufferedReader = new BufferedReader(inputStreamReader);
        printWriter = new PrintWriter(socket.getOutputStream(),true);
        inbox = new ArrayBlockingQueue<>(20);
        //register with auction
        sendMessage("agentID " + Server.getClientId());
    }

    /**
     * will check for a new message every 100 milliseconds & call a process inbox
     */
    @Override
    public void run() {
        while(!socket.isClosed()){
            try {
                Thread.sleep(100);
            }catch (InterruptedException ei){}

            try{
                inbox.add(bufferedReader.readLine());
            } catch (IOException e) {

            }
            processInbox();
        }
    }

    /**
     * Process all messages in inbox
     * and handles any message sent from an auction
     */
    private void processInbox(){
        while (!inbox.isEmpty()) {
            String message = inbox.poll();
            String[] args = message.split(" ");
            System.out.println("Message received from auction " + message);
            switch (args[0]) {
                //format: "auctionItems itemName/itemId currentBid"
                case "auctionItems" -> {
                    ArrayList<Item> auctionItems = new ArrayList<>();
                    for (int i = 1; i < args.length; i += 2) {
                        String[] nameAndID = args[i].split("/");
                        auctionItems.add(new Item(nameAndID[0], (int) Double.parseDouble(nameAndID[1]),
                                (int) Double.parseDouble(args[i+1])));
                    }
                    auction.updateItems(auctionItems);
                }
                //format: "Bid itemId currentBid"
                case "Bid" -> {
                    auction.updateItem(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                }
                //format: "invalidBid itemId reasonWhy"
                case "invalidBid" -> {
                    auction.updateItem(Integer.parseInt(args[1]), args[2]);
                }
                //format: "OutBid itemId"
                case "OutBid" -> {
                    auction.updateItem(Integer.parseInt(args[1]),"Out Bid");
                    Server.getBankConnection().sendMessage("CheckBalance");
                }
                //format: "itemDelivered itemId"
                case "bidPlaced" ->{
                    auction.updateItem(Integer.parseInt(args[1]), "Bid Accepted");
                    Server.requestAuctionItems(auction);
                    Server.getBankConnection().sendMessage("CheckBalance");
                }
                //format: "itemDelivered itemId"
                case "itemWon" ->{
                    auction.updateItem(Integer.parseInt(args[1]), "Item Won");
                    Platform.runLater(() ->Agent.addToYourItems(auction.getItem(Integer.parseInt(args[1])).getName()));
                }
            }
        }
    }

    /**
     * Sends messages to the auction
     * @param message is the message being sent
     */
    protected void sendMessage(String message){
        printWriter.println(message);
        System.out.println(message + " sent to " + socket.getPort());
    }

}
