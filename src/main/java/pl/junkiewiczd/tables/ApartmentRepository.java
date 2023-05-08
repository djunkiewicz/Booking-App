package pl.junkiewiczd.tables;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pl.junkiewiczd.basicobjects.Apartment;

import java.util.List;

@Repository
public class ApartmentRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Apartment> getAll() {
        return jdbcTemplate.query("SELECT * FROM apartment",
                BeanPropertyRowMapper.newInstance(Apartment.class));
    }
}
