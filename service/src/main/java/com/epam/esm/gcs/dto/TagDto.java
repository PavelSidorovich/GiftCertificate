package com.epam.esm.gcs.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TagDto {

    @Null(message = "{model.field.not.null}")
    private Long id;

    @NotBlank(message = "{model.field.null}")
    @Size(max = 128, message = "{model.field.size.max}")
    private String name;

}
