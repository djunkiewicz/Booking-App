package pl.junkiewiczd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.junkiewiczd.basicobjects.apartments.Apartment;
import pl.junkiewiczd.basicobjects.Person;
import pl.junkiewiczd.basicobjects.reservations.Reservation;
import pl.junkiewiczd.basicobjects.reservations.ReservationByTenantName;
import pl.junkiewiczd.tables.ApartmentRepository;
import pl.junkiewiczd.tables.HostRepository;
import pl.junkiewiczd.tables.ReservationRepository;
import pl.junkiewiczd.tables.TenantRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
public class BookingSystemController {

    private HostRepository hostRepository;
    private TenantRepository tenantRepository;
    private ApartmentRepository apartmentRepository;
    private ReservationRepository reservationRepository;

    @Autowired
    public BookingSystemController(HostRepository hostRepository, TenantRepository tenantRepository, ApartmentRepository apartmentRepository, ReservationRepository reservationRepository) {
        this.hostRepository = hostRepository;
        this.tenantRepository = tenantRepository;
        this.apartmentRepository = apartmentRepository;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/tenants")
    public List<Person> getAllTenants() {
        return tenantRepository.getAll();
    }

    @GetMapping("/hosts")
    public List<Person> getAllHosts() {
        return hostRepository.getAll();
    }

    @GetMapping("/apartments")
    public List<Apartment> getAllApartments() {
        return apartmentRepository.getAll();
    }

    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {
        return reservationRepository.getAll();
    }

    @GetMapping("/reservations/tenants/{name}")
    public List<ReservationByTenantName> getReservationsByTenantName(@PathVariable("name") String tenantName) {
        return reservationRepository.getByTenantName(tenantName);
    }

    @GetMapping("/reservations/apartments/{id}")
    public List<Reservation> getReservationsByApartmentId(@PathVariable("id") int id) {
        return reservationRepository.getByApartmentId(id);
    }

    @PostMapping("/reservations")
    public int saveNewReservation(@RequestBody Reservation reservationToAdd) {
        if (areParametersNonEmpty(reservationToAdd) && !isAnyConflictingReservation(reservationToAdd)) {
            return reservationRepository.save(calculateAndUpdateCostIfEmpty(reservationToAdd));
        } else {
            return -1;
        }
    }

    private boolean isAnyConflictingReservation(Reservation reservation) {
        int apartmentID = reservation.getApartmentId();
        LocalDate startOfRental = reservation.getStartOfRental();
        LocalDate endOfRental = reservation.getEndOfRental();

        List<Reservation> conflictingReservations = reservationRepository
                .reservationsByApartmentIdAndTimeOfRental(apartmentID, startOfRental, endOfRental);

        return !conflictingReservations.isEmpty();
    }

    private boolean isAnyConflictingReservation(int id, Reservation reservation) {
        int apartmentID = reservationRepository.getById(id).getApartmentId();
        LocalDate startOfRental = reservation.getStartOfRental();
        LocalDate endOfRental = reservation.getEndOfRental();

        List<Reservation> conflictingReservations = reservationRepository
                .reservationsByApartmentIdAndTimeOfRental(apartmentID, startOfRental, endOfRental);

        return !conflictingReservations.isEmpty();
    }

    private boolean areParametersNonEmpty(Reservation reservation) {
        if (reservation.getStartOfRental() == null ||
                reservation.getEndOfRental() == null ||
                reservation.getTenantId() == null ||
                reservation.getApartmentId() == null
        ) return false;
        else return true;
    }

    private Reservation calculateAndUpdateCostIfEmpty (Reservation reservation) {
        if (reservation.getCost() == null){
            reservation.setCost(calculateCostOfReservation(reservation));
        } return reservation;
    }

    private int calculateCostOfReservation (Reservation reservation) {
        long daysOfReservation = ChronoUnit.DAYS.between(reservation.getStartOfRental(), reservation.getEndOfRental())+1;
        return (int) (daysOfReservation * apartmentRepository.getApartmentPrice(reservation.getApartmentId()));
    }

    @PutMapping("reservations/{id}")
    public int fullyUpdateReservation(@PathVariable("id") int id, @RequestBody Reservation updatedReservation) {
        if (reservationRepository.existsById(id) && !isAnyConflictingReservation(id, updatedReservation)) {
            Reservation reservation = reservationRepository.getById(id);
            reservation.setStartOfRental(updatedReservation.getStartOfRental());
            reservation.setEndOfRental(updatedReservation.getEndOfRental());
            reservation.setTenantId(updatedReservation.getTenantId());
            reservation.setApartmentId(updatedReservation.getApartmentId());
            reservation.setCost(updatedReservation.getCost());
            return reservationRepository.update(reservation);
        } else return -1;
    }

    @PatchMapping("reservations/{id}")
    public int partiallyUpdateReservation(@PathVariable("id") int id, @RequestBody Reservation updatedReservation) {
        if (reservationRepository.existsById(id) && !isAnyConflictingReservation(id, updatedReservation)) {
            Reservation reservation = reservationRepository.getById(id);
            if (updatedReservation.getStartOfRental() != null)
                reservation.setStartOfRental(updatedReservation.getStartOfRental());
            if (updatedReservation.getEndOfRental() != null)
                reservation.setEndOfRental(updatedReservation.getEndOfRental());
            if (updatedReservation.getTenantId() != null) reservation.setTenantId(updatedReservation.getTenantId());
            if (updatedReservation.getApartmentId() != null)
                reservation.setApartmentId(updatedReservation.getApartmentId());
            if (updatedReservation.getCost() != null) reservation.setCost(updatedReservation.getCost());
            return reservationRepository.update(reservation);
        } else {
            return -1;
        }
    }

    @DeleteMapping("reservations/{id}")
    public int deleteReservation(@PathVariable("id") int id) {
        if (reservationRepository.existsById(id)) {
            return reservationRepository.delete(id);
        }
        else return -1;
    }
}
