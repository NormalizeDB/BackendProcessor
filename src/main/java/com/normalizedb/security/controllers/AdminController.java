package com.normalizedb.security.controllers;

import com.normalizedb.entities.GenericApiSuccess;
import com.normalizedb.security.entities.PrincipalUser;
import com.normalizedb.security.entities.application.RegisterAdminPayload;
import com.normalizedb.security.entities.application.Role;
import com.normalizedb.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

@Path("/admin")
@Controller
public class AdminController {
    private PasswordEncoder encoder;
    private UserService userService;

    @Autowired
    public AdminController(PasswordEncoder encoder, UserService userService) {
        this.encoder = encoder;
        this.userService = userService;
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public Response registerAdmin(RegisterAdminPayload payload) {
        PrincipalUser user = new PrincipalUser(payload.getUsername(),
                                                encoder.encode(payload.getPassword()),
                                                Role.ADMIN.getRole());
        userService.saveUser(user);
        return Response.ok().entity(new GenericApiSuccess(Response.Status.OK.getStatusCode(), LocalDateTime.now())).build();
    }

}
