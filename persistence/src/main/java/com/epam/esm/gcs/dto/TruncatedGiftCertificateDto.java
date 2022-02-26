package com.epam.esm.gcs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TruncatedGiftCertificateDto {

    @NotNull(message = "{model.field.null}")
    private String certificateName;

}
