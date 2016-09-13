package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.LinkedList;

import static models.User.Role.SUPERUSER;


/**
 * Represents a user on the system.
 *
 * Data about the user like name, username, and password are stored during
 * registration and (for now) displayed on the profile page.
 *
 * @author adill7, ssrinivas32, qnguyen47
 */
@Entity
public class User extends Model {

    @Id
    public int id; //Unique identifier assigned at creation
    public String username; //Unique username for signin
    public String firstName; //User's first name
    public String lastName; //User's last name
    public String password; //User's password
    public String vpassword; //User's verification password
    public boolean loggedIn; //Status of user. True if currently logged in
    private LinkedList<Sale> sales; //All associated sales
    private LinkedList<Item> cart; //All items currently in cart
    public double total = 0.0; //Total cost of items currently in cart
    private LinkedList<Transaction> transactions; //All associated transactions
    public HashSet<Role> roles; //All associated roles
    public int loginCount = 0; //Number of failed log in attempts
    public Status status; //LOCKED or UNLOCKED depending on loginCount


    /**
     * Constructor with only the essentials needed to create an account.
     *
     * @param username
     * @param password
     * @param vpassword
     */
    public User(String username, String password, String vpassword) {
        this(username, password, vpassword, "No first name yet", "No last name yet");
    }

    /**
     * Constructor with all fields desirable while creating an account
     * @param username
     * @param password
     * @param vpassword
     * @param firstName
     * @param lastName
     */
    public User(String username, String password,
                String vpassword, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.vpassword = vpassword;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Toggle log in state.
     */
    public void logIn() {
        loggedIn = true;
    }

    /**
     * Toggle log out state.
     */
    public void logOut() {
        loggedIn = false;
    }

    /**
     * Check whether internal username and password match user entered
     * username and password.
     *
     * @param other
     * @return whether usernames and passwords match
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) { return false; }
        if (this == other) { return true; }
        if (!(other instanceof User)) { return false; }
        User that = (User) other;
        return this.username.equals(that.username) &&
                this.password.equals(that.password);
    }

    /**
     * Returns a list of all sales for a given user
     * @return sales the list of all sales for a given user
     */
    public LinkedList<Sale> getSales() {
        if (sales == null) {
            sales = new LinkedList<Sale>();
            return sales;
        } else {
            LinkedList<Sale> openSale = new LinkedList<>();
            for (Sale a: sales){
                if (a.isOpen()) {
                    openSale.add(a);
                }
            }
            return openSale;
        }
    }

    /**
     * Adds a new sale to the list of all sales for a given user
     * @param sale a sale to be added to the list of all sales
     */
    public void addSale(Sale sale) {
        boolean contains = false;
        for (Sale s: sales) {
            if (s.name.equals(sale.name)) {
                contains = true;
            }
        }
        if (!contains) {
            sales.add(sale);
        }
    }

    /**
     * Removes a sale from the list of all sales
     * @param sale the sale to be removed from the list of all sales
     */
    public void removeSale(Sale sale) {
        sales.remove(sale);
    }

    /**
     * Adds an item to cart in a given sale
     * @param item the item to be added to cart
     */
    public void addToCart(Item item) {
        if (cart == null) {
            cart = new LinkedList<Item>();
        }
        cart.add(item);
        if (item.price != null && !item.price.isEmpty()) {
            if (item.salePrice > 0) {
                total = total + item.salePrice;
            } else {
                total = total + Double.parseDouble(item.price);
            }
        }
    }

    /**
     * Returns the cart containing all items a user wishes to buy
     * @return cart list of all items that the user wishes to buy
     */
    public LinkedList<Item> getCart() {
        return cart;
    }

    /**
     * Returns total dollar value of transaction
     * @return total the total dollar value of transaction
     */
    public double getTotal(){
        return total;
    }

    /**
     * Returns record of fall financial transactions
     * @return transactions the list of all financial trnasactions
     */
    public LinkedList<Transaction> getTran() {
        return transactions;
    }

    /**
     * Adds a transaction to the record of all financial transactions
     * @param newTran transaction to be added to record of all financial transactions
     */
    @Transient
    public void addTran(Transaction newTran) {
        if (transactions == null) {
            transactions = new LinkedList<Transaction>();
        }
        newTran.setItemList(cart);
        newTran.total = total;
        transactions.add(newTran);
    }

    /**
     * Removes a transaction from the record of all transactions
     * @param tran
     */
    public void removeTran(Transaction tran) {
        transactions.remove(tran);
    }

    public void addRole(Role role) {
        if (roles == null) {
            roles = new HashSet<Role>();
        }
        roles.add(role);
    }

    /**
     * Inner class enumerating all possible Roles
     */
    public enum Role {
        GUEST, BOOKKEEPER, CASHIER, CLERK, SELLER, SALEADMIN, SUPERUSER
    }

    /**
     * Inner class enumerating locked and unlocked status for account
     */
    public enum Status {
        LOCKED, UNLOCKED
    }
}
