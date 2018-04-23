package com.jpa.demo.repo;


import com.jpa.demo.entities.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 * The class that implements  should contain the IMPL postfix for spring recognise and provide implementation during run time
 *
 * */
/**
 * mimic how spring data provides implementation durinhg runtime when we extend to JPA REPO
 * spring data searches for the implementation with IMPL postfix and uses it as implementation at run time
 * */

// add <T> as <Customer>
public class CustomJPARepositoryImpl implements CustomJPARepository<Customer> {

    private  static  final Logger log = LoggerFactory.getLogger(CustomJPARepositoryImpl.class);

    @Autowired
    JdbcTemplate jdbc;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Customer> retriveAll() {
        log.info("query being executed from customrepoimpl");
        return jdbc.query("Select * from customers c order by c.customer_id",(rs, rowNum) -> {
           return new Customer(rs.getLong("customer_id"),rs.getString("first_name"), rs.getString("last_name"));
        });

    }

    @Override
    @Transactional
    public Customer save(Customer entity) {
        log.info("saving entity "+ entity);
        entityManager.persist(entity);
        return entity;


    }
}
