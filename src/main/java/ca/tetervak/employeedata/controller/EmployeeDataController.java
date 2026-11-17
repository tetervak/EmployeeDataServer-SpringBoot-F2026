package ca.tetervak.employeedata.controller;

import ca.tetervak.employeedata.data.Employee;
import ca.tetervak.employeedata.data.EmployeeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/employees", produces = "application/json")
@Tag(name = "Employees", description = "Endpoints for managing employee data")
public class EmployeeDataController {

    private final EmployeeRepository employeeRepository;

    public EmployeeDataController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping(produces = "application/json")
    @Operation(summary = "Retrieve all employees", description = "Returns a list of all employees")
    public List<Employee> getAllEmployees() {
        log.trace("getAllEmployees() is called");
        return employeeRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve an employee by ID", description = "Returns employee data with the specified ID")
    @Parameters(
            @Parameter(name = "id", description = "The ID of the employee to retrieve", required = true, example = "E001")
    )
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) throws NoResourceFoundException {
        log.trace("getEmployeeById() is called with id={}", id);
        return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NoResourceFoundException(HttpMethod.GET, null, "/api/employees/" + id));
    }

    @PostMapping(produces = "application/json")
    @Operation(summary = "Create a new employee record", description = "Creates a new employee record entry")
    public Employee createEmployee(@RequestBody @Valid Employee employee) {
        log.trace("createEmployee() is called with employee={}", employee);
        return employeeRepository.save(employee);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing employee record", description = "Updates an existing employee record entry")
    public ResponseEntity<Employee> updateEmployee(@PathVariable String id, @Valid @RequestBody Employee employee)
            throws NoResourceFoundException {
        log.trace("updateEmployee() is called with id={} and employee={}", id, employee);
        return employeeRepository.findById(id)
                .map(existingEmployee -> {
                    existingEmployee.setFirstName(employee.getFirstName());
                    existingEmployee.setLastName(employee.getLastName());
                    existingEmployee.setAge(employee.getAge());
                    existingEmployee.setEmail(employee.getEmail());
                    existingEmployee.setSalary(employee.getSalary());
                    existingEmployee.setFullTime(employee.isFullTime());
                    existingEmployee.setImageFile(employee.getImageFile());
                    Employee updatedEmployee = employeeRepository.save(existingEmployee);
                    return ResponseEntity.ok(updatedEmployee);
                })
                .orElseThrow(() -> new NoResourceFoundException(HttpMethod.GET, null, "/api/employees/" + id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee record by ID", description = "Deletes an employee record with the specified ID")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) throws NoResourceFoundException {
        log.trace("deleteEmployee() is called with id={}", id);
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new NoResourceFoundException(HttpMethod.GET, null, "/api/employees/" + id);
        }
    }
}
