package org.rental.agency;

import org.rental.boat.Boat;
import org.rental.car.Car;
import org.rental.vehicle.Vehicle;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface RentalRepository {

    List<Agency> findNearestCarAgency(String postalCode);

    List<Car> getCarStatus(Agency agency, LocalDateTime from, LocalDateTime to);

    List<Agency> findNearestBoatAgency(String postalCode);

    List<Boat> getBoatStatus(Agency agency, LocalDateTime from, LocalDateTime to);
}
