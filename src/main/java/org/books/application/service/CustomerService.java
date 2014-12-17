package org.books.application.service;

import java.util.List;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Login;

/**
 *
 * @author Silvan
 */
public interface CustomerService {
    
    List<CustomerInfo> searchCustomersByName(String name);
    
    Login getLoginByUserName(String userName);

    Customer getCustomerByMail(String email);
}
