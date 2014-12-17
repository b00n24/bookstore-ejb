package org.books.application.service;

import java.util.List;
import javax.ejb.Remote;
import org.books.application.exception.BookNotFoundException;
import org.books.persistence.entity.Book;

/**
 *
 * @author AWy
 */
@Remote
public interface CatalogService {
    Book findBook(Long bookId) throws BookNotFoundException;
    Book findBook(String isbn) throws BookNotFoundException;
    List<Book> searchBooks(String keywords);
}
