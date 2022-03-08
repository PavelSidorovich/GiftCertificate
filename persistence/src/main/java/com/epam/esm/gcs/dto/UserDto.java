package com.epam.esm.gcs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UserDto extends SignUpUserDto {

    @Null(message = "{model.field.not.null}")
    private Long id;

    private BigDecimal balance;
    private Boolean enabled;

    @Builder(builderMethodName = "userDtoBuilder")
    public UserDto(String email, String password, String firstName, String lastName,
                   Long id, BigDecimal balance, Boolean enabled) {
        super(email, password, null, firstName, lastName);
        this.id = id;
        this.balance = balance;
        this.enabled = enabled;
    }

}

