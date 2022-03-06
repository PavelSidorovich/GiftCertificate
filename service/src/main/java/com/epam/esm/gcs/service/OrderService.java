package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.OrderDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.dto.TruncatedOrderDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderDto purchase(long userId, long certificateId);

    List<OrderDto> findByUserId(long userId, Pageable pageable);

    TruncatedOrderDto findTruncatedByIds(long userId, long orderId);

//    TagDto findMostWidelyTag();

}
