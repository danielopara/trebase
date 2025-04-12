package com.tredbase.payment.controller.student;

import com.tredbase.payment.response.BaseResponse;
import com.tredbase.payment.service.student.implementation.StudentService;
import com.tredbase.payment.utils.ResponseHandlers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller for handling student-related operations such as fetching student information.
 */
@RestController
@RequestMapping("/api/v1/payment/student")
@Tag(name = "Student")
public class StudentController {

    private final StudentService studentService;
    private final ResponseHandlers responseHandlers;

    public StudentController(StudentService studentService, ResponseHandlers responseHandlers) {
        this.studentService = studentService;
        this.responseHandlers = responseHandlers;
    }

    /**
     * Endpoint for fetching all students.
     *
     * @return a ResponseEntity containing the list of all students
     */
    @Operation(summary = "Get all students", description = "Retrieve a list of all students.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students successfully fetched"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/")
    public ResponseEntity<?> getStudents() {
        // Fetch the list of students using the service
        BaseResponse response = studentService.getStudents();
        return responseHandlers.handleResponse(response);
    }

    /**
     * Endpoint for fetching a specific student by ID.
     *
     * @param id the ID of the student to fetch
     * @return a ResponseEntity containing the student details
     */
    @Operation(summary = "Get student by ID", description = "Retrieve a specific student by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(
            @Parameter(description = "ID of the student to fetch") @PathVariable UUID id) {

        // Fetch the student by ID using the service
        BaseResponse response = studentService.getStudent(id);
        return responseHandlers.handleResponse(response);
    }
}
