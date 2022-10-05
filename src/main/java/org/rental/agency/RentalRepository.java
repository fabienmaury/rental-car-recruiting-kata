package org.rental.agency;

import org.rental.car.Car;

import java.time.LocalDateTime;
import java.util.List;

public interface RentalRepository {

    List<Agency> findNearestCarAgency(String postalCode);

    List<Car> getCarStatus(Agency agency, LocalDateTime from, LocalDateTime to);
}
