package bank;

import javax.imageio.IIOException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Represents the server that listens for incoming client connections
 * and spawns a new @link ClientConnection thread for each client
 */
public class Server implements Runnable {
    ServerSocket serverSocket;
    ArrayList<ClientConnection> clientList = new ArrayList<>();
    public Server(int port) {
        try{
            serverSocket = new ServerSocket(port);
            System.out.println(serverSocket.getLocalPort());
        } catch (IIOException e){} catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Starts accepting incoming client connections
     * For each connection, a new @link ClientConnection thread is started
     */
    @Override
    public void run() {
        while (true){
            try {
                Socket clients = serverSocket.accept();
                ClientConnection cc = new ClientConnection(clients);
                clientList.add(cc);
                Thread thread = new Thread(cc);
                thread.start();
            } catch (IOException e) {}



        }

    }

    /**
     * Closes all active client connections
     * used during server shutdown to release resources.
     */
    public void close(){
        for(ClientConnection client: clientList){
            client.close();
        }
    }
}
