package org.books.application.service;

import java.util.List;
import org.books.persistence.entity.Book;

/**
 *
 * @author Silvan
 */
public interface BookService {
    
    Book getBookByISBN(String isbn);
    
    List<Book> searchBooks(String keywords);
}
