package org.rental.agency;

import java.util.List;

public interface AgenciesRepository {

    List<Agency> findNearest(String postalCode);
}
