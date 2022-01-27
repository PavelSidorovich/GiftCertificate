package com.epam.esm.gcs.web.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseModel {

    private final String responseCode;
    private final String message;

    public ResponseModel(HttpStatus httpStatus, Class<?> clazz, String message) {
        this.responseCode = httpStatus.value() + ResponseCode.findPostfixByClass(clazz);
        this.message = message;
    }

    public ResponseModel(HttpStatus httpStatus, String message) {
        this.responseCode = httpStatus.value() + ResponseCode.findPostfixByClass(Object.class);
        this.message = message;
    }

}
