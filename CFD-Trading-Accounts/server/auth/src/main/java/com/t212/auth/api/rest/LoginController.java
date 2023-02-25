package com.t212.auth.api.rest;

import com.t212.auth.api.rest.models.ApiResponse;
import com.t212.auth.api.rest.models.LoginInput;
import com.t212.auth.api.rest.models.UserOutput;
import com.t212.auth.core.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> authenticate(
            @RequestBody LoginInput request
    ) {
        if (request != null && request.password != null && request.username != null) {
            UserOutput user = loginService.findUserByUsernameAndPassword(request);
            if (user != null) {
                return ResponseEntity.status(200).body(new ApiResponse(200, "", user));
            }
            return ResponseEntity.status(404).body(new ApiResponse(404, "Not found user"));
        }
        return ResponseEntity.status(400).body(new ApiResponse(400, "An error has occurred"));
    }
}
