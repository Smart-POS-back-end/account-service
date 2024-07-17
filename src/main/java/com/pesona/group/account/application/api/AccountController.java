package com.pesona.group.account.application.api;

import com.pesona.group.account.service.UserService;
import com.pesona.group.account.application.request.CreateUserAccountDto;
import com.pesona.group.dto.Response.GlobalDatabaseActionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final UserService userService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<GlobalDatabaseActionResult> register(@RequestBody CreateUserAccountDto createUserAccountDto) {
        GlobalDatabaseActionResult globalDatabaseActionResult = userService.registerUser(createUserAccountDto);
        return ResponseEntity.ok(globalDatabaseActionResult);
    }

}
