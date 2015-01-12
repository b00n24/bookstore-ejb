package org.books.application.service;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.application.service.OrderService;
import org.books.persistence.dto.OrderItem;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order;
import org.books.persistence.enums.Binding;
import org.books.persistence.service.BookRepository;
import org.books.persistence.service.CustomerRepository;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author Silvan
 */
public class OrderServiceIT {

    private static final String ORDER_SERVICE_NAME = "java:global/bookstore-ejb/OrderService";
    private static final String CUSTOMER_SERVICE_NAME = "java:global/bookstore-ejb/CustomerService";
    
    @PersistenceContext(unitName = "bookstore", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    private OrderService orderService;
    private CustomerService customerService;
    
    private Long customerId;
    private Book book;

    @BeforeClass
    public void setUpClass() throws Exception {
	orderService = (OrderService) new InitialContext().lookup(ORDER_SERVICE_NAME);
	customerService = (CustomerService) new InitialContext().lookup(CUSTOMER_SERVICE_NAME);
//	EntityManager em;
	
//	CustomerRepository customerRepo = new CustomerRepository(em);
//	Customer customer = new Customer("firstName", "lastName", "email@address.ch", null, null);
//	customerRepo.persist(customer);
//	customerId = customer.getId();

//	BookRepository bookRepo = new BookRepository(em);
//	this.book = new Book("isbn", "title", "authors", "publisher", 2000, Binding.Hardcover, 100, BigDecimal.TEN);
//	em.persist(book);
	
	customerId = customerService.registerCustomer(new Customer("firstName", "lastName", "email@address.ch", null, null), "password");
//	accountNr = accountService.openAccount(pin);
//	accountManager = (AccountManager) new InitialContext().lookup(ACCOUNT_MANAGER_NAME);
    }

    @AfterClass
    public void tearDownClass() throws Exception {
    }

    @Test
    public void placeOrder() throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException {
	List<OrderItem> orderItems = new LinkedList();
	orderItem = new OrderItem();
	orderItem.setIsbn(book.getIsbn());
	orderItems.add(orderItem);
	orderService.placeOrder(customerId, orderItems);
    }
    private OrderItem orderItem;
}
