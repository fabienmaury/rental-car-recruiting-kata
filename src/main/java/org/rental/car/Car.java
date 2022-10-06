package org.rental.car;

import org.rental.vehicle.RentalStatus;
import org.rental.vehicle.VehicleType;
import org.rental.vehicle.Vehicle;

import java.util.List;

public class Car extends Vehicle {

    private final String label;
    private final double dailyBasePrice;
    private final double dailyWeekEndPrice;
    private final CarType type;
    private final List<String> options;

    public Car(String id, String label, double dailyBasePrice, double dailyWeekEndPrice, CarType type, RentalStatus status, List<String> options) {
        super(id, status);
        this.label = label;
        this.dailyBasePrice = dailyBasePrice;
        this.dailyWeekEndPrice = dailyWeekEndPrice;
        this.type = type;
        this.options = options;
    }

    public String getLabel() {
        return label;
    }

    public double getDailyBasePrice() {
        return dailyBasePrice;
    }

    public double getDailyWeekEndPrice() {
        return dailyWeekEndPrice;
    }

    public List<String> getOptions() {
        return options;
    }

    public CarType getType() {
        return type;
    }
}
