package com.epam.esm.gcs.dto;

import com.epam.esm.gcs.validator.PurchaseValidationGroup;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(Include.NON_NULL)
public class PurchaseDto extends TruncatedPurchaseDto {

    @Null(message = "{model.field.not.null}")
    private long id;

    @NotNull(message = "{model.field.null}", groups = PurchaseValidationGroup.class)
    private GiftCertificateDto certificate;

    private UserDto user;

}
