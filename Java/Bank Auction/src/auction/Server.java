package auction;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * A server for the auction house, it captures the server socket that agents
 * connect to in order to communicate to the auction house
 * Contains helper functions to find, add, and remove active agent connections
 */
public class Server implements Runnable {
    private static ServerSocket serverSocket;
    private static final Map<Integer,
            ClientConnection> clients = new HashMap<>();

    /**
     * Creates a new server
     * @param port Port number the server socket connection runs through
     * @throws IOException If an error occurs when starting the server socket
     */
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    /**
     * A server loop accepts incoming socket connections, capture it
     * in a client connection object, and then starts them on their own thread
     */
    @Override
    public void run() {
        while (!serverSocket.isClosed() && !Auction.getItems().isEmpty()) {
            try {
                Socket client = serverSocket.accept();
                ClientConnection cc = new ClientConnection(client);
                Thread t = new Thread(cc);
                t.start();
            } catch (IOException exc) {}

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * gets a client currently connected to the server
     * @param clientID ID value of the client to identify their connection
     * @return Client connection object associated with the given ID
     */
    protected static ClientConnection getClient(int clientID) {
        return clients.get(clientID);
    }

    /**
     * Adds a connected client to the server's list of active connections
     * @param clientID ID value given by the client
     * @param cc Client connection object associated with the new connection
     */
    protected static void addClient(int clientID, ClientConnection cc) {
        clients.put(clientID, cc);
    }

    /**
     * Removes a connected client from server's list of active connections
     * @param clientID ID value of the client to identify their connection
     */
    protected static void removeClient(int clientID) {
        clients.remove(clientID);
    }

    /**
     * Sends an updated list of items currently for sale
     * to all the clients connected to the auction house server
     * @param items Updated list of items for sale
     */
    protected static void sendItemsUpdate(String items) {
        for (ClientConnection cc : clients.values()) cc.sendMessage(items);
    }

    /**
     * Shuts down the server and closes all active connections to it
     * @throws IOException If an error occurs closing any open sockets
     */
    protected static void close() throws IOException {
        serverSocket.close();

        for (ClientConnection cc : clients.values()) {
            cc.close("noActivity");
        }

    }

}

