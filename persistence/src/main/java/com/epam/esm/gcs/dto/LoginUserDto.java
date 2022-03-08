package com.epam.esm.gcs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
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

    @JsonProperty(access = Access.WRITE_ONLY)
    @NotBlank(message = "{model.field.null}")
    private String password;

}
