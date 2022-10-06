package org.rental;

import org.rental.agency.AgenciesRepository;
import org.rental.agency.Agency;
import org.rental.agency.AgencyCarsRepository;
import org.rental.agency.PortAgencyBoatRepository;
import org.rental.boat.Boat;
import org.rental.boat.BoatType;
import org.rental.car.*;
import org.rental.quotation.Quotation;
import org.rental.vehicle.Vehicle;
import org.rental.vehicle.VehicleType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RentalService {

    private final AgenciesRepository agenciesRepository;
    private final AgencyCarsRepository carsRepository;
    
    private final PortAgencyBoatRepository boatRepository;

    private LocalDateTime current;

    private int nbDays = 0;
    private double discount;

    public RentalService(AgenciesRepository agenciesRepository, AgencyCarsRepository carsRepository, PortAgencyBoatRepository boatRepository) {
        this.agenciesRepository = agenciesRepository;
        this.carsRepository = carsRepository;
        this.boatRepository = boatRepository;
    }

    public List<Quotation> search(String postalCode, LocalDateTime from, LocalDateTime to, List<VehicleType> filter, String currency) {
        List<Agency> agencies = null;
        // for boats, never send empty but all boats type (empty only for cars)
        if (!filter.isEmpty() && filter.get(0) instanceof BoatType) {
            agencies = agenciesRepository.findNearestPort(postalCode);
        } else {
            agencies = agenciesRepository.findNearestCarRepository(postalCode);
        }
        List<Quotation> results = new ArrayList<>();
        for (Agency agency : agencies) {
            List<Vehicle> vehicules = new ArrayList<>();
            if(filter.isEmpty() || filter.get(0) instanceof CarType) {
                vehicules.addAll(carsRepository.getCarStatus(agency, from, to));
            } else {
                vehicules.addAll(boatRepository.getBoatStatus(agency, from, to));
            }
            for (Vehicle vehicle : vehicules) {
                if (
                        (vehicle instanceof Car && !filter.isEmpty() && !filter.contains(((Car)vehicle).getType()))
                        || (vehicle instanceof Boat && !filter.isEmpty() && !filter.contains(((Boat)vehicle).getType()))
                        || !vehicle.getStatus().equals(RentalStatus.AVAILABLE)

                ) {
                    continue;
                }
                current = from;
                double price = 0.0;
                nbDays = 0;
                discount = 0.0;
                while (current.isBefore(to)) {
                    nbDays += 1;
                    if (current.getDayOfWeek().getValue() < 6) {
                        if(vehicle instanceof Car) {
                            price += ((Car) vehicle).getDailyBasePrice();
                        } else {
                            price += ((Boat) vehicle).getDailyBasePrice();
                        };
                    } else {
                        if(vehicle instanceof Car) {
                            price += ((Car) vehicle).getDailyWeekEndPrice();
                        } else {
                            // no overcharge for boat the weekend
                            price += ((Boat) vehicle).getDailyBasePrice();
                        };
                    }
                    if(vehicle instanceof Car) {
                        // no discount for boat
                        getDiscount();
                    }
                    current = current.plusDays(1);
                }
                price = price - (price * discount);
                price = convert(price, currency);
                results.add(new Quotation(vehicle.getId(), price / nbDays, nbDays));
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
