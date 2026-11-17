package ca.tetervak.employeedata.errors;

public record ApiError(
        int status,
        String error,
        String message
){}
