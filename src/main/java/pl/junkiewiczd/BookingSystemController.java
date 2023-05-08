package pl.junkiewiczd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import pl.junkiewiczd.basicobjects.Apartment;
import pl.junkiewiczd.basicobjects.Person;
import pl.junkiewiczd.basicobjects.reservations.Reservation;
import pl.junkiewiczd.basicobjects.reservations.ReservationByTenantName;
import pl.junkiewiczd.tables.ApartmentRepository;
import pl.junkiewiczd.tables.HostRepository;
import pl.junkiewiczd.tables.ReservationRepository;
import pl.junkiewiczd.tables.TenantRepository;

import java.time.LocalDate;
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
    public List<Person> getAllTenants() { return tenantRepository.getAll(); }

    @GetMapping("/hosts")
    public List<Person> getAllHosts() { return hostRepository.getAll(); }

    @GetMapping("/apartments")
    public List<Apartment> getAllApartments() { return apartmentRepository.getAll(); }

    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {return reservationRepository.getAll(); }

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
        if (!isAnyConflictingReservation(reservationToAdd)) {
            return reservationRepository.save(reservationToAdd);
        } else {
            return -1;
        }
    }

    private boolean isAnyConflictingReservation(Reservation reservation){
        int apartmentID = reservation.getApartmentId();
        LocalDate startOfRental = reservation.getStartOfRental();
        LocalDate endOfRental = reservation.getEndOfRental();

        List<Reservation> conflictingReservations = reservationRepository
                .reservationsByApartmentIdAndTimeOfRental(apartmentID,startOfRental,endOfRental);

        return !conflictingReservations.isEmpty();
    }

    private boolean isAnyConflictingReservation(int id, Reservation reservation){
        LocalDate startOfRental = reservation.getStartOfRental();
        LocalDate endOfRental = reservation.getEndOfRental();

        List<Reservation> conflictingReservations = reservationRepository
                .reservationsByApartmentIdAndTimeOfRental(id,startOfRental,endOfRental);

        return !conflictingReservations.isEmpty();
    }

    @PutMapping("reservations/{id}")
    public int fullyUpdateReservation(@PathVariable("id") int id, @RequestBody Reservation updatedReservation) {
        Reservation reservation = reservationRepository.getById(id);
        if(!isAnyConflictingReservation(id, updatedReservation) && reservation != null){
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
        Reservation reservation = reservationRepository.getById(id);
        if(!isAnyConflictingReservation(id, updatedReservation) && reservation != null) {
            if (updatedReservation.getStartOfRental() != null) reservation.setStartOfRental(updatedReservation.getStartOfRental());
            if (updatedReservation.getEndOfRental() != null) reservation.setEndOfRental(updatedReservation.getEndOfRental());
            if (updatedReservation.getTenantId() != null) reservation.setTenantId(updatedReservation.getTenantId());
            if (updatedReservation.getApartmentId() != null) reservation.setApartmentId(updatedReservation.getApartmentId());
            if (updatedReservation.getCost() != null) reservation.setCost(updatedReservation.getCost());
            return reservationRepository.update(reservation);
        } else {
            return -1;
        }
    }

}
