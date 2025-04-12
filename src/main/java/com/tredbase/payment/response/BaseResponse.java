package com.tredbase.payment.response;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
public class BaseResponse {
    private int statusCode;
    private String message;
    private Object data;

    public BaseResponse() {
    }

    public BaseResponse(int statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static BaseResponse createSuccessResponse( String message, Object data){
        return new BaseResponse(HttpServletResponse.SC_OK, message, data);
    }

    public static BaseResponse createErrorResponse(String message, Object data){
        return new BaseResponse(HttpServletResponse.SC_BAD_REQUEST, message, data);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
