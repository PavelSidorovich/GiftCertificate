package com.epam.esm.gcs.dto;

import com.epam.esm.gcs.validator.OrderValidationGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class OrderDto extends TruncatedOrderDto {

    @NotNull(message = "{model.field.null}", groups = OrderValidationGroup.class)
    private CertificateDto certificate;

    @JsonIgnore
    private UserDto user;

}
