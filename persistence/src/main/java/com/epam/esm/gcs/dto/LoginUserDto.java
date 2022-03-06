package com.epam.esm.gcs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class LoginUserDto {

    @NotBlank(message = "{model.field.null}")
    private String email;

    @NotBlank(message = "{model.field.null}")
    private String password;

}
