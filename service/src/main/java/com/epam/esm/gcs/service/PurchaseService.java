package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.PurchaseDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.dto.TruncatedPurchaseDto;
import com.epam.esm.gcs.util.Limiter;

import java.util.List;

public interface PurchaseService {

    PurchaseDto purchase(PurchaseDto purchaseDto);

    List<PurchaseDto> findByUserId(long id, Limiter limiter);

    TruncatedPurchaseDto findTruncatedByIds(long userId, long certificateId);

    TagDto findMostWidelyTag();

}
