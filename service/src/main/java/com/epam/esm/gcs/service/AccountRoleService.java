package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.AccountRoleDto;

public interface AccountRoleService {

    AccountRoleDto findByName(String roleName);

}
