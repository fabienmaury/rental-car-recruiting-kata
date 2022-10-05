package org.rental;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.rental.agency.AgenciesRepository;
import org.rental.agency.Agency;
import org.rental.agency.AgencyCarsRepository;
import org.rental.car.Car;
import org.rental.car.CarStatus;
import org.rental.car.CarType;
import org.rental.quotation.Quotation;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class RentalServiceTest {

    private AgenciesRepository agenciesRepository = Mockito.mock(AgenciesRepository.class);

    private AgencyCarsRepository carsRepository = Mockito.mock(AgencyCarsRepository.class);
    private RentalService sut = new RentalService(agenciesRepository, carsRepository);

    @Test
    void should_return_nothing_when_no_agency() {
        Mockito.when(agenciesRepository.findNearest(ArgumentMatchers.anyString())).thenReturn(Collections.emptyList());

        List<Quotation> result = sut.search("75008", monday_morning(), monday_endday(), Collections.emptyList(), "EUR", new ArrayList<>());

    }

    @Test
    void should_return_quotation_only_for_available_cars() {
        Mockito.when(agenciesRepository.findNearest(ArgumentMatchers.anyString())).thenReturn(asList(new Agency("7500800001")));
        given_the_following_cars_on_period().thenReturn(asList(
                        car("Fiat 500", 66.0, 105.0, CarStatus.ALLOCATED, CarType.BERLINE),
                        car("Peugeot 308", 77.0, 118.0, CarStatus.AVAILABLE, CarType.BERLINE),
                        car("Audi A4", 120.0, 192.0, CarStatus.DAMAGED, CarType.BREAK),
                        car("BMW X1", 134.0, 211.5, CarStatus.ON_RENT, CarType.SUV),
                        car("Citroen space tourer", 163.56, 250.0, CarStatus.OVERDUE, CarType.MINIBUS)
                )
        );

        List<Quotation> result = sut.search("75008", monday_morning(), monday_endday(), Collections.emptyList(), "EUR", new ArrayList<>());

        assertThat(result).hasSize(1);
    }

    @Test
    void should_return_cars_quotation_based_on_week_price() {
        Mockito.when(agenciesRepository.findNearest(ArgumentMatchers.anyString())).thenReturn(asList(new Agency("7500800001")));
        given_the_following_cars_on_period().thenReturn(asList(
                        car("Fiat 500", 66.0, 105.0, CarStatus.AVAILABLE, CarType.BERLINE),
                        car("Peugeot 308", 77.0, 118.0, CarStatus.AVAILABLE, CarType.BERLINE),
                        car("Audi A4", 120.0, 192.0, CarStatus.AVAILABLE, CarType.BREAK),
                        car("BMW X1", 134.0, 211.5, CarStatus.AVAILABLE, CarType.SUV),
                        car("Citroen space tourer", 163.56, 250.0, CarStatus.AVAILABLE, CarType.MINIBUS)
                )
        );

        List<Quotation> result = sut.search("75008", monday_morning(), monday_endday(), Collections.emptyList(), "EUR", new ArrayList<>());

        assertThat(result).as("should be for one day")
                .extracting(Quotation::getNbDays)
                .allMatch(x -> x == 1);

        assertThat(result).as("should be quoted with week price")
                .extracting(Quotation::getDailyPrice)
                .containsExactlyInAnyOrder(66.0, 77.0, 120.0, 134.0, 163.56);

    }

    @Test
    void should_return_can_quote_for_weekend_price() {
        Mockito.when(agenciesRepository.findNearest(ArgumentMatchers.anyString())).thenReturn(asList(new Agency("7500800001")));
        given_the_following_cars_on_period().thenReturn(asList(
                available_berline("Fiat 500", 66.0, 105.0),
                available_berline("Peugeot 308", 77.0, 118.0),
                available_break("Audi A4", 120.0, 192.0),
                available_SUV("BMW X1", 134.0, 211.5),
                available_MINIBUS("Citroen space tourer", 163.56, 250.0))
        );

        List<Quotation> result = sut.search("75008", saturday_morning(), saturday_endday(), Collections.emptyList(), "EUR", new ArrayList<>());

        assertThat(result).as("should be for one day")
                .extracting(Quotation::getNbDays)
                .allMatch(x -> x == 1);

        assertThat(result).as("should be quoted with week price")
                .extracting(Quotation::getDailyPrice)
                .containsExactlyInAnyOrder(105.0, 118.0, 192.0, 211.5, 250.0);
    }

    @Test
    void should_return_cars_quotation_based_on_average_day_price() {
        Mockito.when(agenciesRepository.findNearest(ArgumentMatchers.anyString())).thenReturn(asList(new Agency("7500800001")));
        given_the_following_cars_on_period().thenReturn(asList(
                        car("Fiat 500", 66.0, 105.0, CarStatus.AVAILABLE, CarType.BERLINE)
                )
        );

        List<Quotation> result = sut.search("75008", saturday_morning().minusDays(1), saturday_endday(), Collections.emptyList(), "EUR", new ArrayList<>());

        assertThat(result.get(0))
                .as("should be for 2 day")
                .matches(x -> x.getNbDays() == 2)
                .as("Should be average of price per day")
                .extracting(Quotation::getDailyPrice)
                .matches(x->x > 66.0)
                .matches(x->x < 105.0);
    }

    @Test
    void should_filter_by_car_type() {
        Mockito.when(agenciesRepository.findNearest(ArgumentMatchers.anyString())).thenReturn(asList(new Agency("7500800001")));
        given_the_following_cars_on_period().thenReturn(asList(
                        car("Fiat 500", 66.0, 105.0, CarStatus.AVAILABLE, CarType.BERLINE),
                        car("Peugeot 308", 77.0, 118.0, CarStatus.AVAILABLE, CarType.BERLINE),
                        car("Audi A4", 120.0, 192.0, CarStatus.AVAILABLE, CarType.BREAK),
                        car("BMW X1", 134.0, 211.5, CarStatus.AVAILABLE, CarType.SUV),
                        car("Citroen space tourer", 163.56, 250.0, CarStatus.AVAILABLE, CarType.MINIBUS)
                )
        );

        List<Quotation> result = sut.search("75008", monday_morning(), monday_endday(), asList(CarType.SUV), "EUR", new ArrayList<>());

        assertThat(result)
                .as("should only return SUV")
                .hasSize(1);

    }

    @Test
    void should_have_discount_price_when_have_the_second_day() {
        Mockito.when(agenciesRepository.findNearest(ArgumentMatchers.anyString())).thenReturn(asList(new Agency("7500800001")));
        double daily_week_price = 120.0;
        given_the_following_cars_on_period().thenReturn(asList(
                        car("Audi A4", daily_week_price, 192.0, CarStatus.AVAILABLE, CarType.BREAK)
                )
        );

        List<Quotation> result = sut.search("75008", monday_morning(), tuesday_endday(), Collections.emptyList(), "EUR", new ArrayList<>());

        assertThat(result)
                .as("should return only one quotation")
                .hasSize(1);

        assertThat(result.get(0))
                .extracting(Quotation::getDailyPrice)
                .matches(x->x < daily_week_price, "less than base price");

    }

    @Test
    void should_have_decreasing_price() {
        Mockito.when(agenciesRepository.findNearest(ArgumentMatchers.anyString())).thenReturn(asList(new Agency("7500800001")));
        given_the_following_cars_on_period().thenReturn(asList(
                car("Audi A4", 120.0, 192.0, CarStatus.AVAILABLE, CarType.BREAK))
        );

        List<Quotation> quotations_1_days = sut.search("75008", monday_morning(), monday_endday(), Collections.emptyList(), "EUR", new ArrayList<>());
        List<Quotation> quotations_2_days = sut.search("75008", monday_morning(), monday_endday().plusDays(1), Collections.emptyList(), "EUR", new ArrayList<>());
        List<Quotation> quotations_3_days = sut.search("75008", monday_morning(), monday_endday().plusDays(2), Collections.emptyList(), "EUR", new ArrayList<>());

        assertThat(price(quotations_1_days))
                .isGreaterThan(price(quotations_2_days))
                .isGreaterThan(price(quotations_3_days));

        assertThat(price(quotations_2_days))
                .isGreaterThan(price(quotations_3_days))
                .isLessThan(price(quotations_1_days));
    }

    @Test
    void should_have_decreasing_discount() {
        Mockito.when(agenciesRepository.findNearest(ArgumentMatchers.anyString())).thenReturn(asList(new Agency("7500800001")));
        given_the_following_cars_on_period().thenReturn(asList(
                car("Audi A4", 120.0, 192.0, CarStatus.AVAILABLE, CarType.BREAK))
        );

        List<Quotation> quotations_3_days = sut.search("75008", monday_morning(), monday_endday().plusDays(2), Collections.emptyList(), "EUR", new ArrayList<>());
        List<Quotation> quotations_4_days = sut.search("75008", monday_morning(), monday_endday().plusDays(3), Collections.emptyList(), "EUR", new ArrayList<>());
        List<Quotation> quotations_5_days = sut.search("75008", monday_morning(), monday_endday().plusDays(4), Collections.emptyList(), "EUR", new ArrayList<>());

        assertThat(price(quotations_5_days)).isNotEqualTo(price(quotations_4_days));

        double difference1 = price(quotations_4_days) - price(quotations_5_days);
        double difference2 = price(quotations_3_days) - price(quotations_4_days);
        assertThat(difference1).isLessThan(difference2);
    }

    private double price(List<Quotation> quotations) {
        return quotations.get(0).getDailyPrice();
    }

    private OngoingStubbing<List<Car>> given_the_following_cars_on_period() {
        return Mockito.when(carsRepository.getCarStatus(any(Agency.class), any(LocalDateTime.class), any(LocalDateTime.class)));
    }

    private Car car(String label, double dailyBasePrice, double dailyWeekEndPrice, CarStatus status, CarType type) {
        return new Car(UUID.randomUUID().toString(), label, dailyBasePrice, dailyWeekEndPrice, type, status, Collections.emptyList());
    }

    private Car available_berline(String label, double dailyBasePrice, double dailyWeekEndPrice) {
        return new Car(UUID.randomUUID().toString(), label, dailyBasePrice, dailyWeekEndPrice, CarType.BERLINE, CarStatus.AVAILABLE, Collections.emptyList());
    }

    private Car available_SUV(String label, double dailyBasePrice, double dailyWeekEndPrice) {
        return new Car(UUID.randomUUID().toString(), label, dailyBasePrice, dailyWeekEndPrice, CarType.SUV, CarStatus.AVAILABLE, Collections.emptyList());
    }

    private Car available_MINIBUS(String label, double dailyBasePrice, double dailyWeekEndPrice) {
        return new Car(UUID.randomUUID().toString(), label, dailyBasePrice, dailyWeekEndPrice, CarType.MINIBUS, CarStatus.AVAILABLE, Collections.emptyList());
    }

    private Car available_break(String label, double dailyBasePrice, double dailyWeekEndPrice) {
        return new Car(UUID.randomUUID().toString(), label, dailyBasePrice, dailyWeekEndPrice, CarType.BREAK, CarStatus.AVAILABLE, Collections.emptyList());
    }

    private LocalDateTime monday_endday() {
        return LocalDateTime.of(2022, 10, 3, 18, 0, 0);
    }
    private LocalDateTime tuesday_endday() {
        return LocalDateTime.of(2022, 10, 4, 18, 0, 0);
    }

    private LocalDateTime monday_morning() {
        return LocalDateTime.of(2022, 10, 3, 8, 0, 0);
    }

    private LocalDateTime saturday_endday() {
        return LocalDateTime.of(2022, 10, 8, 18, 0, 0);
    }

    private LocalDateTime saturday_morning() {
        return LocalDateTime.of(2022, 10, 8, 8, 0, 0);
    }

}