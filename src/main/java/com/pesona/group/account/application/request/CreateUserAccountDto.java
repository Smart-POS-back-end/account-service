package com.pesona.group.account.application.request;

import lombok.Builder;

@Builder
public record CreateUserAccountDto(String firstName, String lastName, String email, String password, String username) {
}
