package com.jpa.demo.jpaProjections;


import org.springframework.beans.factory.annotation.Value;

/**
 *
 * Open projections
 * this can be done when the projection fields doesnt match the actual entity fields
 * Accessor methods in projection interfaces can also be used to compute new values by using the @Value annotation on it:
 * */
public interface CustomerOpenProjection {

    @Value("#{target.firstName + ' '+ target.lastName }")
    String getFullName();

}
