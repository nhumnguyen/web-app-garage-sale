package models;

import com.avaje.ebean.Model;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.io.*;
import java.text.DecimalFormat;

@Entity
public class Item extends Model {
    @Id
    public int id; //Unique identifying id
    public String name; //Name of the item
    public String description; //Description of the item
    public int quantity; //Quantity of item remaining to be sold
    public int numSold; //Number of item currently sold
    public String price; //Suggested price of item
    public double salePrice; //Price item is actually sold for
    public double lowerLimit = 0; //Seller decided lower limit on price
    public User seller = new User(null, null, null); //Points to designated owner/seller of item
    public String url; //Url for imgur direct image link for image display


    /**
     * Adds an image to the item
     * @param str string containing path to image
     */
    public void setImage(String url) {
        this.url = url;
    }

    /**
     * Designates user as seller of item
     * @param seller the user that is selling the object
     */
    public void setSeller(User seller) {
        seller.addRole(User.Role.SELLER);
        this.seller = seller;
    }

    /**
     * Sets lower limit on haggling price
     * @param lowerLimit the lowest price acceptable
     */
    public void setLimits(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    /**
     * Checks whether two items are equal
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        Item i = (Item) o;
        return (this.id == i.id);
    }

}