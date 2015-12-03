/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore.model;

/**
 *
 * @author Anders
 */
public interface BookList {
    public Book[] list(String searchString);
    public boolean add(Book book, int amount);
    public int[] buy(Book... books);
}
