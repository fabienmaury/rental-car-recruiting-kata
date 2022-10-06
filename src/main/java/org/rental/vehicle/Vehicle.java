package org.rental.vehicle;

import org.rental.car.RentalStatus;

public class Vehicle {

    private final String id;

    private final RentalStatus status;

    public Vehicle(String id, RentalStatus status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public RentalStatus getStatus() {
        return status;
    }
}
