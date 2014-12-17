package org.books.application.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.books.persistence.QueryUtil;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.Book_;

public class BookServiceImpl implements BookService {

    private final EntityManager em;

    public BookServiceImpl(EntityManagerFactory emf) {
	em = emf.createEntityManager();
    }

    @Override
    public Book getBookByISBN(String isbn) {
	Validate.notEmpty(isbn, "'isbn' darf nicht leer sein");
	TypedQuery<Book> query = em.createNamedQuery(Book.QUERY_BY_ISBN, Book.class);
	query.setParameter(Book.PARAM_ISBN, isbn.toLowerCase());

	return query.getSingleResult();
    }

    @Override
    public List<Book> searchBooks(String keywords) {
	Validate.notEmpty(keywords, "'keywords' darf nicht leer sein");

	CriteriaBuilder cb = em.getCriteriaBuilder();
	CriteriaQuery<Book> q = cb.createQuery(Book.class);
	Root<Book> book = q.from(Book.class);

	CriteriaQuery<Book> crit = q.select(book);

	Expression<String> titlePath = book.get(Book_.title);
	Expression<String> authorPath = book.get(Book_.authors);
	Expression<String> publisherPath = book.get(Book_.publisher);

	String[] keywordsArray = StringUtils.split(keywords, ' ');
	Predicate[] predicates = new Predicate[keywordsArray.length];
	for (int i = 0; i < keywordsArray.length; i++) {
	    String convertedKeyword = QueryUtil.convertToLikeValueAndLowerCase(keywordsArray[i]);
	    predicates[i] = cb.or(cb.like(cb.lower(titlePath), convertedKeyword), cb.like(cb.lower(authorPath), convertedKeyword), cb.like(cb.lower(publisherPath), convertedKeyword));
	}
	crit.where(cb.and(predicates));
	TypedQuery<Book> query = em.createQuery(q);
	return query.getResultList();

    }

}
