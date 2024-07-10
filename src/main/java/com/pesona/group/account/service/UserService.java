package com.pesona.group.account.service;

import com.pesona.group.account.application.request.CreateUserAccountDto;
import com.pesona.group.dto.Response.GlobalDatabaseActionResult;

public interface UserService {
    GlobalDatabaseActionResult registerUser(CreateUserAccountDto posCreateUserDto);
}
