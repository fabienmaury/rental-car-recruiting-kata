package org.rental.agency;

import org.rental.boat.Boat;
import org.rental.car.Car;

import java.time.LocalDateTime;
import java.util.List;

public interface PortAgencyBoatRepository {

    List<Boat> getBoatStatus(Agency agency, LocalDateTime from, LocalDateTime to);

}
