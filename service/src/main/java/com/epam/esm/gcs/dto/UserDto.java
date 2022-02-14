package com.epam.esm.gcs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;
import java.math.BigDecimal;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UserDto {

    @Null(message = "{model.field.not.null}")
    private long id;

    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal balance;

}
