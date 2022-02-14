package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.PurchaseDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.dto.TruncatedPurchaseDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.NoWidelyUsedTagException;
import com.epam.esm.gcs.exception.NotEnoughMoneyException;
import com.epam.esm.gcs.model.PurchaseModel;
import com.epam.esm.gcs.model.TagModel;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.repository.PurchaseRepository;
import com.epam.esm.gcs.service.GiftCertificateService;
import com.epam.esm.gcs.service.PurchaseService;
import com.epam.esm.gcs.service.UserService;
import com.epam.esm.gcs.util.Limiter;
import com.epam.esm.gcs.util.impl.QueryLimiter;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.epam.esm.gcs.repository.column.GiftCertificateColumn.*;

@Service
@AllArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final UserService userService;
    private final PurchaseRepository purchaseRepository;
    private final GiftCertificateService certificateService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public PurchaseDto purchase(PurchaseDto purchaseDto) {
        UserDto user = userService.findById(purchaseDto.getUser().getId());
        GiftCertificateDto certificate = certificateService.findByName(
                purchaseDto.getCertificate().getName()
        );

        purchaseDto.setUser(user);
        purchaseDto.setCertificate(certificate);
        checkBalance(certificate, user);

        PurchaseModel purchaseModel = purchaseRepository.create(
                modelMapper.map(purchaseDto, PurchaseModel.class)
        );
        purchaseRepository.flushAndClear();
        return modelMapper.map(purchaseModel, PurchaseDto.class);
    }

    @Override
    public List<PurchaseDto> findByUserId(long id, Limiter limiter) {
        List<PurchaseDto> purchases =
                purchaseRepository.findByUserId(id, limiter).stream()
                                  .map(purchase -> modelMapper.map(purchase.withoutUser(), PurchaseDto.class))
                                  .collect(Collectors.toList());
        purchaseRepository.clear();
        return purchases;
    }

    @Override
    public TruncatedPurchaseDto findTruncatedByIds(long userId, long purchaseId) {
        userService.findById(userId);
        PurchaseModel certificate = purchaseRepository.findByIds(userId, purchaseId).orElseThrow(
                () -> new EntityNotFoundException(PurchaseDto.class, ID.getColumnName(), purchaseId)
        );
        purchaseRepository.clear();
        return modelMapper.map(certificate, TruncatedPurchaseDto.class);
    }

    @Override
    public TagDto findMostWidelyTag() {
        Optional<UserModel> user = purchaseRepository.findTheMostActiveUser();

        if (user.isPresent()) {
            Optional<TagModel> tag = findWidelyUsedTag(user.get());
            if (tag.isPresent()) {
                purchaseRepository.clear();
                return modelMapper.map(tag.get(), TagDto.class);
            }
        }
        throw new NoWidelyUsedTagException(TagDto.class);
    }

    private Optional<TagModel> findWidelyUsedTag(UserModel purchase) {
        Limiter limiter = new QueryLimiter(Integer.MAX_VALUE, 0);
        return purchaseRepository.findByUserId(purchase.getId(), limiter).stream()
                                 .map(purchaseModel -> purchaseModel.getCertificate()
                                                                    .getTags())
                                 .flatMap(List::stream)
                                 .collect(Collectors.groupingBy(Function.identity(),
                                                                Collectors.counting()))
                                 .entrySet()
                                 .stream()
                                 .max(Entry.comparingByValue())
                                 .map(Entry::getKey);
    }

    private void checkBalance(GiftCertificateDto certificate, UserDto userDto) {
        final BigDecimal userDtoBalance = userDto.getBalance();
        final BigDecimal certificateCost = certificate.getPrice();

        if (userDtoBalance.subtract(certificateCost).compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughMoneyException(
                    PurchaseDto.class, certificate.getName(),
                    certificateCost, userDtoBalance
            );
        }
    }

}