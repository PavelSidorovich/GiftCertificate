package com.epam.esm.gcs.response;

import com.epam.esm.gcs.dto.GiftCertificateDto;
import com.epam.esm.gcs.dto.OrderDto;
import com.epam.esm.gcs.dto.TagDto;
import com.epam.esm.gcs.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    TAG("01", TagDto.class),
    CERTIFICATE("02", GiftCertificateDto.class),
    USER("03", UserDto.class),
    PURCHASE("04", OrderDto.class),
    DEFAULT("00", Object.class);

    private final String postfixCode;
    private final Class<?> clazzForPostfix;

    public static String findPostfixByClass(Class<?> clazz) {
        return Arrays.stream(values())
                     .filter(code -> code.clazzForPostfix.equals(clazz))
                     .findAny()
                     .map(ResponseCode::getPostfixCode)
                     .orElseGet(DEFAULT::getPostfixCode);
    }

}
