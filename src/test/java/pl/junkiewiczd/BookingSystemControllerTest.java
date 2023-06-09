package pl.junkiewiczd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import pl.junkiewiczd.basicobjects.apartments.Apartment;
import pl.junkiewiczd.basicobjects.Person;
import pl.junkiewiczd.basicobjects.reservations.Reservation;
import pl.junkiewiczd.basicobjects.reservations.ReservationByTenantName;
import pl.junkiewiczd.tables.ReservationRepository;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
class BookingSystemControllerTest {

    @Autowired
    private BookingSystemController bookingSystemController;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void getAllTenants() {
        //given
        //when
        List<Person> tenantsList = bookingSystemController.getAllTenants();
        //then
        assertThat(tenantsList, hasSize(4));
        assertThat(tenantsList.get(0), hasProperty("name", equalTo("Liam King")));
        assertThat(tenantsList.get(2), hasProperty("name", equalTo("Lucas Patel")));
    }


    @Test
    void getAllHosts() {
        //given
        //when
        List<Person> hostsList = bookingSystemController.getAllHosts();
        //then
        assertThat(hostsList, hasSize(5));
        assertThat(hostsList.get(0), hasProperty("name", equalTo("Sophie Hall")));
        assertThat(hostsList.get(3), hasProperty("name", equalTo("Jacob Brown")));
    }

    @Test
    void getAllApartments() {
        //given
        //when
        List<Apartment> apartmentsList = bookingSystemController.getAllApartments();
        //then
        assertThat(apartmentsList, hasSize(10));
        assertThat(apartmentsList.get(0), allOf(
                hasProperty("name", equalTo("Baker Street 123")),
                hasProperty("area", equalTo(45)),
                hasProperty("hostId", equalTo(4)),
                hasProperty("price", equalTo(110)),
                hasProperty("description", equalTo("2-room apartment near the park"))
        ));
        assertThat(apartmentsList.get(8), allOf(
                hasProperty("name", equalTo("Tottenham Court 32")),
                hasProperty("area", equalTo(120)),
                hasProperty("hostId", equalTo(1)),
                hasProperty("price", equalTo(400)),
                hasProperty("description", equalTo("huge 5-room penthouse"))
        ));
    }

    @Test
    void getAllReservations() {
        //given
        //when
        List<Reservation> reservationList = bookingSystemController.getAllReservations();
        //then
        assertThat(reservationList, hasSize(8));
        assertThat(reservationList.get(0), allOf(
                hasProperty("startOfRental", equalTo(LocalDate.of(2023, 4, 15))),
                hasProperty("endOfRental", equalTo(LocalDate.of(2023, 4, 20))),
                hasProperty("tenantId", equalTo(1)),
                hasProperty("apartmentId", equalTo(5))
        ));
        assertThat(reservationList.get(5), allOf(
                hasProperty("startOfRental", equalTo(LocalDate.of(2023, 10, 2))),
                hasProperty("endOfRental", equalTo(LocalDate.of(2023, 10, 6))),
                hasProperty("tenantId", equalTo(4)),
                hasProperty("apartmentId", equalTo(3))
        ));
    }

    @Test
    void getReservationsByTenantName() {
        //given
        //when
        List<ReservationByTenantName> reservationList = bookingSystemController.getReservationsByTenantName("Liam King");

        //then
        assertThat(reservationList, hasSize(2));
        assertThat(reservationList.get(1), hasProperty("startOfRental", equalTo(LocalDate.of(2023,10,1))));
    }

    @Test
    void getReservationsByApartmentId() {
        //given
        //when
        List<Reservation> reservationList = bookingSystemController.getReservationsByApartmentId(6);

        //then
        assertThat(reservationList, hasSize(3));
        assertThat(reservationList.get(0), hasProperty("tenantId", equalTo(3)));

    }

    @Test
    @DirtiesContext
    void saveNewReservation() {
        //given
        List<Reservation> reservationList = bookingSystemController.getAllReservations();
        int sizeBeforeAddingNewReservation = reservationList.size();

        Reservation newReservation = new Reservation(LocalDate.of(2023, 12,12), LocalDate.of(2023, 12,16),
                3,2, 500);

        //when
        bookingSystemController.saveNewReservation(newReservation);
        reservationList = bookingSystemController.getAllReservations();
        int sizeAfterAddingNewReservation = reservationList.size();

        //then
        assertThat(sizeBeforeAddingNewReservation, is(lessThan(sizeAfterAddingNewReservation)));
        assertThat(reservationList.get(8), allOf(
                hasProperty("id", equalTo(9)),
                hasProperty("startOfRental", equalTo(LocalDate.of(2023, 12, 12))),
                hasProperty("endOfRental", equalTo(LocalDate.of(2023, 12, 16))),
                hasProperty("tenantId", equalTo(3)),
                hasProperty("apartmentId", equalTo(2))
        ));
    }

