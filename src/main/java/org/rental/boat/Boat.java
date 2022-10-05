package org.rental.boat;

import org.rental.vehicle.RentalStatus;
import org.rental.vehicle.Vehicle;

public class Boat extends Vehicle {

    private final String label;
    private final double dailyBasePrice;
    private final BoatType type;

    public Boat(String id, RentalStatus status, String label, double dailyBasePrice, BoatType type) {
        super(id, status);
        this.label = label;
        this.dailyBasePrice = dailyBasePrice;
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public double getDailyBasePrice() {
        return dailyBasePrice;
    }

    public BoatType getType() {
        return type;
    }
}
