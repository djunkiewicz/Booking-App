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
    private Integer daysOfReservation;
    private Integer amountOfReservation;
    private Integer totalIncome;
}
