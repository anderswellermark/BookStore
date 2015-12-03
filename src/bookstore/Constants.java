/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 *
 * @author Anders
 */
public class Constants {
   public static final String BOOKS_URL = "http://www.contribe.se/bookstoredata/bookstoredata.txt";
   public static final Locale DECIMAL_FORMAT_LOCALE = Locale.US;
   public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
   public static final String CURRENCY_SYMBOL = "$";
   public static final String ADD_TO_CART_BTN_TEXT = "Add to Cart";
   public static final String REMOVE_BTN_TEXT = "Remove";
   public static final String CHECKOUT_INFO_HEADLINE = "Checkout information";
   public static final String ITEMS_MISSING_ANNOUNCEMENT = "The following items are missing and will be sent in a later delivery: \n\n";
   public static final String CHECKOUT_INFO_ENDTEXT = "\nYou will now be redirected to our payment provider\n<TO BE IMPLEMENTED>";
   public static final String EMPTY_INVENTORY_TEXT = "No items found";
   public static final String EMPTY_CART_TEXT = "No items in cart";
   public static final String SEARCH_STATUS_BAR_TEXT = "Search matches per word: ";
   public static final String CART_DIALOG_HEADER = "Add book(s) to cart";
   public static final String CART_DIALOG_TEXT = "Please enter how many pcs";
   public static final String EMPTY_STOCK_DIALOG_HEADER = "Not enough items in stock";
   public static final String EMPTY_STOCK_DIALOG_TEXT = "If you proceed with your order, some items will be sent in a separate delivery."; 
}