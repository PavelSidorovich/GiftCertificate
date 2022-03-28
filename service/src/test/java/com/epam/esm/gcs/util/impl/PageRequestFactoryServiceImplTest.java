package com.epam.esm.gcs.auth.impl;

import com.epam.esm.gcs.spec.PageRequestFactory;
import com.epam.esm.gcs.spec.SearchQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageRequestFactoryServiceImplTest {

    private final PageRequestFactoryServiceImpl pageRequestFactoryService;

    private final PageRequestFactory pageRequestFactory;

    public PageRequestFactoryServiceImplTest(@Mock PageRequestFactory pageRequestFactory) {
        this.pageRequestFactory = pageRequestFactory;
        this.pageRequestFactoryService = new PageRequestFactoryServiceImpl(pageRequestFactory);
    }

    @Test
    void pageable_shouldDelegateToAnotherService_always() {
        final SearchQuery searchQuery = mock(SearchQuery.class);

        pageRequestFactoryService.pageable(searchQuery);
        pageRequestFactoryService.pageable(0, 10);

        verify(pageRequestFactory).pageable(searchQuery);
        verify(pageRequestFactory).pageable(0, 10);
    }

}