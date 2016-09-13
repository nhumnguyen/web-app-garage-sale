package controllers;

import models.*;
import play.api.libs.concurrent.Promise;
import play.data.Form;
import play.mvc.*;

import views.html.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 *
 * @author adill7, ssrinivas32, qnguyen47
 */
public class HomeController extends Controller {

    HashMap<String, User> users = new HashMap<>(); //Map of all usernames to user objects across system
    User current; //The currently logged in user
    Sale currentSale; //The currently viewed sale
    Item currentItem; //The currently viewed item
    Transaction currentTransaction; //The transaction in progress
    LinkedList<Transaction> completedTransactions = new LinkedList<>(); //All completed transaction for reporting
    LinkedList<Sale> allSale = new LinkedList<>(); //All sales in the system
    LinkedList<Item> outOfStockItem = new LinkedList<>(); //All items with inventory of zero
    public LinkedList<Item> instockItem = new LinkedList<>(); //All items with nonzero inventory
    LinkedList<Item> soldItem = new LinkedList<>(); //All sold items
    LinkedList<Item> unsoldItem = new LinkedList<>(); //All unsold items at sale's termination
    int itemID = 100; //Record keeping constant for item IDs.

    /**
     * Render the main selection page.
     *
     * The configuration in the <code>routes</code> file means that
     * the index method will be called when the application receives
     * a <code>GET</code> request with a path of <code>/index</code>.
     *
     * @return result the page being rendered
     */
    public Result index() throws IOException, ClassNotFoundException {


        // filename
        String filename = "userFile.txt";

        // read

        InputStream is = new FileInputStream(filename);
        ObjectInput oi = new ObjectInputStream(is);
        users = (HashMap<String, User>) oi.readObject();
        oi.close();
        is.close();

        if (users != null) {
            for (User eachUser : users.values()) {
                if (eachUser != null) {
                    for (Sale event : eachUser.getSales()) {
                        if (event != null) {
                            for (Item i : event.getCatalogue()) {
                                if (i != null) {
                                    if (i.id > itemID) {
                                        itemID = i.id + 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return ok(index.render());
    }

    /**
     * Render the login page for the user
     *
     * The configuration in the <code>routes</code> file means that
     * the login method will be called when the application receives
     * a <code>GET</code> request with a path of <code>/login</code>.
     *
     * @return result the page being rendered
     */
    public Result login() {
        return ok(login.render());
    }

    /**
     * Render the profile page for the user if successful, else render
     * the main selection page.
     *
     * The configuration in the <code>routes</code> file means that
     * the login method will be called when the application receives
     * a <code>POST</code> request with a path of <code>/login</code>.
     *
     * @return result the page being rendered
     */
    public Result loginUser() {
        User user = Form.form(User.class).bindFromRequest().get();
        if (users.containsValue(user)) {
            User logUser = users.get(user.username);
            if(logUser.status == User.Status.UNLOCKED) {
                current = logUser;
                return ok(success.render(logUser));
            } else {
                return ok(index.render());
            }
        } else if (users.containsKey(user.username)) {
            User bad = users.get(user.username);
            if(bad.loginCount >= 2) {
                bad.status = User.Status.LOCKED;
            }
            bad.loginCount++;
            return ok(index.render());
        } else {
            return ok(index.render());
        }
    }

    /**
     * Register user within the program (for now).
     *
     * The configuration in the <code>routes</code> file means that
     * the registerUser method will be called when the application
     * receives a <code>GET</code> request with a path of
     * <code>/register</code>.
     *
     * @return result the page being rendered
     */
    public Result registerUser() throws IOException {
        User user = Form.form(User.class).bindFromRequest().get();
        if (users.containsKey(user.username)) {
            return ok(index.render());
        }
        if (user.password.equals(user.vpassword)) {
            users.put(user.username, user);
            current = user;
            if(user.username.equals("SUPERUSER")) {
                current.addRole(models.User.Role.SUPERUSER);
            } else {
                current.addRole(models.User.Role.GUEST);
            }
            current.status = models.User.Status.UNLOCKED;

            // filename
            String filename = "userFile.txt";

            // write
            OutputStream os = new FileOutputStream(filename);
            ObjectOutput oo = new ObjectOutputStream(os);
            oo.writeObject(users);
            oo.close();
            os.close();

            return ok(success.render(user));
        } else {
            return ok(index.render());
        }
    }

    /**
     * Render the registration page for the user.
     *
     * The configuration in the <code>routes</code> file means that
     * the register method will be called when the application
     * receives a <code>POST</code> request with a path of
     * <code>/register</code>.
     *
     * @return result the page being rendered
     */
    public Result register() {
        return ok(registration.render());
    }

    /**
     * Render the edit profile page for the user.
     *
     * The configuration in the <code>routes</code> file means that
     * the edit method will be called when the application
     * receives a <code>GET</code> request with a path of
     * <code>/edit</code>.
     *
     * @return result the page being rendered
     */
    public Result edit() {
        return ok(edit.render(current));
    }

    /**
     * Save the edits for the user and render new page.
     *
     * The configuration in the <code>routes</code> file means that
     * the save method will be called when the application
     * receives a <code>POST</code> request with a path of
     * <code>/save</code>.
     *
     * @return result the page being rendered
     */
    public Result save() throws IOException {
        User user = Form.form(User.class).bindFromRequest().get();
        users.remove(current);
        users.put(user.username, user);
        current = user;

        // filename
        String filename = "userFile.txt";

        // write
        OutputStream os = new FileOutputStream(filename);
        ObjectOutput oo = new ObjectOutputStream(os);
        oo.writeObject(users);
        oo.close();
        os.close();

        return ok(profile.render(user, users.values()));
    }

    /**
     * Renders the success page for the user.
     *
     * The configuration in the <code>routes</code> file means that
     * the success method will be called when the application
     * receives a <code>GET</code> request with a path of
     * <code>/success</code>.
     *
     * @return result the page being rendered
     */
    public Result success() {
        return ok(success.render(current));
    }

    /**
     * Renders the profile page for the user.
     *
     * The configuration in the <code>routes</code> file means that
     * the profile method will be called when the application
     * receives a <code>GET</code> request with a path of
     * <code>/profile</code>.
     *
     * @return result the page being rendered
     */
    public Result profile() {
        return ok(profile.render(current, users.values()));
    }

    /**
     * Renders a list of all sales for the user.
     * The configuration in the <code>routes</code> file means that
     * the profile method will be called when the application
     * receives a <code>GET</code> request with a path of
     * <code>/sales</code>.
     *
     * @return result the page being rendered
     */
    public Result sales() {
        allSale.clear();
        if (users != null) {
            for (User eachUser : users.values()) {
                if (eachUser != null) {
                    for (Sale event : eachUser.getSales()) {
                        if (event != null) {
                            if (event.isOpen()) {
                                allSale.add(event);
                            }
                        }
                    }
                }
            }
        }
        return ok(sales.render(allSale));
    }

    /**
     * Renders a new sale creator page for the user.
     * The configuration in the <code>routes</code> file means that
     * the profile method will be called when the application
     * receives a <code>GET</code> request with a path of
     * <code>/newSale</code>.
     *
     * @return result the page being rendered
     */
    public Result newSale() throws IOException {
        Sale sale = Form.form(Sale.class).bindFromRequest().get();
        current.addSale(sale);
        sale.setSaleStatus(true);
        if (!current.roles.contains(User.Role.SUPERUSER)) {
            current.addRole(User.Role.SALEADMIN);
        }

        // file

        String filename = "userFile.txt";

        // write
        OutputStream os = new FileOutputStream(filename);
        ObjectOutput oo = new ObjectOutputStream(os);
        oo.writeObject(users);
        oo.close();
        os.close();
        
        allSale.clear();
        if (users != null) {
            for (User eachUser : users.values()) {
                if (eachUser != null) {
                    for (Sale event : eachUser.getSales()) {
                        if (event != null) {
                            if (event.isOpen()) {
                                allSale.add(event);
                            }
                        }
                    }
                }
            }
        }

        return ok(sales.render(allSale));
    }

    /**
     * Selects and renders a sales page listing items for the user.
     * The configuration in the <code>routes</code> file means that
     * the profile method will be called when the application
     * receives a <code>GET</code> request with a path of
     * <code>/sale</code>.
     *
     * @param name the sale to render
     * @return result the page being rendered
     */
    public Result sale(String name) {
        Sale currSale = new Sale();
        for (User eachUser : users.values()) {
            for (Sale s : eachUser.getSales()) {
                if ((s.name.equals(name)) && (s.isOpen())) {
                    currSale = s;
//                    if (eachUser.username.equals(current.username)) {
//                        current.addRole(User.Role.SALEADMIN);
//                    } else {
//                        current.addRole(User.Role.HOLD);
//                    }
                }
            }
        }
        currentSale = currSale;
        return ok(sale.render(currSale, currSale.getCatalogue(), currSale.userRoles, current));
    }

    /**
     * Renders a page for creating/modifying an item for the user.
     * The configuration in the <code>routes</code> file means that
     * the profile method will be called when the application
     * receives a <code>GET</code> request with a path of
     * <code>/newItem</code>.
     *
     * @return result the page being rendered
     */
    public Result newItem() throws IOException {
        Item item = Form.form(Item.class).bindFromRequest().get();
        item.id = itemID;
        itemID++;
        currentSale.addItem(item);
        if (item.quantity > 0) {
            instockItem.add(item);
        }

        String filename = "userFile.txt";

        // write
        OutputStream os = new FileOutputStream(filename);
        ObjectOutput oo = new ObjectOutputStream(os);
        oo.writeObject(users);
        oo.close();
        os.close();

        return ok(sale.render(currentSale, currentSale.getCatalogue(), currentSale.userRoles, current));
    }

    /**
     * Renders a list of matched items to the user.
     * The configuration in the <code>routes</code> file means that
     * the profile method will be called when the application
     * receives a <code>GET</code> request with a path of
     * <code>/searchItem<code>.
     *
     * @param str the item to search for
     */
    public Result searchItem(String str) {
        LinkedList<Item> tempList = new LinkedList<Item>();
        for (Item i: currentSale.getCatalogue()) {
            if (i.name.contains(str)) {
                tempList.add(i);
            }
        }
        return ok(sale.render(currentSale, tempList, currentSale.userRoles, current));
    }

    /**
     * Renders a page for viewing an item for the user.
     * The configuration in the <code>routes</code> file means that
     * the profile method will be called when the application
     * receives a <code>GET</code> request with a path of
     * <code>/item</code>.
     *
     * @param name the name of the item being viewed.
     * @return result the page being rendered
     */
    public Result item(String name) {
        Item currItem = new Item();
        for (Item i: currentSale.getCatalogue()) {
            if (i.name.equals(name)) {
                currItem = i;
            }
        }
        currentItem = currItem;
        return ok(item.render(currentItem, current));
    }

    /**
     * Editing an item for the user.
     * The configuration in the <code>routes</code> file means that
     * the profile method will be called when the application
     * receives a <code>GET</code> request with a path of
     * <code>/saveItem</code>.
     *
     * @return result the page being rendered
     */

    public Result saveItem() throws IOException {
        Item edItem = Form.form(Item.class).bindFromRequest().get();
        edItem.id = currentItem.id;
        currentSale.removeItem(currentItem);
        currentSale.addItem(edItem);
        if (soldItem.contains(currentItem)) {
            edItem.numSold = currentItem.numSold;
            soldItem.remove(currentItem);
            soldItem.add(edItem);
        }
        if (instockItem.contains(currentItem)) {
            edItem.numSold = currentItem.numSold;
            instockItem.remove(currentItem);
            instockItem.add(edItem);
        }
        currentItem = edItem;

        String filename = "userFile.txt";

        // write
        OutputStream os = new FileOutputStream(filename);
        ObjectOutput oo = new ObjectOutputStream(os);
        oo.writeObject(users);
        oo.close();
        os.close();

        return ok(item.render(edItem, current));
    }

    /**
     * Shows receipt for single item in cart
     *
     * @return result the page being rendered
     */
    public Result itemPrint() { return ok(itemPrint.render(currentItem)); }

    /**
     * Shows receipt for all items in cart
     *
     * @return result the page being rendered
     */
    public Result printCatalog() { return ok(printCatalog.render(currentSale.getCatalogue())); }

    /**
     * Adds item to cart
     *
     * @return result the page being rendered
     */
    public Result addToCart() {
        if (currentItem.quantity > 0) {
            --currentItem.quantity;
            current.addToCart(currentItem);
        }
        return ok(checkout.render(current));

    }

    /**
     * Shows page for a transaction (after adding items to cart and checking out)
     *
     * @return result the page being rendered
     */
    public Result transaction() {
        Transaction newTran = Form.form(Transaction.class).bindFromRequest().get();
        double lowerLimitSum = 0;
        current.addTran(newTran);
        for (Item i: newTran.getItemList()) {
            lowerLimitSum += i.lowerLimit;
        }
        if (newTran.cash + newTran.card < lowerLimitSum) {
            current.removeTran(newTran);
            return ok(checkout.render(current));
        }
        return ok(transaction.render(current));
    }


    /**
     * Cancels a transaction
     * @param id the unique identifier for a transaction
     * @return result the page being rendered
     */

    public Result closeTran(String id) throws IOException {
        Transaction fin = new Transaction();
        for (Transaction t: current.getTran()) {
            if (t.tranID.equals(id)) {
                fin = t;
            }
        }
        for(Item i: fin.getItemList()) {
            if(i.quantity == 0) {
                if (!outOfStockItem.contains(i)) {
                    outOfStockItem.add(i);
                }
                instockItem.remove(i);
                for(Sale s: current.getSales()) {
                    if (s.getCatalogue().contains(i)) {
                        s.addSoldItem(i);
                        s.removeItem(i);
                    }
                }
            }
        }

        fin.setItemList(current.getCart());
        for (Item x : current.getCart()) {
            if (soldItem.contains(x)) {
                soldItem.get(soldItem.indexOf(x)).numSold++;
            } else {
                x.numSold++;
                soldItem.add(x);
            }
        }
        completedTransactions.add(fin);

        current.removeTran(fin);
        current.getCart().clear();
        current.total = 0.0;

        //filename
        String filename = "userFile.txt";

        // write
        OutputStream os = new FileOutputStream(filename);
        ObjectOutput oo = new ObjectOutputStream(os);
        oo.writeObject(users);
        oo.close();
        os.close();

        return ok(success.render(current));
    }

    /**
     * Displays a printable page summary of completed transaction
     * @param id unique identifier for transaction
     * @return result the page being rendered
     */
    public Result printReceipt(String id) {
        Transaction fin = new Transaction();
        for (Transaction t: current.getTran()) {
            if (t.tranID.equals(id)) {
                fin = t;
            }
        }
        return ok(printReceipt.render(fin,
                fin.getItemList(),
                current,
                currentSale));
    }

    /**
     * Adds an item to cart by id
     * @param inputID unique identifier for an item
     * @return
     */
    public Result addByItemID(int inputID) {
        boolean found = false;
        if (current.getSales() != null) {
            for (Sale sale : current.getSales()) {
                if (sale.catalogue != null && sale.getCatalogue().size() != 0) {
                    for (Item item : sale.catalogue) {
                        if (item.id == inputID) {
                            currentItem = item;
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        if (currentSale != null && currentItem != null) {
            if (found && currentItem.quantity > 0) {
                --currentItem.quantity;
                current.addToCart(currentItem);
            }
            return ok(checkout.render(current));
        } else {
            return ok(sales.render(current.getSales()));
        }

    }

    /**
     * show all user in the current system
     * @returnresult the page being rendered
     */

    public Result displayUsers() {
        return ok(displayUsers.render(users.values()));
    }

    /**
     * unlock a given user from the system admin
     * @param name match the username for that user to be unlocked
     * @returnresult the page being rendered
     */
    public Result unlock(String name) {
        User found = new User(null, null, null);
        for (User u: users.values()) {
            if(u.username.equals(name)) {
                found = u;
            }
        }
        found.status = User.Status.UNLOCKED;
        found.loginCount = 0;
        return ok(displayUsers.render(users.values()));
    }

    /**
     * Set user perspective roll assigned
     * @returnresult the page being rendered
     */
    public Result newUserRole() throws IOException {
        UserInfo newUserInfo = Form.form(UserInfo.class).bindFromRequest().get();
        User newUser = users.get(newUserInfo.username);
        if(newUser != null) {
            newUser.addRole(User.Role.valueOf(newUserInfo.role));
            currentSale.removeUser(newUser);
            currentSale.addUser(newUser);
            newUser.addSale(currentSale);
        }
        String filename = "userFile.txt";

        // write
        OutputStream os = new FileOutputStream(filename);
        ObjectOutput oo = new ObjectOutputStream(os);
        oo.writeObject(users);
        oo.close();
        os.close();
        return ok(sale.render(currentSale, currentSale.getCatalogue(), currentSale.userRoles, current));
    }

    /**
     * setuser to a specific role
     * @returnresult the page being rendered
     */
    public Result setSeller() {
        UserInfo newUserInfo = Form.form(UserInfo.class).bindFromRequest().get();
        User newUser = users.get(newUserInfo.username);
        currentItem.setSeller(newUser);
        if(newUser == null) {
            return ok(index.render());
        }
        return ok(item.render(currentItem, current));
    }

    /**
     * Sets lower limit of Item's possible price
     * @returnresult the page being rendered
     */
    public Result setLimit() {
        PriceInfo newPriceInfo = Form.form(PriceInfo.class).bindFromRequest().get();
        currentItem.setLimits(Double.parseDouble(newPriceInfo.lowerLimit));
        return ok(item.render(currentItem, current));
    }

    /**
     * display all sales that are open in the main screne of the admin profile
     * @returnresult the page being rendered
     */
    public Result displaySales(){
        allSale.clear();
        if (users != null) {
            for (User eachUser : users.values()) {
                if (eachUser != null) {
                    for (Sale event : eachUser.getSales()) {
                        if (event != null) {
                            if (event.isOpen()) {
                                allSale.add(event);
                            }
                        }
                    }
                }
            }
        }
        return ok(displaySales.render(allSale));
    }

    /**
     * Close an individuel sale by chaing the status of the sale:open to false
     * @param name match the name of the sale in the entire garage sale webpage
     * @returnresult the page being rendered, re-reder the update page because it somehow doesn't update
     */
    public Result closeSale(String name) throws IOException {
        User found = new User(null, null, null);

        if (users != null) {
            for (User eachUser : users.values()) {
                if (eachUser != null) {
                    for (Sale event : eachUser.getSales()) {
                        if (event.name.equals(name)) {
                            unsoldItem.addAll(event.getCatalogue());
                            event.setSaleStatus(false);
                        }
                    }
                }
            }
        }

        String filename = "userFile.txt";

        // write
        OutputStream os = new FileOutputStream(filename);
        ObjectOutput oo = new ObjectOutputStream(os);
        oo.writeObject(users);
        oo.close();
        os.close();

        allSale.clear();
        if (users != null) {
            for (User eachUser : users.values()) {
                if (eachUser != null) {
                    for (Sale event : eachUser.getSales()) {
                        if (event != null) {
                            if (event.isOpen()) {
                                allSale.add(event);
                            }
                        }
                    }
                }
            }
        }
        return ok(update.render(allSale));
    }

    /**
     * Generates report of each transaction by ID number
     * @param tranID the ID of the transaction(s) to be rendered
     * @returnresult the page being rendered
     */
    public Result reportEachTran(String tranID) {
        Transaction fin = new Transaction();
        for (Transaction t : completedTransactions) {
            if (tranID.equals(t.tranID)) {
                fin = t;
            }
        }

        return ok(reportEachTran.render(fin.getItemList()));
    }


    /**
     * Sets an imgur url to be the image source for an item
     * @param url the imgur direct url
     * @returnresult the page being rendered
     */
    public Result upload(String url) {
        currentItem.setImage(url);
        return ok(item.render(currentItem, current));
    }

    /**
     * Generates report of all completed transactions
     * @returnresult the page being rendered
     */
    public Result finanReport() {
        return ok(finanReport.render(completedTransactions));
    }

    /**
     * Generates reports of all instock items
     * @returnresult the page being rendered
     */
    public Result instockPage() {
        return ok(instockPage.render(instockItem));
    }

    /**
     * Generates reports of all out of stock items
     * @returnresult the page being rendered
     */
    public Result outOfStockPage() {
        return ok(outOfStockPage.render(outOfStockItem));
    }

    /**
     * Generates reports of all sold items
     * @returnresult the page being rendered
     */
    public Result soldPage() {
        return ok(soldPage.render(soldItem));
    }

    /**
     * Generates reports of all unsold items
     * @returnresult the page being rendered
     */
    public Result displayUnsold() {
        return ok(printCatalog.render(unsoldItem));
    }

    public Result bySeller(String sellerName) {
        User seller = users.get(sellerName);
        LinkedList<Item> sellerItems = new LinkedList<>();
        for(Item i: soldItem) {
            if(seller.equals(i.seller)) {
                sellerItems.add(i);
            }
        }
        return ok(printCatalog.render(sellerItems));
    }
}
