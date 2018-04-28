package com.jpa.demo.jpaProjections;


/**
 *
 * Closed projections
 * A projection interface whose accessor methods all match properties of the target aggregate are considered closed projections.
 * */
public interface CustomerProjection {

    String getFirstName();

}
