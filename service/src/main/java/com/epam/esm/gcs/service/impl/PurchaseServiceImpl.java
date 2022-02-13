package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.PurchaseDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.NotEnoughMoneyException;
import com.epam.esm.gcs.model.PurchaseModel;
import com.epam.esm.gcs.repository.PurchaseRepository;
import com.epam.esm.gcs.service.GiftCertificateService;
import com.epam.esm.gcs.service.PurchaseService;
import com.epam.esm.gcs.service.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

    private void checkBalance(GiftCertificateDto certificate, UserDto userDto) {
        final BigDecimal userDtoBalance = userDto.getBalance();
        final BigDecimal certificateCost = certificate.getPrice();

        if (userDtoBalance.subtract(certificateCost).compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughMoneyException(
                    certificate.getName(), certificateCost,
                    userDtoBalance, PurchaseDto.class
            );
        }
    }

}
