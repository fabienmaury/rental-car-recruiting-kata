package org.rental.agency;

import java.util.List;

public interface AgenciesRepository {

    List<Agency> findNearestCarAgency(String postalCode);
    List<Agency> findNearestBoatAgency(String postalCode);
}
