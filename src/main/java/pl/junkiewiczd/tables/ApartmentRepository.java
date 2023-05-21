package pl.junkiewiczd.tables;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import pl.junkiewiczd.basicobjects.apartments.Apartment;
import pl.junkiewiczd.basicobjects.apartments.ApartmentReportDetails;
import pl.junkiewiczd.reports.PeriodOfTime;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class ApartmentRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Apartment> getAll() {
        return jdbcTemplate.query("SELECT * FROM apartment",
                BeanPropertyRowMapper.newInstance(Apartment.class));
    }

    public Integer getApartmentPrice(int id) {
        return jdbcTemplate.queryForObject("SELECT price FROM apartment WHERE id=?",
                Integer.class, id);
    }

    public List<ApartmentReportDetails> getReportDetails(PeriodOfTime periodOfTime) {

        Map<String, LocalDate> parametersMap = new HashMap<>();
        parametersMap.put("beginning", periodOfTime.getBeginning());
        parametersMap.put("end", periodOfTime.getEnd());

        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(Objects.requireNonNull(jdbcTemplate.getDataSource()));

        return namedTemplate.query(
                "SELECT " +
                        "apartment.name, " +
                        "COUNT(reservation.id) AS amountOfReservation, " +
                        "SUM(CASE WHEN start_of_rental < CAST(:beginning AS DATE) AND end_of_rental BETWEEN CAST(:beginning AS DATE) AND CAST(:end AS DATE) THEN " +
                        "DATEDIFF(end_of_rental, CAST(:beginning AS DATE))+1 " +
                        "WHEN end_of_rental > CAST(:end AS DATE) AND start_of_rental BETWEEN CAST(:beginning AS DATE) AND CAST(:end AS DATE) THEN " +
                        "DATEDIFF(CAST(:end AS DATE), start_of_rental)+1 " +
                        "ELSE DATEDIFF(end_of_rental, start_of_rental)+1 END) AS daysOfReservation, " +
                        "SUM(reservation.cost) AS totalIncome " +
                    "FROM " +
                        "apartment " +
                    "JOIN " +
                        "reservation ON apartment.id = reservation.apartment_id " +
                    "WHERE " +
                        "(start_of_rental BETWEEN CAST(:beginning AS DATE) AND CAST(:end AS DATE)) OR " +
                        "(end_of_rental BETWEEN CAST(:beginning AS DATE) AND CAST(:end AS DATE)) " +
                    "GROUP BY " +
                        "apartment.id",
                parametersMap,
                BeanPropertyRowMapper.newInstance(ApartmentReportDetails.class));
    }
}
