package com.epam.esm.gcs.util;

import com.epam.esm.gcs.spec.SearchQuery;
import org.springframework.data.domain.Pageable;

public interface PageRequestFactoryService {

    Pageable pageable(SearchQuery searchQuery);

    Pageable pageable(Integer page, Integer size);

}
