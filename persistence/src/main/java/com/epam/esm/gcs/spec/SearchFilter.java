package com.epam.esm.gcs.spec;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchFilter {

    private String field;
    private QueryOperator operator;
    private Object value;

}
