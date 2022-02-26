package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.OrderDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.dto.TruncatedGiftCertificateDto;
import com.epam.esm.gcs.dto.TruncatedOrderDto;
import com.epam.esm.gcs.dto.UserDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.exception.NoWidelyUsedTagException;
import com.epam.esm.gcs.exception.NotEnoughMoneyException;
import com.epam.esm.gcs.model.OrderModel;
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
import java.util.Set;
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

    /**
     * Creates purchase model
     *
     * @param userId         user id
     * @param certificateDto truncated certificate (contains only name)
     * @return saved purchase
     */
    @Override
    @Transactional
    public OrderDto purchase(long userId, TruncatedGiftCertificateDto certificateDto) {
        OrderDto purchaseDto = new OrderDto();
        UserDto user = userService.findById(userId);
        GiftCertificateDto certificate = certificateService.findByName(
                certificateDto.getCertificateName()
        );

        purchaseDto.setUser(user);
        purchaseDto.setCertificate(certificate);
        checkBalance(certificate, user);

        OrderModel orderModel = purchaseRepository.create(
                modelMapper.map(purchaseDto, OrderModel.class)
        );
        purchaseRepository.flushAndClear();
        return modelMapper.map(orderModel, OrderDto.class);
    }

    /**
     * Finds user purchases
     *
     * @param id      user id
     * @param limiter query limiter
     * @return user purchases
     */
    @Override
    public List<OrderDto> findByUserId(long userId, Limiter limiter) {
        userService.findById(userId); // checks if user exists (throws exception if not)
        List<OrderDto> purchases =
                purchaseRepository.findByUserId(userId, limiter).stream()
                                  .map(purchase -> modelMapper.map(purchase, OrderDto.class))
                                  .collect(Collectors.toList());
        purchaseRepository.clear();
        return purchases;
    }

    /**
     * Finds truncated purchase by user and purchase ids
     *
     * @param userId     user id
     * @param purchaseId purchase id
     * @return truncated purchase (contains only id, cost and purchase date)
     */
    @Override
    public TruncatedOrderDto findTruncatedByIds(long userId, long purchaseId) {
        userService.findById(userId); // checks if user exists (throws exception if not)
        OrderModel certificate = purchaseRepository.findByIds(userId, purchaseId).orElseThrow(
                () -> new EntityNotFoundException(OrderDto.class, ID.getColumnName(), purchaseId)
        );
        purchaseRepository.clear();
        return modelMapper.map(certificate, TruncatedOrderDto.class);
    }

    /**
     * Finds the most widely used tag of the most active user
     *
     * @return the most widely user tag of a user
     */
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

    private Optional<TagModel> findWidelyUsedTag(UserModel user) {
        Limiter limiter = new QueryLimiter(Integer.MAX_VALUE, 0);
        return purchaseRepository.findByUserId(user.getId(), limiter).stream()
                                 .map(purchaseModel -> purchaseModel.getCertificate()
                                                                    .getTags())
                                 .flatMap(Set::stream)
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
                    OrderDto.class, certificate.getName(),
                    certificateCost, userDtoBalance
            );
        }
    }

}
