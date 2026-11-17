package ca.tetervak.employeedata.data;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeData {

    private List<Employee> employees;
}
