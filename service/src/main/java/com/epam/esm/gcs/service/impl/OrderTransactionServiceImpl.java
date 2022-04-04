package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.CertificateDto;
import com.epam.esm.gcs.dto.OrderDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.NotEnoughMoneyException;
import com.epam.esm.gcs.model.UserModel;
import com.epam.esm.gcs.model.UserModel_;
import com.epam.esm.gcs.repository.UserRepository;
import com.epam.esm.gcs.service.OrderTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderTransactionServiceImpl implements OrderTransactionService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDto withdrawMoney(UserDto user, CertificateDto certificate) {
        final UserModel userModel = userRepository
                .findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(UserDto.class, UserModel_.ID, user.getId()));
        final BigDecimal userBalance = userModel.getBalance();
        final BigDecimal certificateCost = certificate.getPrice();
        final BigDecimal balanceAfterOrder = userBalance.subtract(certificateCost);

        if (balanceAfterOrder.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughMoneyException(
                    OrderDto.class, certificate.getName(),
                    certificateCost, userBalance
            );
        }
        userModel.setBalance(balanceAfterOrder);

        return modelMapper.map(userModel, UserDto.class);
    }

}
