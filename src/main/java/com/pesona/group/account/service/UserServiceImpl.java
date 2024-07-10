package com.pesona.group.account.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pesona.group.account.application.request.CreateUserAccountDto;
import com.pesona.group.dto.Response.GlobalDatabaseActionResult;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    @Value("${smartpos.auth-server}")
    private String authBaseUrl;
    @Value("${smartpos.auth-username}")
    private String authUsername;
    @Value("${smartpos.auth-password}")
    private String authPassword;

    @Override
    public GlobalDatabaseActionResult registerUser(CreateUserAccountDto posCreateUserDto) {
        WebClient webClient = WebClient.create(authBaseUrl);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", authUsername);
        formData.add("password", authPassword);
        formData.add("grant_type", "password");
        formData.add("client_id", "admin-cli");

        AdminTokenResponseDto adminTokenResponseDto = webClient.post()
                .uri("/realms/master/protocol/openid-connect/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(AdminTokenResponseDto.class)
                .block();
        assert adminTokenResponseDto != null;

        List<RequestCreateUserCredential> createUserCredentials = new ArrayList<>();
        createUserCredentials.add(RequestCreateUserCredential.builder()
                .type("password")
                .value(posCreateUserDto.password())
                .temporary(false)
                .build());

        RequestCreateUser userValue = RequestCreateUser.builder()
                .username(posCreateUserDto.username())
                .firstName(posCreateUserDto.firstName())
                .lastName(posCreateUserDto.lastName())
                .email(posCreateUserDto.email())
                .requestCreateUserCredentials(createUserCredentials)
                .enabled(true)
                .build();

        webClient.post()
                .uri("/admin/realms/account-service/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminTokenResponseDto.accessToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(userValue)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return GlobalDatabaseActionResult.builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .payload(posCreateUserDto)
                .message("SAVE SUCCESSFULLY")
                .build();
    }

    record AdminTokenResponseDto(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") int expiresIn,
            @JsonProperty("refresh_expires_in") int refreshExpiresIn,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("not-before-policy") int notBeforePolicy,
            @JsonProperty("session_state") String sessionState,
            @JsonProperty("scope") String scope
                                 ) {
    }

    @Builder
    record RequestCreateUser(
            String username, String firstName, String email, String lastName, Boolean enabled,
            @JsonProperty("credentials") List<RequestCreateUserCredential> requestCreateUserCredentials
        )
    {}

    @Builder
    record RequestCreateUserCredential(String type, String value, Boolean temporary) {}
}
