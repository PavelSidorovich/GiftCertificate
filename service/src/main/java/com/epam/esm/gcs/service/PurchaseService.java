package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.OrderDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.dto.TruncatedGiftCertificateDto;
import com.epam.esm.gcs.dto.TruncatedOrderDto;
import com.epam.esm.gcs.util.Limiter;

import java.util.List;

public interface PurchaseService {

    OrderDto purchase(long userId, TruncatedGiftCertificateDto certificateDto);

    List<OrderDto> findByUserId(long userId, Limiter limiter);

    TruncatedOrderDto findTruncatedByIds(long userId, long certificateId);

    TagDto findMostWidelyTag();

}
