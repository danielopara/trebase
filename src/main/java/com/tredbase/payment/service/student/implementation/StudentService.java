package com.tredbase.payment.service.student.implementation;

import com.tredbase.payment.dto.ParentDto;
import com.tredbase.payment.dto.StudentDto;
import com.tredbase.payment.entity.Parent;
import com.tredbase.payment.entity.Student;
import com.tredbase.payment.repository.StudentRepository;
import com.tredbase.payment.response.BaseResponse;
import com.tredbase.payment.service.student.StudentInterface;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StudentService implements StudentInterface {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    /**
     * Fetches all students from the repository.
     * Maps the students to StudentDto objects for response.
     * @return BaseResponse containing the list of students
     */
    @Override
    public BaseResponse getStudents() {
        try{
            // Fetch all students from the repository
            List<Student> students = studentRepository.findAll();
            // Convert the list of students to a list of StudentDto objects
            List<StudentDto> list = students.stream().map(student  ->{
                // Create a new StudentDto object to hold the mapped data
                StudentDto dto = new StudentDto();
                dto.setId(student.getId());
                dto.setFirst_name(student.getFirst_name());
                dto.setLast_name(student.getLast_name());
                dto.setBalance(student.getBalance());

                // Map the student's parents to a list of maps for the response DTO
                List<Map<String, Object>> parentDtos =student.getParents().stream().map(parent ->{
                    // Create a map to hold parent details
                    Map<String, Object> parentDto = new HashMap<>();
                    parentDto.put("id", parent.getId());
                    parentDto.put("firstName",parent.getFirstName());
                    parentDto.put("lastName",parent.getLastname());
                    parentDto.put("balance",parent.getBalance());

                    return parentDto;
                }).toList(); // Collect the parents' details into a list

                dto.setParent(parentDtos);
                return dto; // Collect the StudentDto objects into a list
            }).toList();

            return BaseResponse.createSuccessResponse("success", list);
        } catch(Exception e){
            return BaseResponse.createErrorResponse("error", e);
        }
    }

    /**
     * Fetches a specific student by ID from the repository.
     * Maps the student to a StudentDto object.
     * @param id The student ID
     * @return BaseResponse containing the student DTO or error message
     */
    @Override
    public BaseResponse getStudent(UUID id) {
        try{
            Optional<Student> checkStudent = studentRepository.findById(id);
            if (checkStudent.isEmpty()){
                return BaseResponse.createErrorResponse("student does not exist",  id);
            }
            Student student = checkStudent.get();
            StudentDto studentDto = new StudentDto();
            studentDto.setId(student.getId());
            studentDto.setFirst_name(student.getFirst_name());
            studentDto.setLast_name(student.getLast_name());
            studentDto.setBalance(student.getBalance());

            List<Map<String, Object>> parentsDto = student.getParents().stream().map(parent -> {
                Map<String, Object> studentParent = new HashMap<>();
                studentParent.put("firstName", parent.getFirstName());
                studentParent.put("lastName", parent.getLastname());
                studentParent.put("balance", parent.getBalance());
                studentParent.put("id", parent.getId());
            return studentParent;
            }).toList();

            studentDto.setParent(parentsDto);

            return BaseResponse.createSuccessResponse("success", studentDto);
        }catch(Exception e){
            return BaseResponse.createErrorResponse("error", e);
        }
    }
}
