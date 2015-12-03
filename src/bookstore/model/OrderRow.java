/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore.model;

import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Anders
 */
public class OrderRow {
    
    private SimpleIntegerProperty pcs = new SimpleIntegerProperty();
    private Book book;
    
    public int getPcs() {
        return pcs.get();
    }
    
    public void setPcs(int pcs) {
        this.pcs.set(pcs);
    }
    
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }
}
