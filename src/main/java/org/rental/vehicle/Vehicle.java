package org.rental.vehicle;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id) && status == vehicle.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status);
    }
}
