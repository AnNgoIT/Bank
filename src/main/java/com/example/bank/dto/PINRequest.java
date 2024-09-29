package com.example.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PINRequest {
    @NotBlank(message = "pin is required")
    @Pattern(regexp = "\\d{6}", message = "pin must be exactly 6 digits")
    private String pin;
}
