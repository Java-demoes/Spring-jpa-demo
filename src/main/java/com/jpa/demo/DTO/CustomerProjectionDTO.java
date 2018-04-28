package com.jpa.demo.DTO;

import java.util.Objects;

/**
 * Class-based projections (DTOs)
 * Another way of defining projections is using value type DTOs that hold properties for the fields that are supposed to be retrieved.
 * These DTO types can be used exactly the same way projection interfaces are used,
 * except that no proxying is going on here and no nested projections can be applied.
 *
 * */
public class CustomerProjectionDTO {

    private String firstName;
    private String lastName;

    public CustomerProjectionDTO(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerProjectionDTO)) return false;
        CustomerProjectionDTO that = (CustomerProjectionDTO) o;
        return Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(firstName, lastName);
    }

    @Override
    public String toString() {
        return "CustomerProjectionDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
