package agent;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class AutoAgent implements Runnable {
    private int id;
    private double balance = 500;
    private Socket bankSocket, auctionSocket;
    private PrintWriter toBank, toAuction;
    private BufferedReader fromBank, fromAuction;

    private final Map<String, Double> items = new HashMap<>(); // key: fullName (e.g. Monitor/123)
    private final Map<String, Integer> itemIdMap = new HashMap<>(); // key: name -> id
    private final Random rand = new Random();

    public AutoAgent(String bankHost, int bankPort, String auctionHost, int auctionPort) throws IOException {
        bankSocket = new Socket(bankHost, bankPort);
        auctionSocket = new Socket(auctionHost, auctionPort);

        toBank = new PrintWriter(bankSocket.getOutputStream(), true);
        fromBank = new BufferedReader(new InputStreamReader(bankSocket.getInputStream()));

        toAuction = new PrintWriter(auctionSocket.getOutputStream(), true);
        fromAuction = new BufferedReader(new InputStreamReader(auctionSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            id = 1000 + rand.nextInt(9000);
            toBank.println("registerAgent AutoAgent" + id + " " + id);
            Thread.sleep(200);
            toBank.println("deposit " + id + " " + balance);

            toAuction.println("agentID " + id);
            Thread.sleep(200);
            toAuction.println("ReqItems");

            while (true) {
                String line = fromAuction.readLine();
                if (line == null) break;

                System.out.println("[AutoAgent] " + line);

                if (line.startsWith("auctionItems")) {
                    parseItems(line);
                    Thread.sleep(1000);  // slight delay to simulate decision making
                    tryBidding();
                } else if (line.startsWith("OutBid")) {
                    String itemId = line.split(" ")[1];
                    rebid(itemId);
                } else if (line.startsWith("itemDelivered")) {
                    System.out.println("[AutoAgent] Won item " + line.split(" ")[1]);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseItems(String msg) {
        items.clear();
        itemIdMap.clear();
        String[] parts = msg.split(" ");
        for (int i = 1; i < parts.length - 1; i += 2) {
            String[] nameId = parts[i].split("/");
            String name = nameId[0];
            int id = Integer.parseInt(nameId[1]);
            double price = Double.parseDouble(parts[i + 1]);

            String fullName = name + "/" + id;
            items.put(fullName, price);
            itemIdMap.put(fullName, id);
        }
    }

    private void tryBidding() {
        for (Map.Entry<String, Double> entry : items.entrySet()) {
            String fullName = entry.getKey();
            double bid = entry.getValue() + 10;

            if (rand.nextBoolean() && balance >= bid) {
                System.out.println("[AutoAgent] Bidding $" + bid + " on " + fullName);
                toAuction.println("newBid " + fullName + " " + (int) bid);
            }
        }
    }

    private void rebid(String itemId) {
        for (String fullName : items.keySet()) {
            if (fullName.endsWith("/" + itemId)) {
                double newBid = items.get(fullName) + 10;
                if (balance >= newBid) {
                    System.out.println("[AutoAgent] Re-bidding $" + newBid + " on " + fullName);
                    toAuction.println("newBid " + fullName + " " + (int) newBid);
                }
            }
        }
    }

    public static void main(String[] args) {
        String bankHost = "localhost";
        int bankPort = 1245;
        String auctionHost = "localhost";
        int auctionPort = 6000;

        try {
            AutoAgent agent = new AutoAgent(bankHost, bankPort, auctionHost, auctionPort);
            new Thread(agent).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
