package com.epam.esm.gcs.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TruncatedPurchaseDto {

    @Null(message = "{model.field.not.null}")
    private long id;

    private BigDecimal cost;
    private LocalDateTime purchaseDate;

}