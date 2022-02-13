package com.epam.esm.gcs.dto;

import com.epam.esm.gcs.validator.PurchaseValidationGroup;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class PurchaseDto {

    @Null(message = "{model.field.not.null}")
    private long id;

    @NotNull(message = "{model.field.null}", groups = PurchaseValidationGroup.class)
    private GiftCertificateDto certificate;

    private UserDto user;
    private BigDecimal cost;
    private LocalDateTime purchaseDate;

}
