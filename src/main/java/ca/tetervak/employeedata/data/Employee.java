package ca.tetervak.employeedata.data;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {

    @Id
    @NotBlank
    private String id;

    @NotBlank
    @JsonProperty("firstName")
    @JsonAlias({"first_name", "firstname"})
    private String firstName;

    @NotBlank
    @JsonProperty("lastName")
    @JsonAlias({"last_name", "lastname"})
    private String lastName;

    @Min(0)
    private int age;

    @NotBlank
    private String email;

    @Min(0)
    private double salary;

    @JsonProperty("fullTime")
    @JsonAlias({"isFullTime", "full_time", "fulltime", "fullTimeStatus"})
    private boolean isFullTime;

    @NotBlank
    @JsonProperty("imageFile")
    @JsonAlias({"image", "imageFileName", "imageName", "image_file", "image_file_name"})
    private String imageFile;
}
