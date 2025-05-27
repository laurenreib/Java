package agent;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stores information for an action house and stores gui representation handled corresponding the
 * mouse events
 * The items -> a hashmap, item object, format itemId
 * The displayBox -> what users click on
 */
public class Auction {
    private String name;
    private String hostName;
    private int port;
    private final HashMap<Integer,Item> items;
    private final HBox displayBox;

    Auction(String hostName, String name, int port){
        this.hostName = hostName;
        this.name = name;
        this.port = port;
        items = new HashMap<>();
        Label nameLbl = new Label(name);
        displayBox = new HBox(nameLbl);
        displayBox.setOnMouseEntered(event -> nameLbl.setOpacity(.5));
        displayBox.setOnMouseExited(event ->  nameLbl.setOpacity(1));
        displayBox.setOnMouseClicked(event -> {
            Server.addAuctionConnection(this);
            Agent.setCurrAuction(this);
        });
    }

    public HBox getDisplayBox() {
        return displayBox;
    }

    /**
     * Takes in list of updated items from auction, copies old bid statuses if
     * an item wasn't removed then updates the items hashmap
     * @param itemsUpdate updated items list
     */
    protected void updateItems(ArrayList<Item> itemsUpdate){
        for (Item item : itemsUpdate) {
            if (items.containsKey(item.getItemId())) item.setBidStatus(items.get(item.getItemId()).getBidStatus());
        }

        items.clear();
        for (Item item : itemsUpdate) items.put(item.getItemId(),item);

        Platform.runLater(() -> Agent.updateItemsBox(items, name));
    }

    /**
     * updates an items bid and tells the agent to update the gui
     * @param bid new bid
     * @param itemID item ID
     */
    protected void updateItem(int itemID, int bid){
        items.get(itemID).setBid(bid);
        Platform.runLater(()->Agent.updateItemsBox(items,name));
    }

    /**
     * same as above
     * updates an items bid status and tells the agent to update the gui
     * @param itemID item ID
     * @param bidStatus new bid
     */
    protected void updateItem(int itemID, String bidStatus){
        items.get(itemID).setBidStatus(bidStatus);
        Platform.runLater(()->Agent.updateItemsBox(items,name));
    }

    public HashMap<Integer,Item> getItems() {
        return items;
    }

    protected Item getItem(int itemID){
        return items.get(itemID);
    }

    public int getPort() {
        return port;
    }

    public String getHostName() {
        return hostName;
    }

    public String getName() {
        return name;
    }
}
