package com.tredbase.payment.service.parent.implementation;

import com.tredbase.payment.dto.ParentDto;
import com.tredbase.payment.entity.Parent;
import com.tredbase.payment.repository.ParentRepository;
import com.tredbase.payment.response.BaseResponse;
import com.tredbase.payment.service.parent.ParentInterface;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ParentService implements ParentInterface {
    private final ParentRepository parentRepository;

    public ParentService(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
    }

    @Override
    public BaseResponse getAllParents() {
        try{
            List<Parent> parents = parentRepository.findAll();
            List<ParentDto> dtoList = parents.stream().map(parent -> {
                ParentDto dto = new ParentDto();
                dto.setId(parent.getId());
                dto.setFirstName(parent.getFirstName());
                dto.setLastName(parent.getLastname());
                dto.setBalance(parent.getBalance());

                List<Map<String, Object>> studentDtos = parent.getStudents().stream().map(student -> {
                    Map<String, Object> parentStudent = new HashMap<>();
                    parentStudent.put("id", student.getId());
                    parentStudent.put("firstName",student.getFirst_name());
                    parentStudent.put("lastName",student.getLast_name());
                    parentStudent.put("balance",student.getBalance());
                    return parentStudent;
                }).toList();

                dto.setStudents(studentDtos);
                return dto;
            }).toList();
            return BaseResponse.createSuccessResponse(
                    "success",
                    dtoList
            );
        }catch (Exception e){
            return BaseResponse.createErrorResponse("INTERNAL SERVER ERROR", null);
        }
    }

    @Override
    public BaseResponse getParent(UUID id) {
        try{
            Optional<Parent> checkParent = parentRepository.findById(id);
            if(checkParent.isEmpty()){
                return BaseResponse.createErrorResponse("parent not found", null);
            }
            Parent parent = checkParent.get();
            ParentDto parentDto = new ParentDto();
            parentDto.setId(parent.getId());
            parentDto.setBalance(parent.getBalance());
            parentDto.setFirstName(parent.getFirstName());
            parentDto.setLastName(parent.getLastname());

            List<Map<String, Object>> studentDtos = parent.getStudents().stream().map(student -> {
                Map<String, Object> parentStudent = new HashMap<>();
                parentStudent.put("balance", student.getBalance());
                parentStudent.put("id",student.getId());
                parentStudent.put("firstName",student.getFirst_name());
                parentStudent.put("lastName",student.getLast_name());
                return parentStudent;
            }).toList();
            parentDto.setStudents(studentDtos);

            return BaseResponse.createSuccessResponse("success", parentDto);
        } catch (Exception e){
            return BaseResponse.createErrorResponse("error", e);
        }
    }
}
