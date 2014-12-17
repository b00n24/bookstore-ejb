package org.books.application.service;

import java.util.List;
import javax.ejb.Stateless;
import org.books.application.exception.BookNotFoundException;
import org.books.persistence.entity.Book;

@Stateless(name = "CatalogService")
public class CatalogServiceBean implements CatalogService {

    @Override
    public Book findBook(Long bookId) throws BookNotFoundException {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Book findBook(String isbn) throws BookNotFoundException {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Book> searchBooks(String keywords) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
