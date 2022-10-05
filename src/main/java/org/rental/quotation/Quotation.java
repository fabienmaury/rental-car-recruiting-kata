package org.rental.quotation;

public class Quotation {

    private final String carId;
    private final double dailyPrice;
    private final int nbDays;

    public Quotation(String carId, double dailyPrice, int nbDays) {
        this.carId = carId;
        this.dailyPrice = dailyPrice;
        this.nbDays = nbDays;
    }

    public String getCarId() {
        return carId;
    }

    public double getDailyPrice() {
        return dailyPrice;
    }

    public int getNbDays() {
        return nbDays;
    }

    @Override
    public String toString() {
        return "Quotation{" +
                "carId='" + carId + '\'' +
                ", dailyPrice=" + dailyPrice +
                ", nbDays=" + nbDays +
                '}';
    }
}
