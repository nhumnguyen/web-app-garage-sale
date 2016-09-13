package models;

import com.avaje.ebean.Model;
import sun.awt.image.ImageWatched;

import javax.persistence.Entity;
import javax.persistence.Id;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by qnguyen47 on 6/29/2016.
 */

@Entity
public class Transaction extends Model {

    private LinkedList<Item> itemList; //List of items associated with transaction
    public String buyerID; //Buyer's unique identifier
    public String buyer; //Buyer's name
    @Id
    public String tranID; //Unique identifier of transaction
    public double total; //total amount buyer should pay
    public double cash; //amount paid by cash
    public double card; //amount paid by card or check
    double change; //the change given back to buyer


    /**
     * Returns list of all items in cart
     * @return itemList a list of all items to be bought
     */
    public LinkedList<Item> getItemList() {
        return itemList;
    }

    /**
     * Adds items to cart
     * @param list a list of all items to be added to transaction list
     */
    public void setItemList(LinkedList<Item> list) {
        if (itemList == null) {
            itemList = new LinkedList<Item>();
        }
        itemList = list;
    }

    /**
     * Returns total dollar value of transaction
     * @return total the total dollar value of transaction
     */
    public double getTotal() {
        return total;
    }


    /**
     * Returns total amount of change to be rendered
     * @return double the total amount of change. Zero if originally less than zero.
     */
    public double getChange() {
        change = card + cash - total;
        if(change < 0) {
            change = 0;
        }
        return change;
    }


}