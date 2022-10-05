package org.rental;

import org.rental.agency.AgenciesRepository;
import org.rental.agency.Agency;
import org.rental.agency.AgencyCarsRepository;
import org.rental.car.Car;
import org.rental.car.CarStatus;
import org.rental.car.CarType;
import org.rental.quotation.Quotation;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RentalService {

    private final AgenciesRepository agenciesRepository;
    private final AgencyCarsRepository carsRepository;

    private LocalDateTime current;

    private int nbDays = 0;
    private double discount;

    public RentalService(AgenciesRepository agenciesRepository, AgencyCarsRepository carsRepository) {
        this.agenciesRepository = agenciesRepository;
        this.carsRepository = carsRepository;
    }

    public List<Quotation> search(String postalCode, LocalDateTime from, LocalDateTime to, List<CarType> filter, String currency, List<String> options) {
        List<Agency> agencies = agenciesRepository.findNearest(postalCode);
        List<Quotation> results = new ArrayList<>();
        for (Agency agency : agencies) {
            List<Car> cars = carsRepository.getCarStatus(agency, from, to);
            for (Car car : cars) {
                if ((!filter.isEmpty() && !filter.contains(car.getType())) || !car.getStatus().equals(CarStatus.AVAILABLE)) {
                    continue;
                }
                for (String option : options) {
                    if(!car.getOptions().contains(option)){
                        continue;
                    }
                }
                current = from;
                double price = 0.0;
                nbDays = 0;
                discount = 0.0;
                while (current.isBefore(to)) {
                    nbDays += 1;
                    if (current.getDayOfWeek().getValue() < 6) {
                        price += car.getDailyBasePrice();
                    } else {
                        price += car.getDailyWeekEndPrice();
                    }
                    getDiscount();
                    current = current.plusDays(1);
                }
                price = price - (price * discount);
                price = convert(price, currency);
                results.add(new Quotation(car.getId(), price / nbDays, nbDays));
            }
        }
        return results;
    }

    private void getDiscount() {
        if (nbDays == 2) {
            discount = 0.1;
        }
        if (nbDays == 3) {
            discount = 0.15;
        }
        if (nbDays == 4) {
            discount = 0.20;
        }
        if (nbDays == 5) {
            discount = 0.23;
        }
        if (nbDays == 6) {
            discount = 0.25;
        }
        if (nbDays == 7) {
            discount = 0.26;
        }
    }

    private double convert(double price, String currency) {
        switch (currency) {
            case "EUR":
                return price;
            case "USD":
                return price * 0.99838461369504;
            case "GBP":
                return price * 0.87186931544761;
            case "BTC":
                return price * 4.9079636148711E-5;
        }
        return price;
    }
}
