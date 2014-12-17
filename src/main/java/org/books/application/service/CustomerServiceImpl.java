package org.books.application.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.Validate;
import org.books.persistence.QueryUtil;
import org.books.persistence.dto.CustomerInfo;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Login;

public class CustomerServiceImpl implements CustomerService {


    private final EntityManager em;

    public CustomerServiceImpl(EntityManagerFactory emf) {
	em = emf.createEntityManager();
    }

    @Override
    public List<CustomerInfo> searchCustomersByName(String name) {
	Validate.notEmpty(name, "'name' darf nicht leer sein");

	TypedQuery<CustomerInfo> query = em.createNamedQuery(Customer.QUERY_BY_NAME, CustomerInfo.class);
	query.setParameter(Customer.PARAM_NAME, QueryUtil.convertToLikeValueAndLowerCase(name));

	return query.getResultList();
    }

    @Override
    public Customer getCustomerByMail(String email) {
	Validate.notEmpty(email, "'email' darf nicht leer sein");
	TypedQuery<Customer> query = em.createNamedQuery(Customer.QUERY_BY_EMAIL, Customer.class);
	query.setParameter(Customer.PARAM_EMAIL, email.toLowerCase());

	return query.getSingleResult();
    }

    @Override
    public Login getLoginByUserName(String userName) {
	Validate.notEmpty(userName, "'userName' darf nicht leer sein");
	TypedQuery<Login> query = em.createNamedQuery(Login.QUERY_BY_NAME, Login.class);
	query.setParameter(Login.PARAM_NAME, userName.toLowerCase());

	return query.getSingleResult();
    }

}
