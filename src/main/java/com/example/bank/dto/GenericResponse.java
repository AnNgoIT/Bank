package com.example.bank.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GenericResponse {
    private Boolean success;
    private String message;
    private Object result;
    private int statusCode;
}
