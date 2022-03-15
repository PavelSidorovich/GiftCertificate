package com.epam.esm.gcs.spec;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AbstractSearchCriteria {

    private Integer page;
    private Integer pageSize;

}
