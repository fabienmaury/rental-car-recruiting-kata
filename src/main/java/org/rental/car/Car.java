package org.rental.car;

import java.util.List;

public class Car {

    private final String id;
    private final String label;
    private final double dailyBasePrice;
    private final double dailyWeekEndPrice;
    private final CarType type;
    private final CarStatus status;
    private final List<String> options;

    public Car(String id, String label, double dailyBasePrice, double dailyWeekEndPrice, CarType type, CarStatus status, List<String> options) {
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

    public CarStatus getStatus() {
        return status;
    }

    public CarType getType() {
        return type;
    }
}
