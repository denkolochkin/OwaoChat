package com.clone.instagram.authservice.endpoint;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.clone.instagram.authservice.exception.BadRequestException;
import com.clone.instagram.authservice.exception.EmailAlreadyExistsException;
import com.clone.instagram.authservice.exception.UsernameAlreadyExistsException;
import com.clone.instagram.authservice.model.Profile;
import com.clone.instagram.authservice.model.Role;
import com.clone.instagram.authservice.model.User;
import com.clone.instagram.authservice.payload.*;
import com.clone.instagram.authservice.service.FacebookService;
import com.clone.instagram.authservice.service.UserService;
import com.example.AutheticateUserQuery;
import com.example.proccesor.AuthProcessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Base64;

@RestController
public class AuthEndpoint {

    @Autowired
    private UserService userService;

    @Autowired
    private FacebookService facebookService;

    private String token;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) throws JSONException {
        AuthProcessor authProcessor = new AuthProcessor();
        String ourToken = authProcessor.getToken(StringUtils.EMPTY, StringUtils.EMPTY);
        System.out.println(ourToken);

        String[] chunks = ourToken.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        JSONObject payloadObject = new JSONObject(payload);
        String email = (String) payloadObject.get("email");

        User user = User
                .builder()
                .username(email)
                .email(email)
                .password(loginRequest.getPassword())
                .userProfile(Profile
                        .builder()
                        .displayName(email)
                        .profilePictureUrl("123123")
                        .build())
                .build();
        userService.registerUser(user, Role.USER);
        token = userService.loginUser(email, loginRequest.getPassword());

        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @PostMapping("/facebook/signin")
    public  ResponseEntity<?> facebookAuth(@RequestBody FacebookLoginRequest facebookLoginRequest) {
        String token = facebookService.loginUser(facebookLoginRequest.getAccessToken());
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody SignUpRequest payload) {

        User user = User
                .builder()
                .username(payload.getUsername())
                .email(payload.getEmail())
                .password(payload.getPassword())
                .userProfile(Profile
                        .builder()
                        .displayName(payload.getName())
                        .profilePictureUrl(payload.getProfilePicUrl())
                        .build())
                .build();

        try {
            userService.registerUser(user, Role.USER);
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
            throw new BadRequestException(e.getMessage());
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(user.getUsername()).toUri();

        return ResponseEntity
                .created(location)
                .body(new ApiResponse(true,"User registered successfully"));
    }
}
