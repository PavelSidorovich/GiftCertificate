package com.epam.esm.gcs.service;

import com.epam.esm.gcs.dto.UserDto;

public interface UserService extends Readable<UserDto> {

    UserDto findByEmail(String email);

    boolean existsWithEmail(String email);

}
