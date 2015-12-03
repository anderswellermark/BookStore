/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore.model;

import bookstore.Constants;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Anders
 */
public class Book {
    
    private int id;
    public SimpleStringProperty title = new SimpleStringProperty();
    public SimpleStringProperty author = new SimpleStringProperty();
    public SimpleObjectProperty<BigDecimal> price = new SimpleObjectProperty();
    public SimpleIntegerProperty stockBalance = new SimpleIntegerProperty();
    
    public Book(int id, String title, String author, double price, int stockBalance) {
        this.id = id;
        this.title.set(title);
        this.author.set(author);
        this.price.set(BigDecimal.valueOf(price));
        this.stockBalance.set(stockBalance);
    }
    
    public int getId() {
        return id;
    }
    
    public String getTitle() {
        return title.get();
    }
    
    public String getAuthor() {
        return author.get();
    }
    
    public String getPrice() {
        DecimalFormat decim = Constants.DECIMAL_FORMAT;
        return decim.format(getPriceDouble()) + Constants.CURRENCY_SYMBOL;
    }
    
    public double getPriceDouble() {
        return price.get().doubleValue();
    } 
  
    public int getStockBalance() {
        return stockBalance.get();
    }
    
}
