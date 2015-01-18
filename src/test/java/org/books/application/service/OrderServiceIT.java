package org.books.application.service;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.SystemException;
import junit.framework.Assert;
import org.books.application.exception.BookNotFoundException;
import org.books.application.exception.CustomerNotFoundException;
import org.books.application.exception.InvalidOrderStatusException;
import org.books.application.exception.OrderNotFoundException;
import org.books.application.exception.PaymentFailedException;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.dto.OrderItem;
import org.books.persistence.entity.Order;
import org.books.persistence.enums.Status;
import org.dbunit.IDatabaseTester;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 *
 * @author Silvan
 */
public class OrderServiceIT {

    private static final String SERVICE_NAME = "java:global/bookstore-ejb/OrderService";
    private static final String DB_UNIT_PROPERTIES = "/dbunit.properties";
    private static final String DB_UNIT_DATASET = "/dataset.xml";

    private OrderService service;
    private EntityManager em;

    private static final String ISBN = "0596009208";
    private static final Long CUSTOMER_ID = 101l;
    private static final Long ORDER_ID = 101l;
    private static final String ORDER_NUMBER = "111";

    @BeforeTest
    public void setUpClass() throws Exception {
	EntityManagerFactory emf = Persistence.createEntityManagerFactory("bookstore");
	em = emf.createEntityManager();
	initDatabase();
	service = (OrderService) new InitialContext().lookup(SERVICE_NAME);
    }

    private void initDatabase() throws Exception {
	System.getProperties().load(getClass().getResourceAsStream(DB_UNIT_PROPERTIES));
	IDatabaseTester databaseTester = new PropertiesBasedJdbcDatabaseTester();
	IDataSet dataset = new FlatXmlDataSetBuilder().build(getClass().getResourceAsStream(DB_UNIT_DATASET));
	databaseTester.setDataSet(dataset);
	databaseTester.onSetup();
    }
    
    public void cancelOrder() throws OrderNotFoundException, InvalidOrderStatusException {
	// WHEN
	service.cancelOrder(ORDER_ID);

	// THEN
	Order result = service.findOrder(ORDER_ID);
	Assert.assertEquals(Status.canceled, result.getStatus());

	// Revert
	em.getTransaction().begin();
	Order o = service.findOrder(ORDER_ID);
	o.setStatus(Status.processing);
	em.merge(o);
	em.flush();
	em.getTransaction().commit();
    }

    @Test(expectedExceptions = OrderNotFoundException.class)
    public void cancelOrder_notExisitng() throws OrderNotFoundException, InvalidOrderStatusException {
	// WHEN
	service.cancelOrder(55533l);
    }

    @Test
    public void cancelOrder_statusAccepted() throws OrderNotFoundException, InvalidOrderStatusException {
	// WHEN
	service.cancelOrder(102l);

	// THEN
	Order result = service.findOrder(102l);
	Assert.assertEquals(Status.canceled, result.getStatus());

	// Revert
	em.getTransaction().begin();
	Order o = service.findOrder(102l);
	o.setStatus(Status.accepted);
	em.merge(o);
	em.flush();
	em.getTransaction().commit();
    }

    @Test(expectedExceptions = InvalidOrderStatusException.class)
    public void cancelOrder_statusShipped_invalidStatus() throws OrderNotFoundException, InvalidOrderStatusException {
	// WHEN
	service.cancelOrder(103l);
    }

    @Test(expectedExceptions = InvalidOrderStatusException.class)
    public void cancelOrder_statusCanceled_invalidStatus() throws OrderNotFoundException, InvalidOrderStatusException {
	// WHEN
	service.cancelOrder(104l);
    }

    @Test
    public void findOrderById() throws OrderNotFoundException {
	// WHEN
	Order result = service.findOrder(ORDER_ID);

	// THEN
	Assert.assertNotNull(result);
    }

    @Test(expectedExceptions = OrderNotFoundException.class)
    public void findOrderById_wrongId() throws OrderNotFoundException {
	// WHEN
	service.findOrder(3332l);
    }

    @Test
    public void findOrderByOrderNumber() throws OrderNotFoundException {
	// WHEN
	Order result = service.findOrder(ORDER_NUMBER);

	// THEN
	Assert.assertNotNull(result);
    }

    @Test(expectedExceptions = OrderNotFoundException.class)
    public void findOrderByOrderNumber_wrongNumber() throws OrderNotFoundException {
	// WHEN
	service.findOrder("543434ss");
    }

    @Test
    public void placeOrder() throws CustomerNotFoundException, BookNotFoundException, PaymentFailedException{
	List<OrderItem> orderItems = new LinkedList();
	OrderItem orderItem = new OrderItem();
	orderItem.setIsbn(ISBN);
	orderItem.setQuantity(1);
	orderItems.add(orderItem);

	// WHEN
	OrderInfo order = service.placeOrder(CUSTOMER_ID, orderItems);

	// THEN
	String createdOrderNmber = order.getNumber();
	Assert.assertNotNull(createdOrderNmber);

    }

    @Test
    public void searchOrders_shouldFind2() throws CustomerNotFoundException {
	// WHEN
	final List<OrderInfo> result = service.searchOrders(CUSTOMER_ID, 2013);

	// THEN
	Assert.assertNotNull(result);
	Assert.assertEquals(2, result.size());
	Calendar cal = Calendar.getInstance();
	cal.setTime(result.get(0).getDate());
	Assert.assertEquals(2013, cal.get(Calendar.YEAR));
	cal.setTime(result.get(1).getDate());
	Assert.assertEquals(2013, cal.get(Calendar.YEAR));
    }
}
