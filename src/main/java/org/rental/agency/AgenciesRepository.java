package org.rental.agency;

import java.util.List;

public interface AgenciesRepository {

    List<Agency> findNearestCarRepository(String postalCode);

    List<Agency> findNearestPort(String postalCode);
}
