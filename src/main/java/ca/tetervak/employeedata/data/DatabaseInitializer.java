package ca.tetervak.employeedata.data;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

@Slf4j
@Component
public class DatabaseInitializer {

    @Value("classpath:data/employee-data.json")
    private Resource resourceFile;

    private final EmployeeRepository employeeRepository;
    private final ObjectMapper objectMapper;

    public DatabaseInitializer(
            EmployeeRepository employeeRepository,
            ObjectMapper objectMapper
    ) {
        this.employeeRepository = employeeRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        if (employeeRepository.count() == 0) {
            try {
                InputStream inputStream = resourceFile.getInputStream();
                EmployeeData employeeData = objectMapper.readValue(inputStream, EmployeeData.class);
                employeeRepository.saveAll(employeeData.getEmployees());
            } catch (java.io.IOException e) {
                throw new RuntimeException("Failed to initialize database from JSON file", e);
            }
            log.info("Database initialized with {} employees", employeeRepository.count());
        }
    }
}
