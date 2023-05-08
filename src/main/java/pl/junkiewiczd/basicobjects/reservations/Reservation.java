package pl.junkiewiczd.basicobjects.reservations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    private Integer id;
    private LocalDate startOfRental;
    private LocalDate endOfRental;
    private Integer tenantId;
    private Integer apartmentId;
    private Integer cost;

    public Reservation(LocalDate startOfRental, LocalDate endOfRental, Integer tenantId, Integer apartmentId, Integer cost) {
        this.startOfRental = startOfRental;
        this.endOfRental = endOfRental;
        this.tenantId = tenantId;
        this.apartmentId = apartmentId;
        this.cost = cost;
    }
}
