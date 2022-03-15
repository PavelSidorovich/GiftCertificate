package com.epam.esm.gcs.spec.impl;

import com.epam.esm.gcs.spec.PageRequestFactory;
import com.epam.esm.gcs.spec.SearchQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PageRequestFactoryImpl implements PageRequestFactory {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MIN_PAGE = 0;
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 100;

    @Override
    public Pageable pageable(SearchQuery searchQuery) {
        final int pageNumber = getValidPageNumber(searchQuery.getPageNumber());
        final int pageSize = getValidPageSize(searchQuery.getPageSize());
        final List<Order> orders = new ArrayList<>();
        final List<String> ascProps = searchQuery.getSortOrder().getAscendingOrder();
        final List<String> descProps = searchQuery.getSortOrder().getDescendingOrder();

        orders.addAll(fillQueryOrderProps(ascProps, Order::asc));
        orders.addAll(fillQueryOrderProps(descProps, Order::desc));
        Sort sort = Sort.by(orders);

        return PageRequest.of(pageNumber, pageSize, sort);
    }

    @Override
    public Pageable pageable(Integer page, Integer size) {
        return PageRequest.of(getValidPageNumber(page), getValidPageSize(size));
    }

    private List<Order> fillQueryOrderProps(List<String> orderProps, Function<String, Order> function) {
        if (orderProps != null && !orderProps.isEmpty()) {
            return orderProps.stream()
                             .map(function)
                             .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private int getValidPageNumber(Integer pageNumber) {
        return pageNumber == null || pageNumber < MIN_PAGE
                ? DEFAULT_PAGE : pageNumber;
    }

    private int getValidPageSize(Integer pageSize) {
        return pageSize == null || pageSize < MIN_SIZE || pageSize > MAX_SIZE
                ? DEFAULT_SIZE : pageSize;
    }

}
