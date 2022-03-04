package com.epam.esm.gcs.spec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SearchQuery {

    private List<SearchFilter> filters;
    private List<JoinColumnProps> joinColumnProps;
    private SortOrder sortOrder;
    private int pageNumber;
    private int pageSize;

}
