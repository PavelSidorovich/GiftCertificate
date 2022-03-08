package com.epam.esm.gcs.spec;

import org.springframework.data.domain.Pageable;

public interface PageRequestFactory {

    Pageable pageable(SearchQuery searchQuery);

    Pageable pageable(Integer page, Integer size);

}
