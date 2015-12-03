/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore;

import bookstore.model.Book;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.text.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Anders
 */
public class BookUnmarshaller {

    public static Book[] BooksFromCsv(String url) {
        InputStream in = null;
        Book[] books;
        try {
            in = new URL(url).openStream();
            books = BooksFromString(IOUtils.toString(in, Charset.forName("utf-8")));
        } catch (IOException | ParseException | NumberFormatException e) {
            return null;
        } finally {
            IOUtils.closeQuietly(in);
        }
        return books;
    }
    
    public static Book[] BooksFromCsv(File file) {
        InputStream in = null;
        Book[] books;
        try {
            in = FileUtils.openInputStream(file);
            books = BooksFromString(IOUtils.toString(in, Charset.forName("utf-8")));
        } catch (IOException | ParseException | NumberFormatException e) {
            return null;
        } finally {
            IOUtils.closeQuietly(in);
        }
        return books;
    }

    private static Book[] BooksFromString(String str) throws ParseException {
        String[] rows = str.split("\n");
        Book[] books = new Book[rows.length];
        String[] row;
        for (int i = 0; i < rows.length; i++) {
            row = rows[i].split(";");
            if (row.length == 4) {
                double price = NumberFormat.getNumberInstance(Constants.DECIMAL_FORMAT_LOCALE).parse(row[2]).doubleValue();
                books[i] = new Book(row[0], row[1], price, Integer.parseInt(row[3]));
            }
        }
        return books;
    }

}
