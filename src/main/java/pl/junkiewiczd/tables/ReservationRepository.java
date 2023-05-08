package pl.junkiewiczd.tables;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pl.junkiewiczd.basicobjects.reservations.Reservation;
import pl.junkiewiczd.basicobjects.reservations.ReservationByTenantName;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ReservationRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Reservation> getAll() {
        return jdbcTemplate.query("SELECT * FROM reservation",
                BeanPropertyRowMapper.newInstance(Reservation.class));
    }

    public Reservation getById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM reservation WHERE id=?",
                BeanPropertyRowMapper.newInstance(Reservation.class), id);
    }

    public List<ReservationByTenantName> getByTenantName(String name) {
        return jdbcTemplate.query("SELECT reservation.id, start_of_rental, end_of_rental, " +
                "tenant_id, apartment_id, cost, tenant.name AS tenant_name " +
                "FROM reservation JOIN tenant ON reservation.tenant_id = tenant.id " +
                "WHERE tenant.name = ?",
                BeanPropertyRowMapper.newInstance(ReservationByTenantName.class), name);
    }

    public List<Reservation> getByApartmentId(int id) {
        return jdbcTemplate.query("SELECT * FROM reservation WHERE apartment_id=?",
                BeanPropertyRowMapper.newInstance(Reservation.class), id);
    }

    public int save(Reservation reservationToAdd) {
        return jdbcTemplate.update("INSERT INTO reservation(start_of_rental, end_of_rental, " +
                "tenant_id, apartment_id, cost) VALUES (?, ?, ?, ?, ?)",
                reservationToAdd.getStartOfRental(), reservationToAdd.getEndOfRental(),
                reservationToAdd.getTenantId(), reservationToAdd.getApartmentId(), reservationToAdd.getCost());
    }

    public int update(Reservation reservationToUpdate) {
        return jdbcTemplate.update("UPDATE reservation SET start_of_rental=?, end_of_rental=?, " +
                "tenant_id=?, apartment_id=?, cost=? WHERE id=?", reservationToUpdate.getStartOfRental(),
                reservationToUpdate.getEndOfRental(), reservationToUpdate.getTenantId(),
                reservationToUpdate.getApartmentId(), reservationToUpdate.getCost(), reservationToUpdate.getId());
    }

    public List<Reservation> reservationsByApartmentIdAndTimeOfRental(int apartmentID, LocalDate startOfRental, LocalDate endOfRental) {
        return jdbcTemplate.query("SELECT * FROM reservation " +
                        "WHERE apartment_id = ? " +
                        "AND (CAST(? AS DATE) BETWEEN start_of_rental AND end_of_rental OR " +
                        "CAST(? AS DATE) BETWEEN start_of_rental AND end_of_rental)",
                BeanPropertyRowMapper.newInstance(Reservation.class),
                apartmentID, startOfRental, endOfRental);
    }

}
