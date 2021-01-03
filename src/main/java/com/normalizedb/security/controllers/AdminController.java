package com.normalizedb.security.controllers;

import com.normalizedb.entities.GenericApiSuccess;
import com.normalizedb.security.entities.PrincipalUser;
import com.normalizedb.security.entities.application.RegisterAdminPayload;
import com.normalizedb.security.entities.application.Role;
import com.normalizedb.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(name = "/admin")
public class AdminController {
    private PasswordEncoder encoder;
    private UserService userService;

    @Autowired
    public AdminController(PasswordEncoder encoder, UserService userService) {
        this.encoder = encoder;
        this.userService = userService;
    }

    @PostMapping(path = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericApiSuccess> registerAdmin(@RequestBody RegisterAdminPayload payload) {
        PrincipalUser user = new PrincipalUser(payload.getUsername(),
                                                encoder.encode(payload.getPassword()),
                                                Role.ADMIN.getRole());
        userService.saveUser(user);
        return new ResponseEntity<>(new GenericApiSuccess(HttpStatus.OK.value(), LocalDateTime.now()), HttpStatus.OK);
    }

}
