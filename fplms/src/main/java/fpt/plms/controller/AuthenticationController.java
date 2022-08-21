package fpt.plms.controller;

import fpt.plms.model.response.GoogleTokenResponse;
import fpt.plms.model.response.Response;
import fpt.plms.service.constant.ServiceMessage;
import fpt.plms.service.constant.ServiceStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import fpt.plms.model.request.CreateUserRequest;
import fpt.plms.service.AuthenticationService;

@RestController
@RequestMapping(value = "/api/auth/management")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;

    @GetMapping
    public Response<String> getAccessToken(@RequestParam String code) {
        GoogleTokenResponse googleTokenResponse = authenticationService.validateGoogleToken(code);
        if(googleTokenResponse == null)
            return new Response<>(ServiceStatusCode.BAD_REQUEST_STATUS,ServiceMessage.INVALID_ARGUMENT_MESSAGE);
        return new Response<>(ServiceStatusCode.OK_STATUS,ServiceMessage.SUCCESS_MESSAGE, googleTokenResponse.getAccessToken());
    }

    @PostMapping
    public void createUser(@RequestBody CreateUserRequest createUserRequest) {
        authenticationService.createUser(createUserRequest);
    }

}

