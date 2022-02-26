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
@AllArgsConstructor
public class TruncatedOrderDto {

    @Null(message = "{model.field.not.null}")
    private long id;

    private BigDecimal cost;
    private LocalDateTime purchaseDate;

}