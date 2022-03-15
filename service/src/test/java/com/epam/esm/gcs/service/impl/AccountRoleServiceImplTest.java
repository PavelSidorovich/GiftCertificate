package com.epam.esm.gcs.service.impl;

import com.epam.esm.gcs.config.ModelMapperConfig;
import com.epam.esm.gcs.dto.AccountRoleDto;
import com.epam.esm.gcs.exception.EntityNotFoundException;
import com.epam.esm.gcs.model.AccountRoleModel;
import com.epam.esm.gcs.repository.AccountRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountRoleServiceImplTest {

    private final AccountRoleServiceImpl accountRoleService;

    private final AccountRoleRepository accountRoleRepository;

    public AccountRoleServiceImplTest(@Mock AccountRoleRepository accountRoleRepository) {
        this.accountRoleRepository = accountRoleRepository;
        this.accountRoleService = new AccountRoleServiceImpl(
                accountRoleRepository, new ModelMapperConfig().modelMapper()
        );
    }

    @Test
    void findByName_shouldReturnAccountRole_ifExistsByName() {
        final String roleName = "ROLE_ADMIN";
        final AccountRoleModel accountRoleModel = new AccountRoleModel(1L, roleName);
        when(accountRoleRepository.findByName(roleName)).thenReturn(Optional.of(accountRoleModel));

        AccountRoleDto actual = accountRoleService.findByName(roleName);

        assertEquals(roleName, actual.getName());
        assertEquals(1L, actual.getId());
    }

    @Test
    void findByName_shouldThrowEntityNotFoundException_ifNotExistsByName() {
        final String roleName = "ROLE_ADMIN";

        when(accountRoleRepository.findByName(roleName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountRoleService.findByName(roleName));
    }

}