package com.epam.esm.gcs.spec;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SortOrder {

    private List<String> ascendingOrder;
    private List<String> descendingOrder;

}
