package com.esprit.pi.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", status != null ? status : "Unknown");
        errorDetails.put("error", message != null ? message : "No message available");
        errorDetails.put("exception", exception != null ? exception.toString() : "No exception");

        HttpStatus httpStatus = status != null ? HttpStatus.valueOf(Integer.parseInt(status.toString())) : HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(errorDetails, httpStatus);
    }
}