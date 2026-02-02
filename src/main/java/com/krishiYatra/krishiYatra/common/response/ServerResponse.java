package com.krishiYatra.krishiYatra.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse {

    private String message;

    private String fieldName;

    private HttpStatus httpStatus;

    private int statusCode;

    private HttpHeaders httpHeaders;

    private Object response;

    private int totalItems;


    public ServerResponse(String message, int statusCode, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.statusCode = statusCode;
    }

    public ServerResponse(String fieldName, String message, int statusCode, HttpStatus status, HttpHeaders headers) {
        this.fieldName = fieldName;
        this.message = message;
        this.httpStatus = status;
        this.httpHeaders = headers;
        this.statusCode = statusCode;
    }

    public ServerResponse(String message, int value, HttpStatus httpStatus, Object response) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.statusCode = value;
        this.response = response;
    }

    public ServerResponse(String message, int value, HttpStatus httpStatus, Object object, int totalItems) {
        this.message = message;
        this.statusCode = value;
        this.httpStatus = httpStatus;
        this.response = object;
        this.totalItems = totalItems;
    }


    public static ServerResponse successResponse(String message, HttpStatus httpStatus) {
        return new ServerResponse(message, httpStatus.value(), httpStatus);
    }


    public static ServerResponse successObjectResponse(String message, HttpStatus httpStatus, Object object) {
        return new ServerResponse(message, httpStatus.value(), httpStatus, object);
    }

    public static ServerResponse successObjectResponse(String message, HttpStatus httpStatus, Object object, int totalItems) {
        return new ServerResponse(message, httpStatus.value(), httpStatus, object, totalItems);
    }


    public static ServerResponse failureResponse(String message, HttpStatus httpStatus) {
        return new ServerResponse(message, httpStatus.value(), httpStatus);
    }
}
