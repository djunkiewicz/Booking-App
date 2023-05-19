package pl.junkiewiczd.basicobjects.apartments;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApartmentReportDetails {
    private String name;
    private Integer DaysOfReservation;
    private Integer AmountOfReservation;
}
