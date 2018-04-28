package com.jpa.demo.entities;

import org.hibernate.annotations.CollectionId;

import javax.persistence.*;

@Embeddable
public class Address {

//    @Id
////    @SequenceGenerator(name="address_id_generator",sequenceName = "generate_address_seq",initialValue = 1,allocationSize = 100)
////    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "address_id_generator")
////    private long id;
    private String state;
    private String country;


    protected Address(){};

    public Address(String state, String country) {
        this.state = state;
        this.country = country;

    }


//    public long getId() {
//        return id;
//    }


    public String getState() {
        return state;
    }


    public String getCountry() {
        return country;
    }


    @Override
    public String toString() {
        return "Address{" +
//                "id=" + id +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                '}';
    }


}
