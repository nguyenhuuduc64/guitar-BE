package com.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Authentication & Authorization (4xx)
    UNAUTHENTICATED(401, "User is not authenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "User does not have permission", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(4001, "Invalid username or password", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(401, "Token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(4003, "Token is invalid", HttpStatus.UNAUTHORIZED),

    // User Errors (4xx)
    USER_NOT_FOUND(4041, "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(4091, "User already exists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS(4092, "Email already registered", HttpStatus.CONFLICT),
    INVALID_EMAIL(4004, "Invalid email format", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(4005, "Password does not meet requirements", HttpStatus.BAD_REQUEST),

    //company errors
    COMPANY_NOT_FOUND(4041, "User not found", HttpStatus.NOT_FOUND),


    // Resource Errors (4xx)
    RESOURCE_NOT_FOUND(4040, "Resource not found", HttpStatus.NOT_FOUND),
    RESOURCE_ALREADY_EXISTS(4090, "Resource already exists", HttpStatus.CONFLICT),

    // Validation Errors (4xx)
    VALIDATION_ERROR(4220, "Validation failed", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_INPUT(4000, "Invalid input data", HttpStatus.BAD_REQUEST),
    REQUIRED_FIELD_MISSING(4006, "Required field is missing", HttpStatus.BAD_REQUEST),

    // Business Logic Errors (4xx)
    INSUFFICIENT_BALANCE(4007, "Insufficient balance", HttpStatus.BAD_REQUEST),
    OPERATION_NOT_ALLOWED(4008, "Operation not allowed", HttpStatus.BAD_REQUEST),

    // Server Errors (5xx)
    INTERNAL_SERVER_ERROR(5000, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR(5001, "Database error", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTERNAL_SERVICE_ERROR(5002, "External service error", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_UPLOAD_ERROR(5003, "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR),

    //tokens
    INVALID_TOKEN(5004,"token id expired",HttpStatus.UNAUTHORIZED ),


    //Category
    CATEGORY_NOT_FOUND(5005, "category not found", HttpStatus.NOT_FOUND),

    RECRUITMENT_NOT_FOUND(500, "recruitment not found", HttpStatus.NOT_FOUND),

    //Playlist
    PLAYLIST_NOT_FOUND(5005, "playlist not found", HttpStatus.NOT_FOUND),
    CHORD_NOT_FOUND(5005, "playlist not found", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    // Helper method to find by code
    public static ErrorCode fromCode(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }
}