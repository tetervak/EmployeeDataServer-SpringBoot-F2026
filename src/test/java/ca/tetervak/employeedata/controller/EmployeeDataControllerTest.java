package ca.tetervak.employeedata.controller;

import ca.tetervak.employeedata.data.Employee;
import ca.tetervak.employeedata.data.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class EmployeeDataControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        employeeRepository.deleteAll();
        Employee emp1 = new Employee("E001", "Alice", "Johnson", 28, "alice.johnson@example.com", 75000.0, true, "alice_johnson.jpg");
        Employee emp2 = new Employee("E002", "Bob", "Smith", 34, "bob.smith@example.com", 82000.0, false, "bob_smith.jpg");
        employeeRepository.saveAll(List.of(emp1, emp2));
    }

    @Test
    void getAllEmployees_returnsList() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("E001")))
                .andExpect(jsonPath("$[0].firstName", is("Alice")))
                .andExpect(jsonPath("$[0].lastName", is("Johnson")))
                .andExpect(jsonPath("$[0].age", is(28)))
                .andExpect(jsonPath("$[0].email", is("alice.johnson@example.com")))
                .andExpect(jsonPath("$[0].salary", is(75000.0)))
                .andExpect(jsonPath("$[0].fullTime", is(true)))
                .andExpect(jsonPath("$[0].imageFile", is("alice_johnson.jpg")))
                .andExpect(jsonPath("$[1].id", is("E002")))
                .andExpect(jsonPath("$[1].firstName", is("Bob")))
                .andExpect(jsonPath("$[1].lastName", is("Smith")))
                .andExpect(jsonPath("$[1].fullTime", is(false)));
    }

    @Test
    void getEmployeeById_existingId_returnsEmployee() throws Exception {
        mockMvc.perform(get("/api/employees/E001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("E001")))
                .andExpect(jsonPath("$.firstName", is("Alice")))
                .andExpect(jsonPath("$.lastName", is("Johnson")))
                .andExpect(jsonPath("$.age", is(28)))
                .andExpect(jsonPath("$.email", is("alice.johnson@example.com")))
                .andExpect(jsonPath("$.salary", is(75000.0)))
                .andExpect(jsonPath("$.fullTime", is(true)))
                .andExpect(jsonPath("$.imageFile", is("alice_johnson.jpg")));
    }

    @Test
    void getEmployeeById_nonExistingId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/employees/E999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("/api/employees/E999")));
    }

    @Test
    void createEmployee_validData_returnsCreatedEmployee() throws Exception {
        Employee newEmp = new Employee("E003", "Charlie", "Davis", 22, "charlie.davis@example.com", 45000.0, false, "charlie_davis.jpg");
        String json = objectMapper.writeValueAsString(newEmp);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("E003")))
                .andExpect(jsonPath("$.firstName", is("Charlie")))
                .andExpect(jsonPath("$.lastName", is("Davis")))
                .andExpect(jsonPath("$.salary", is(45000.0)))
                .andExpect(jsonPath("$.fullTime", is(false)))
                .andExpect(jsonPath("$.imageFile", is("charlie_davis.jpg")));

        assertTrue(employeeRepository.existsById("E003"));
    }

    @Test
    void createEmployee_invalidData_returnsBadRequest() throws Exception {
        Employee invalidEmp = new Employee("", "", "", -5, "", -100.0, false, "");
        String json = objectMapper.writeValueAsString(invalidEmp);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Validation failed")));
    }

    @Test
    void updateEmployee_existingId_returnsUpdatedEmployee() throws Exception {
        Employee updatedData = new Employee("E001", "Alice", "Williams", 29, "alice.w@example.com", 80000.0, false, "alice_new.jpg");
        String json = objectMapper.writeValueAsString(updatedData);

        mockMvc.perform(put("/api/employees/E001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("E001")))
                .andExpect(jsonPath("$.firstName", is("Alice")))
                .andExpect(jsonPath("$.lastName", is("Williams")))
                .andExpect(jsonPath("$.age", is(29)))
                .andExpect(jsonPath("$.email", is("alice.w@example.com")))
                .andExpect(jsonPath("$.salary", is(80000.0)))
                .andExpect(jsonPath("$.fullTime", is(false)))
                .andExpect(jsonPath("$.imageFile", is("alice_new.jpg")));
    }

    @Test
    void updateEmployee_nonExistingId_returnsNotFound() throws Exception {
        Employee updatedData = new Employee("E999", "Unknown", "User", 30, "unknown@example.com", 50000.0, true, "unknown.jpg");
        String json = objectMapper.writeValueAsString(updatedData);

        mockMvc.perform(put("/api/employees/E999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("/api/employees/E999")));
    }

    @Test
    void deleteEmployee_existingId_deletesSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/employees/E001"))
                .andExpect(status().isNoContent());

        assertFalse(employeeRepository.existsById("E001"));
    }

    @Test
    void deleteEmployee_nonExistingId_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/employees/E999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("/api/employees/E999")));
    }
}
