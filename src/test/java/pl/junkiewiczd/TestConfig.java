package pl.junkiewiczd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.TestPropertySource;
import pl.junkiewiczd.reports.PDFReportsService;
import pl.junkiewiczd.tables.ApartmentRepository;
import pl.junkiewiczd.tables.HostRepository;
import pl.junkiewiczd.tables.ReservationRepository;
import pl.junkiewiczd.tables.TenantRepository;

import javax.sql.DataSource;

@Configuration
@TestPropertySource(locations = "classpath:application-test.properties")
public class TestConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("classpath:schema-test.sql")
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public BookingSystemController bookingSystemController(HostRepository hostRepository, TenantRepository tenantRepository, ApartmentRepository apartmentRepository, ReservationRepository reservationRepository) {
        return new BookingSystemController(hostRepository, tenantRepository, apartmentRepository, reservationRepository);
    }

    @Bean
    public HostRepository hostRepository(){
        return new HostRepository();
    }

    @Bean
    public ApartmentRepository apartmentRepository() {
        return new ApartmentRepository();
    }

    @Bean
    public TenantRepository tenantRepository() {
        return new TenantRepository();
    }

    @Bean
    public ReservationRepository reservationRepository() {
        return new ReservationRepository();
    }
}
