package com.jpa.demo.repo;

import com.jpa.demo.entities.Customer;

import java.util.List;

/**
 *
 * The class that implements  should contain the IMPL postfix for spring recognise and provide implementation during run time
 *
 * */

/**
 * mimic how spring data provides implementation durinhg runtime when we extend to JPA REPO
 *
 * */
public interface CustomJPARepository<T> {

  Iterable<T> retriveAll();

  //PROVIDING CUSTOM SAVE IMPLEMENATION
  // this has higher precendence over save method from CRUD REPO
  // if return type is defined as T directly, we can either use T class or a sub class of T
  // when u say S extends T, a  DTO can be passed  which need not be extending to T,
  // the save method however returns an new object which is extended to T
    <S extends T> S save(S entity);


}
