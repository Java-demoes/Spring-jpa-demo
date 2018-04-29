package com.jpa.demo.SPECS;

import com.jpa.demo.entities.Customer;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class CustomerSpecs {

    public static Specification<Customer> NameContains = (Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder builder) ->
            builder.like(root.get("firstName"),"%P%");


    public static Specification<Customer> NameDoesntContain = (Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder builder) ->
            builder.notLike(root.get("firstName"),"%P%");

    public static Specification<Customer> NameContainsR = (Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder builder) ->
            builder.like(root.get("firstName"),"%r%");

}
