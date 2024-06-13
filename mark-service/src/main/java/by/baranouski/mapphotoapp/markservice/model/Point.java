package by.baranouski.mapphotoapp.markservice.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
@Accessors(chain = true)
@FieldNameConstants
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Point {
    String type = "Point";
    double[] coordinates = new double[]{0,0};

    public Point(double x, double y){
        coordinates = new double[]{x,y};
    }
}
