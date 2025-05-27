package bank;

import javafx.util.Pair;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Bank {
    protected static ConcurrentHashMap<Integer, Account> bank = new ConcurrentHashMap<>();
    protected static HashMap<Pair<String, Integer>, String> availableAuctions = new HashMap<>();
    protected static HashMap<Integer, PrintWriter> activeClients = new HashMap<>();

    private static final String host = "localhost";
    private static final int port = 1245;

    private static int nextAccountId = 100001;

    public Bank() {
        try {
            Scanner sc = new Scanner(new File("src/bank/users.txt"));
            sc.nextLine();
            while (sc.hasNext()) {
                String line = sc.nextLine();
                String[] inputs = line.split(", ");
                Account account = new Account(inputs[0], Double.parseDouble(inputs[3]), Integer.parseInt(inputs[1]), Integer.parseInt(inputs[2]), new HashMap<>());
                bank.put(Integer.parseInt(inputs[1]), account);
                System.out.println(account);
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }

    public static synchronized int assignID() {
        return nextAccountId++;
    }

    public static void createHumanUser(String user, int ID) {
        bank.put(ID, new Account(user, 0.0, ID, 0, new HashMap<>()));
    }

    public static void createAuctionHouseUser(String user, int ID) {
        bank.put(ID, new Account(user, 0.0, ID, 1, new HashMap<>()));
    }

    public static boolean withdraw(int ID, double amount) {
        Account acc = bank.get(ID);
        if (acc.Account() >= amount) {
            acc = new Account(acc.User(), acc.Account() - amount, acc.ID(), acc.Type(), acc.Holds());
            bank.put(ID, acc);
            return true;
        }
        System.out.println("Not enough funds");
        return false;
    }

    public static boolean deposit(int ID, double amount) {
        Account acc = bank.get(ID);
        acc = new Account(acc.User(), acc.Account() + amount, acc.ID(), acc.Type(), acc.Holds());
        bank.put(ID, acc);
        return true;
    }

    public static boolean hold(int ID, double amount, int itemID) {
        Account acc = bank.get(ID);
        double totalHolds = acc.Holds().values().stream().mapToDouble(Double::doubleValue).sum();
        if (0 < acc.Account() + amount + totalHolds) {
            acc.Holds().put(itemID, amount);
            acc = new Account(acc.User(), acc.Account(), ID, acc.Type(), acc.Holds());
            bank.put(ID, acc);
            return true;
        }
        System.out.println("Not enough funds");
        return false;
    }

    public static boolean removeHold(int ID, int itemID) {
        Account acc = bank.get(ID);
        acc.Holds().remove(itemID);
        acc = new Account(acc.User(), acc.Account(), ID, acc.Type(), acc.Holds());
        bank.put(ID, acc);
        return true;
    }

    public static boolean pushTransfer(int fromID, int toID, int itemID) {
        Account from = bank.get(fromID);
        Account to = bank.get(toID);
        double amount = from.Holds().getOrDefault(itemID, 0.0);
        from.Holds().remove(itemID);

        from = new Account(from.User(), from.Account(), fromID, from.Type(), from.Holds());
        to = new Account(to.User(), to.Account() + amount, toID, to.Type(), to.Holds());

        bank.put(fromID, from);
        bank.put(toID, to);
        return true;
    }

    public static String getBalanceAndHolds(int ID) {
        Account acc = bank.get(ID);
        double holds = acc.Holds().values().stream().mapToDouble(Double::doubleValue).sum();
        return "Balances " + acc.Account() + " " + holds;
    }

    public static void broadcastAuctionUpdate() {
        for (PrintWriter out : activeClients.values()) {
            out.print("Auctions");
            for (Pair<String, Integer> auction : availableAuctions.keySet()) {
                out.print(" " + auction.getKey() + " " + availableAuctions.get(auction) + " " + auction.getValue());
            }
            out.println();
        }
    }

    public synchronized static void close() {
        try {
            File file = new File("src/bank/users.txt");
            if (file.exists()) file.delete();
            FileWriter fw = new FileWriter(file);
            fw.write("Users, ID, Type, Amount\n");
            for (Account acc : bank.values()) {
                fw.write(acc.User() + ", " + acc.ID() + ", " + acc.Type() + ", " + acc.Account() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving accounts to file.");
        }
    }

    public static void main(String[] args) {
        System.out.println("Created the bank's host: " + host + ", and port number: " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientConnection(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Entry point for the bank server application
     * initializes the bank, starts the server to listen for incoming client connections,
     * then provides a simple console interface to shut down the server.
     */
    public class Main {

        /**
         * Main method launches the bank server
         * @param args command-line argumentsl; expects a single argument representing the
         *             port number
         * @throws UnknownHostException if the local host address can't be determined
         * @throws InterruptedException if the main thread is interrupted while sleeping
         */
        public static void main(String[] args) throws UnknownHostException, InterruptedException {
            System.out.println(args[0]);
            Bank bank = new Bank();
            Server server = new Server(Integer.parseInt(args[0]));
            System.out.println(InetAddress.getLocalHost().getHostAddress());
            Thread thread = new Thread(server);
            thread.start();
            while(true){
                Scanner sc = new Scanner(System.in);
                System.out.println("Quit [q]");
                String response = sc.nextLine();
                if (response.charAt(0) == 'q' || response.charAt(0) == 'Q'){
                    Bank.close();
                    server.close();
                    System.exit(0);
                }
                else {
                    Thread.sleep(100);
                }
            }
        }
    }
}