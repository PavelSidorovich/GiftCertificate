package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.dto.AccountRoleDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.AccountRoleModel_;
import com.epam.esm.gcs.repository.AccountRoleRepository;
import com.epam.esm.gcs.service.AccountRoleService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountRoleServiceImpl implements AccountRoleService {

    private final AccountRoleRepository accountRoleRepository;
    private final ModelMapper modelMapper;

    @Override
    public AccountRoleDto findByName(String roleName) {
        return modelMapper.map(
                accountRoleRepository.findByName(roleName).orElseThrow(
                        () -> new EntityNotFoundException(AccountRoleDto.class, roleName,
                                                          AccountRoleModel_.ROLE_NAME)
                ),
                AccountRoleDto.class
        );
    }

}
