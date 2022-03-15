package com.epam.esm.gcs.spec;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinColumnProps {

    private String joinColName;
    private SearchFilter searchFilter;

}
