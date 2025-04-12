package com.tredbase.payment.utils;

import com.tredbase.payment.response.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseHandlers {
    // This method is used to handle responses based on the provided BaseResponse object.
    public ResponseEntity<BaseResponse> handleResponse(BaseResponse response) {
        // Check if the status code of the response is HTTP 200 (OK)
        return response.getStatusCode() == HttpServletResponse.SC_OK
                // If the status code is 200, return a ResponseEntity with status OK
                ? ResponseEntity.ok(response)
                //else return other status code
                : ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
