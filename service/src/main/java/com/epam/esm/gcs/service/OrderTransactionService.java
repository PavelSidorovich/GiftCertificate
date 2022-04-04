package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.CertificateDto;
import com.epam.esm.gcs.dto.UserDto;

public interface OrderTransactionService {

    UserDto withdrawMoney(UserDto user, CertificateDto certificate);

}
