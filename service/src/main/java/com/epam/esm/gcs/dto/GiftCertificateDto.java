package com.epam.esm.gcs.dto;

import com.epam.esm.gcs.validator.CreateValidationGroup;
import com.epam.esm.gcs.validator.UpdateValidateGroup;
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

    @Null(message = "{model.field.not.null}", groups = { CreateValidationGroup.class })
    private Long id;

    @NotBlank(message = "{model.field.null}", groups = { CreateValidationGroup.class })
    @Size(max = 128, message = "{model.field.size.max}",
            groups = { UpdateValidateGroup.class, CreateValidationGroup.class })
    private String name;

    @Size(max = 256, message = "{model.field.size.max}",
            groups = { UpdateValidateGroup.class, CreateValidationGroup.class })
    @NotNull(message = "{model.field.null}", groups = { CreateValidationGroup.class })
    private String description;

    @Positive(message = "{model.field.positive}",
            groups = { UpdateValidateGroup.class, CreateValidationGroup.class })
    @NotNull(message = "{model.field.null}", groups = { CreateValidationGroup.class })
    private BigDecimal price;

    @Positive(message = "{model.field.positive}",
            groups = { UpdateValidateGroup.class, CreateValidationGroup.class })
    @NotNull(message = "{model.field.null}", groups = { CreateValidationGroup.class })
    private Integer duration;

    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
    private List<TagDto> tags;

}
