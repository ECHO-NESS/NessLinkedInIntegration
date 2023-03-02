package com.linkedin.oauth.dto;

import lombok.Data;

@Data
public class Response {

    private int statusCode;
    private String status;
    private String message;
}
