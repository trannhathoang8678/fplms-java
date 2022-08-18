package fpt.plms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import fpt.plms.model.request.CreateUserRequest;
import fpt.plms.service.AuthenticationService;

@RestController
@RequestMapping(value = "/api/auth/management")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping
    public void createUser(@RequestBody CreateUserRequest createUserRequest){
        authenticationService.createUser(createUserRequest);
    }

}

