package com.jpa.demo.entities;

import org.hibernate.annotations.CollectionId;

import javax.persistence.*;

@Entity
@Table(name="address")
public class Address {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    private String state;
    private String country;

    protected Address(){};

    public Address(String state, String country) {
        this.state = state;
        this.country = country;

    }


    public long getId() {
        return id;
    }


    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }


    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                '}';
    }


}