    @Test
    @DirtiesContext
    void shouldNotAddOverlappingReservationsForSameApartment() {
        //given
        List<Reservation> reservationList = bookingSystemController.getAllReservations();
        int sizeBeforeAddingNewReservations = reservationList.size();
        Reservation baseReservation = new Reservation(LocalDate.of(2023, 3,10), LocalDate.of(2023, 3,15),
                3,2, 500);
        Reservation correctReservation = new Reservation(LocalDate.of(2023, 3,16), LocalDate.of(2023, 3,20),
                3,2, 500);
        Reservation incorrectReservation1 = new Reservation(LocalDate.of(2023, 3,10), LocalDate.of(2023, 3,15),
                3,2, 500);
        Reservation incorrectReservation2 = new Reservation(LocalDate.of(2023, 3,2), LocalDate.of(2023, 3,12),
                3,2, 500);
        Reservation incorrectReservation3 = new Reservation(LocalDate.of(2023, 3,13), LocalDate.of(2023, 3,18),
                3,2, 500);

        //when
        bookingSystemController.saveNewReservation(baseReservation);
        bookingSystemController.saveNewReservation(correctReservation);
        bookingSystemController.saveNewReservation(incorrectReservation1);
        bookingSystemController.saveNewReservation(incorrectReservation2);
        bookingSystemController.saveNewReservation(incorrectReservation3);
        reservationList = bookingSystemController.getAllReservations();
        int sizeAfterAddingNewReservations = reservationList.size();

        //then
        assertThat(sizeBeforeAddingNewReservations,is(lessThan(sizeAfterAddingNewReservations)));
        assertThat(sizeAfterAddingNewReservations,is(equalTo(sizeBeforeAddingNewReservations+2)));
    }

    @Test
    @DirtiesContext
    void shouldNotPartiallyUpdateToCauseDateConflictForSameApartment() {
        //given
        Reservation reservation = reservationRepository.getById(2);
        LocalDate startOfRentalBeforeUpdateMethods = reservation.getStartOfRental();
        LocalDate endOfRentalBeforeUpdateMethods = reservation.getEndOfRental();
        Reservation reservationChanges1 = new Reservation(LocalDate.of(2023, 4,12), LocalDate.of(2023, 4,18),
                null,null, null);
        Reservation reservationChanges2 = new Reservation(LocalDate.of(2023, 4,17), LocalDate.of(2023, 4,22),
                null,null, null);

        //when
        bookingSystemController.partiallyUpdateReservation(2,reservationChanges1);
        bookingSystemController.partiallyUpdateReservation(2,reservationChanges2);
        reservation = reservationRepository.getById(2);
        LocalDate startOfRentalAfterUpdateMethod = reservation.getStartOfRental();
        LocalDate endOfRentalAfterUpdateMethod = reservation.getEndOfRental();

        //then
        assertThat(startOfRentalBeforeUpdateMethods,is(equalTo(startOfRentalAfterUpdateMethod)));
        assertThat(endOfRentalBeforeUpdateMethods,is(equalTo(endOfRentalAfterUpdateMethod)));
    }

    @Test
    @DirtiesContext
    void shouldNotFullyUpdateToCauseDateConflictForSameApartment() {
        //given
        Reservation reservationBeforeFullyUpdateMethod = reservationRepository.getById(2);
        Reservation conflictReservation1 = new Reservation(LocalDate.of(2023, 4,15), LocalDate.of(2023, 4,20),
                3,5, 600);
        Reservation conflictReservation2 = new Reservation(LocalDate.of(2023, 4,17), LocalDate.of(2023, 4,22),
                4,5, 720);
        Reservation conflictReservation3 = new Reservation(LocalDate.of(2023, 4,12), LocalDate.of(2023, 4,18),
                1,5, 800);

        //when
        bookingSystemController.fullyUpdateReservation(2,conflictReservation1);
        bookingSystemController.fullyUpdateReservation(2,conflictReservation2);
        bookingSystemController.fullyUpdateReservation(2,conflictReservation3);
        Reservation reservationAfterFullyUpdateMethods = reservationRepository.getById(2);

        //then
        assertThat(reservationBeforeFullyUpdateMethod,is(equalTo(reservationAfterFullyUpdateMethods)));
    }

