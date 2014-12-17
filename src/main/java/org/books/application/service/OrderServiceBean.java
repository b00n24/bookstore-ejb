package org.books.application.service;

import java.util.List;
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
import org.books.persistence.entity.LineItem;
import org.books.persistence.entity.Order;
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

    @PostConstruct
    public void initialize() {
	orderRepository = new OrderRepository(em);
    }

    @Override
    public void cancelOrder(Long orderId) throws OrderNotFoundException, InvalidOrderStatusException {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Order findOrder(Long orderId) throws OrderNotFoundException {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Order findOrder(String number) throws OrderNotFoundException {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public OrderInfo placeOrder(Long customerId, List<LineItem> items) throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<OrderInfo> searchOrders(Long customerId, Integer year) throws CustomerNotFoundException {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
