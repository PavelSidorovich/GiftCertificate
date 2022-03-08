package com.epam.esm.gcs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class SignUpUserDto extends LoginUserDto {

    @JsonProperty(access = Access.WRITE_ONLY)
    @Pattern(regexp = "^.{8,256}$", message = "{user.signup.password.strength}")
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