    @Test
    @DirtiesContext
    void shouldAutomaticallyCalculateAndUpdateCostOfReservationIfNotGiven() {
        //given
        Reservation newReservation1 = new Reservation(LocalDate.of(2023, 3,10), LocalDate.of(2023, 3,15),
                3,2, null);
        Reservation newReservation2 = new Reservation(LocalDate.of(2023, 3,16), LocalDate.of(2023, 3,18),
                3,2, 456);
        //when
        bookingSystemController.saveNewReservation(newReservation1);
        bookingSystemController.saveNewReservation(newReservation2);
        Reservation previousLastAddedReservation = reservationRepository.getById(9);
        Reservation lastAddedReservation = reservationRepository.getById(10);

        //then
        assertThat(previousLastAddedReservation.getCost(),is(equalTo(360)));
        assertThat(lastAddedReservation.getCost(),is(equalTo(456)));
    }


    @Test
    @DirtiesContext
    void fullyUpdateReservation() {
        //given
        Reservation newReservation = new Reservation(LocalDate.of(2023, 12,12), LocalDate.of(2023, 12,16),
                3,2, 500);
        Reservation reservationBeforeFullyUpdate = bookingSystemController.getAllReservations().get(0);

        //when
        bookingSystemController.fullyUpdateReservation(1,newReservation);
        Reservation reservationAfterFullyUpdate = bookingSystemController.getAllReservations().get(0);

        //then
        assertThat(reservationBeforeFullyUpdate, is(not(equalTo(reservationAfterFullyUpdate))));
    }

    @Test
    void partiallyUpdateReservation() {
        //given
        Reservation editedReservation = new Reservation();
        editedReservation.setApartmentId(3);
        int apartmentIdBeforeEdit = bookingSystemController.getAllReservations().get(0).getApartmentId();

        //when
        bookingSystemController.partiallyUpdateReservation(1,editedReservation);
        int apartmentIdAfterEdit = bookingSystemController.getAllReservations().get(0).getApartmentId();

        //then
        assertThat(apartmentIdBeforeEdit, is(not(equalTo(apartmentIdAfterEdit))));
        assertThat(apartmentIdAfterEdit, is(equalTo(3)));
        assertThat(apartmentIdBeforeEdit, is(equalTo(5)));
    }

    @Test
    @DirtiesContext
    void deleteReservation() {
        //given
        List<Reservation> reservationList = bookingSystemController.getAllReservations();
        int sizeBeforeDeleteExistingReservation = reservationList.size();

        //when
        bookingSystemController.deleteReservation(2);
        reservationList = bookingSystemController.getAllReservations();
        int sizeAfterDeletingExistingReservation = reservationList.size();

        //then
        assertThat(sizeBeforeDeleteExistingReservation, is(greaterThan(sizeAfterDeletingExistingReservation)));
        assertThat(reservationRepository.existsById(2), is(false));
    }

    @Test
    @DirtiesContext
    void youCannotDeleteReservationThatDoesNotExist() {
        //given
        List<Reservation> reservationList = bookingSystemController.getAllReservations();
        int sizeBeforeDeleteExistingReservation = reservationList.size();

        //when
        int result = bookingSystemController.deleteReservation(30);
        reservationList = bookingSystemController.getAllReservations();
        int sizeAfterDeletingExistingReservation = reservationList.size();

        //then
        assertThat(sizeBeforeDeleteExistingReservation, is(equalTo(sizeAfterDeletingExistingReservation)));
        assertThat(result, is(equalTo(-1)));
    }

    @Test
    @DirtiesContext
    void youCannotPartiallyUpdateReservationThatDoesNotExist() {
        //given
        Reservation newReservation = new Reservation(LocalDate.of(2023, 12,12), LocalDate.of(2023, 12,16),
                3,2, 500);
        Reservation reservationBeforePartiallyUpdate = bookingSystemController.getAllReservations().get(0);

        //when
        int result = bookingSystemController.partiallyUpdateReservation(30, newReservation);
        Reservation reservationAfterPartiallyUpdate = bookingSystemController.getAllReservations().get(0);

        //then
        assertThat(reservationBeforePartiallyUpdate, is(equalTo(reservationAfterPartiallyUpdate)));
        assertThat(result, is(equalTo(-1)));
    }

    @Test
    @DirtiesContext
    void youCannotFullyUpdateReservationThatDoesNotExist() {
        //given
        Reservation newReservation = new Reservation(LocalDate.of(2023, 12,12), LocalDate.of(2023, 12,16),
                3,2, 500);
        Reservation reservationBeforeFullyUpdate = bookingSystemController.getAllReservations().get(0);

        //when
        int result = bookingSystemController.fullyUpdateReservation(30, newReservation);
        Reservation reservationAfterFullyUpdate = bookingSystemController.getAllReservations().get(0);

        //then
        assertThat(reservationBeforeFullyUpdate, is(equalTo(reservationAfterFullyUpdate)));
        assertThat(result, is(equalTo(-1)));
    }

}
