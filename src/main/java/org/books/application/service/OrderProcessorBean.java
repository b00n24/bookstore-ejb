package org.books.application.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import org.books.persistence.entity.Order;
import org.books.persistence.enums.Status;
import org.books.persistence.service.OrderRepository;

/**
 *
 * @author AWy
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/orderQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class OrderProcessorBean implements MessageListener {

    @PersistenceContext(unitName = "bookstore", type = PersistenceContextType.TRANSACTION)
    private EntityManager em;
    @Resource
    private TimerService timerService;
    @Inject
    private MailBean mailBean;

    private static final Long SIMULATED_PROCESSING_TIME_IN_MILLIS = 60 * 1000L;
    private static final String PARAM_ORDER_ID = "orderId";
    private static final Logger LOGGER = Logger.getLogger(OrderProcessorBean.class.getName());

    private OrderRepository orderRepository;

    @PostConstruct
    public void initialize() {
	orderRepository = new OrderRepository(em);
    }

    @Override
    public void onMessage(Message message) {
	if (message instanceof MapMessage) {
	    MapMessage mapMessage = (MapMessage) message;
	    Long orderId = 0L;
	    try {
		orderId = mapMessage.getLong(PARAM_ORDER_ID);
		final Order order = orderRepository.findById(orderId);
		LOGGER.log(Level.FINE, "Setting status to processing for orderId {0}", orderId);
		order.setStatus(Status.processing);
		orderRepository.update(order);
		sendMail(order);
	    } catch (JMSException ex) {
		LOGGER.log(Level.SEVERE, "Could not process order with orderId " + orderId, ex);
	    }

	    timerService.createSingleActionTimer(SIMULATED_PROCESSING_TIME_IN_MILLIS,
		    new TimerConfig(orderId, true));
	}
    }

    @Timeout
    public void timer(Timer timer) {
	Object obj = timer.getInfo();
	if (obj instanceof Long) {
	    Long orderId = (Long) obj;
	    final Order order = orderRepository.findById(orderId);
	    if (order.getStatus() == Status.processing) {
		LOGGER.log(Level.FINE, "Setting status to shipped for orderId {0}", orderId);
		order.setStatus(Status.shipped);
		orderRepository.update(order);
		sendMail(order);
	    } else {
		LOGGER.log(Level.WARNING, "Invalid order status for orderId {0}. Status is set to {1}", new Object[]{orderId, order.getStatus()});
	    }
	} else {
	    throw new IllegalArgumentException();
	}
    }

    private void sendMail(final Order order) {
	try {
	    mailBean.sendMail(order.getCustomer().getEmail(), "Order Status changed",
		    "Your order status changed to " + order.getStatus() + " for order " + order.getNumber());
	} catch (MessagingException ex) {
	    Logger.getLogger(OrderServiceBean.class.getName()).log(Level.WARNING, "Could not send email", ex);
	}
    }

}
