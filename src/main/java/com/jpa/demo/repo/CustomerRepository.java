package com.jpa.demo.repo;

import com.jpa.demo.entities.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.NamedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


/**
 * spring data searches for the implementation with IMPL postfix and uses it as implementation at run time
 * so it searches for CustomJPARepositoryImpl
 * and provides the method inside it
 *
 * all supported query keywords
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
 * */
public interface CustomerRepository extends CrudRepository<Customer,Long> , CustomJPARepository<Customer> {

    List<Customer> findByLastName(String lastName);

    //named query defined in customer.java
    Optional<Customer> findByFirstAndLastName(String firstName, String lastName);


    @Query("Select C from Customer C where C.firstName like %?1%")
    Optional<List<Customer>> findByFirstNameContaining(String fragment);



    // if there is a conflict between a namedQuery and query, query is given priority




}
