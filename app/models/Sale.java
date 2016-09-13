package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

import java.util.HashMap;
import java.util.LinkedList;
import java.io.*;
import java.util.*;

public class Sale extends Model implements Serializable {

    @Id
    public Long id; //Unique identifier for Sale
    public String name; //Name of sale
    public String date; //Date the sale will take place
    public LinkedList<Item> catalogue; //All items to be sold in sale
    public LinkedList<User> userRoles; //All users associated with sale and their permissions
    public boolean open; //True is sale is open, false if ended
    public LinkedList<Item> soldCatalogue; //All items that were in the catalogue but sold out

    /**
     * Getter method for soldCatalogue
     * @return LinkList of soldCatalogue
     */
    public LinkedList<Item> getSoldCatalogue() {
        if(soldCatalogue == null) {
            soldCatalogue = new LinkedList<Item>();
        }
        return catalogue;
    }

    /**
     * Adds item to soldCatalogue and removes it from catalogue
     * @param item that was sold
     */
    public void addSoldItem(Item item) {
        if (soldCatalogue == null) {
            soldCatalogue = new LinkedList<Item>();
        }
        catalogue.remove(item);
        soldCatalogue.add(item);
    }

    /**
     * Getter method for sale open status
     * @return boolean true if and only if the sale is ongoing
     */
    public boolean isOpen(){
        return open;
    }

    /**
     * Setter method for sale open status
     * @param status true if and only if the sale is ongoing
     */
    public void setSaleStatus(boolean status) {
        this.open = status;
    }

    /**
     * Returns the catalog of items to render
     * 
     * @return catalogue the list of all items
     */
    public LinkedList<Item> getCatalogue() {
        if(catalogue == null) {
            catalogue = new LinkedList<Item>();
        }
        return catalogue;
    }

    /**
     * Adds an item to the catalog.
     *
     * @param item the item to be added
     */
    public void addItem(Item item) {
        if (catalogue == null) {
            catalogue = new LinkedList<Item>();
        }
        catalogue.add(item);
    }

    /**
     * Removes an item from the catalog.
     *
     * @param item the item to be removed
     */
    public void removeItem(Item item) {
        if (catalogue != null) {
            catalogue.remove(item);
        }
    }

    /**
     * Associates user with sale
     * @param user the user to be given viewing rights to sale
     */
    public void addUser(User user) {
        if(userRoles == null) {
            userRoles = new LinkedList<>();
        }
        userRoles.add(user);
    }

    /**
     * Terminates user's association with sale
     * @param user the user to be removed from userRoles
     */
    public void removeUser(User user) {
        if(userRoles != null) {
            userRoles.remove(user);
        }
    }

}
