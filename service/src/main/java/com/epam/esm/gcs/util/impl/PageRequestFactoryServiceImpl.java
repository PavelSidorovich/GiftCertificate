package com.epam.esm.gcs.util.impl;

import com.epam.esm.gcs.spec.PageRequestFactory;
import com.epam.esm.gcs.spec.SearchQuery;
import com.epam.esm.gcs.util.PageRequestFactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PageRequestFactoryServiceImpl implements PageRequestFactoryService {

    private final PageRequestFactory pageRequestFactory;

    @Override
    public Pageable pageable(SearchQuery searchQuery) {
        return pageRequestFactory.pageable(searchQuery);
    }

    @Override
    public Pageable pageable(Integer page, Integer size) {
        return pageRequestFactory.pageable(page, size);
    }

}
