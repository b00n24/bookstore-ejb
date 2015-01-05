package org.books.application.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.InvalidOrderStatusException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.dto.OrderItem;
import org.books.persistence.entity.Book;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.LineItem;
import org.books.persistence.entity.Order;
import org.books.persistence.enums.Status;
import org.books.persistence.service.BookRepository;
import org.books.persistence.service.CustomerRepository;
import org.books.persistence.service.OrderRepository;

/**
 *
 * @author AWy
 */
@Stateless(name = "OrderService")
public class OrderServiceBean implements OrderService {

    @PersistenceContext(unitName = "bookstore", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;
    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;
    private BookRepository bookRepository;

    @PostConstruct
    public void initialize() {
	orderRepository = new OrderRepository(em);
	customerRepository = new CustomerRepository(em);
	bookRepository = new BookRepository(em);
    }

    @Override
    public void cancelOrder(Long orderId) throws OrderNotFoundException, InvalidOrderStatusException {
	// TODO : Check ob hier getBusinessObject benutzt weden muss???
	final Order order = findOrder(orderId);
	if (order.getStatus() == Status.DELIVERED) {
	    throw new InvalidOrderStatusException();
	}
	order.setStatus(Status.CANCELED);

	orderRepository.update(order);
    }

    @Override
    public Order findOrder(Long orderId) throws OrderNotFoundException {
	final Order order = orderRepository.findById(orderId);
	if (order == null) {
	    throw new OrderNotFoundException();
	}
	return order;
    }

    @Override
    public Order findOrder(String number) throws OrderNotFoundException {
	final Order order = orderRepository.findByNumber(number);
	if (order == null) {
	    throw new OrderNotFoundException();
	}
	return order;
    }

    @Override
    public OrderInfo placeOrder(Long customerId, List<OrderItem> items) throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException {
	Customer customer = getCustomer(customerId);

	Order order = new Order();
	order.setAddress(customer.getAddress());
	order.setCreditCard(customer.getCreditCard());
	order.setCustomer(customer);
	order.setDate(new Date());
	// TODO Check ob anders generieren
	order.setNumber(UUID.randomUUID().toString());
	order.setStatus(Status.PROCESSING);
	
	BigDecimal amount = new BigDecimal(BigInteger.ZERO);
	for (OrderItem item : items) {
	    Book book = bookRepository.findByISBN(item.getIsbn());
	    if (book == null) {
		throw new BookNotFoundException();
	    }
	    BigDecimal curAmount = book.getPrice().multiply(new BigDecimal(item.getQuantity()));
	    amount.add(curAmount);
	    order.getItems().add(new LineItem(book, item.getQuantity()));
	}
	order.setAmount(amount);
	orderRepository.update(order);
	
	//TODO payment
	
	OrderInfo orderInfo = new OrderInfo(customerId, order.getNumber(), order.getDate(), order.getAmount(), order.getStatus());
	return orderInfo;
    }

    @Override
    public List<OrderInfo> searchOrders(Long customerId, Integer year) throws CustomerNotFoundException {
	Customer customer = getCustomer(customerId);
	return orderRepository.getOrders(customer, year);
    }

    private Customer getCustomer(Long customerId) throws CustomerNotFoundException {
	final Customer customer = customerRepository.findById(customerId);
	if (customer == null) {
	    throw new CustomerNotFoundException();
	}
	return customer;
    }

}
