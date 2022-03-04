package com.epam.esm.gcs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AccountRoleDto {

    private Long id;
    private String name;

}
