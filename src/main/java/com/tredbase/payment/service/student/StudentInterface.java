package com.tredbase.payment.service.student;

import com.tredbase.payment.response.BaseResponse;

import java.util.UUID;

/**
 * Defines the services for student-related services such as fetching one or all students.
 */
public interface StudentInterface {
    /**
     * Retrieves all students in the system.
     *
     * @return A BaseResponse containing the list of students or error details
     */
    BaseResponse getStudents();

    /**
     * Retrieves a specific student by their unique identifier.
     *
     * @param id The UUID of the student
     * @return A BaseResponse containing the student or error details
     */
    BaseResponse getStudent(UUID id);
}
