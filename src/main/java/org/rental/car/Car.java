package org.rental.car;

import org.rental.vehicle.RentalStatus;
import org.rental.vehicle.VehicleType;

import java.util.List;

public class Car {

    private final String id;
    private final String label;
    private final double dailyBasePrice;
    private final double dailyWeekEndPrice;
    private final VehicleType type;
    private final RentalStatus status;
    private final List<String> options;

    public Car(String id, String label, double dailyBasePrice, double dailyWeekEndPrice, VehicleType type, RentalStatus status, List<String> options) {
        this.id = id;
        this.label = label;
        this.dailyBasePrice = dailyBasePrice;
        this.dailyWeekEndPrice = dailyWeekEndPrice;
        this.type = type;
        this.status = status;
        this.options = options;
    }

    public String getId() {
        return id;
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

    public RentalStatus getStatus() {
        return status;
    }

    public VehicleType getType() {
        return type;
    }
}
