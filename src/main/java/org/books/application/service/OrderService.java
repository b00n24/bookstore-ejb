package org.books.application.service;

import java.util.List;
import org.books.persistence.dto.OrderInfo;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order;

/**
 *
 * @author Silvan
 */
public interface OrderService {
    
    Order getOrderByNumber(String number);
    
    List<OrderInfo> getOrders(Customer customer, int year);
}
