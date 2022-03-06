package com.epam.esm.gcs.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class SignUpUserDto extends LoginUserDto {

    @NotBlank(message = "{model.field.null}")
    private String passwordRepeat;

    @NotBlank(message = "{model.field.null}")
    private String firstName;

    @NotBlank(message = "{model.field.null}")
    private String lastName;

    @Builder(builderMethodName = "signUpBuilder")
    public SignUpUserDto(String email, String password,
                         String passwordRepeat, String firstName,
                         String lastName) {
        super(email, password);
        this.passwordRepeat = passwordRepeat;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
