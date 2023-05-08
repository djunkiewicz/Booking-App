package pl.junkiewiczd.tables;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pl.junkiewiczd.basicobjects.Person;

import java.util.List;

@Repository
public class TenantRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Person> getAll() {
        return jdbcTemplate.query("SELECT * FROM tenant",
                BeanPropertyRowMapper.newInstance(Person.class));
    }
}
