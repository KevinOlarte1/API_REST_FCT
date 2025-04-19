package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.in.auth.LoginUserDto;
import com.kevinolarte.resibenissa.dto.in.auth.RegisterUserDto;
import com.kevinolarte.resibenissa.dto.in.auth.VerifyUserDto;
import com.kevinolarte.resibenissa.dto.out.UserResponseDto;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.dto.out.LoginResponseDto;
import com.kevinolarte.resibenissa.services.AuthenticationService;
import com.kevinolarte.resibenissa.services.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@AllArgsConstructor
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;


    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> register(@RequestBody RegisterUserDto registerUserDto){
            UserResponseDto user = authenticationService.singUp(registerUserDto);
            return ResponseEntity.ok(user);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(@RequestBody LoginUserDto loginUserDto){
            User userauthentication = authenticationService.authenticate(loginUserDto);
            String token = jwtService.generateToken(userauthentication);
            LoginResponseDto loginResponse = new LoginResponseDto(
                    token,
                    jwtService.getExpirationTime()
            );
            return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestBody VerifyUserDto verifyUserDto){
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("AcountVerfied");

    }

    @PostMapping("/resend")
    public ResponseEntity<String> resendVerificationCode(@RequestParam String email ){
        authenticationService.resendVerificationCode(email);
        return ResponseEntity.ok("Verification Code Resent");

    }
}
