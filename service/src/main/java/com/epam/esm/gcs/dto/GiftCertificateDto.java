package com.epam.esm.gcs.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class GiftCertificateDto {

    @Null(message = "{model.field.not.null}")
    private Long id;

    @NotBlank(message = "{model.field.null}")
    @Size(max = 128, message = "{model.field.size.max}")
    private String name;

    @Size(max = 256, message = "{model.field.size.max}")
    @NotNull(message = "{model.field.null}")
    private String description;

    @Positive(message = "{model.field.positive}")
    @NotNull(message = "{model.field.null}")
    private BigDecimal price;

    @Positive(message = "{model.field.positive}")
    @NotNull(message = "{model.field.null}")
    private Integer duration;

    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
    private List<TagDto> tags;

}
