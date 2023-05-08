package pl.junkiewiczd.basicobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Apartment {
    private Integer id;
    private String name;
    private Integer area;
    private Integer hostId;
    private Integer price;
    private String description;
}
