package com.tredbase.payment.controller.parent;

import com.tredbase.payment.response.BaseResponse;
import com.tredbase.payment.service.parent.implementation.ParentService;
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
 * Controller for managing parent-related operations such as retrieving parent details.
 */
@RestController
@RequestMapping("/api/v1/payment/parent")
@Tag(name = "Parent Controller")
public class ParentController {

    private final ParentService parentService;
    private final ResponseHandlers responseHandlers;


    public ParentController(ParentService parentService, ResponseHandlers responseHandlers) {
        this.parentService = parentService;
        this.responseHandlers = responseHandlers;
    }

    /**
     * Endpoint for retrieving all parents.
     *
     * @return a ResponseEntity containing a list of all parents or an error message
     */
    @Operation(summary = "Get all parents", description = "Retrieve the list of all parents.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of parents"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/")
    public ResponseEntity<?> getParents() {
        // Call the service to get all parents
        BaseResponse response = parentService.getAllParents();

        return responseHandlers.handleResponse(response);
    }

    /**
     * Endpoint for retrieving a parent by their ID.
     *
     * @param id the unique identifier of the parent
     * @return a ResponseEntity containing the parent details or an error message
     */
    @Operation(summary = "Get parent by ID", description = "Retrieve details of a specific parent by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved parent details"),
            @ApiResponse(responseCode = "404", description = "Parent not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getParent(
            @Parameter(description = "Unique identifier of the parent") @PathVariable UUID id) {

        // Call the service to get parent by ID
        BaseResponse parent = parentService.getParent(id);

        return responseHandlers.handleResponse(parent);
    }
}
