package com.epam.esm.gcs.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;
import java.math.BigDecimal;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UserDto {

    @Null(message = "{model.field.not.null}")
    private Long id;

    @JsonIgnore
    private String password;

    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal balance;

}
