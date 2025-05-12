package org.example.projectdevtool.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.projectdevtool.dto.LoginRequest;
import org.example.projectdevtool.dto.RegisterRequest;
import org.example.projectdevtool.entity.Users;
import org.example.projectdevtool.security.JwtUtil;
import org.example.projectdevtool.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean haveProfile = userService.checkProfile(userDetails.getUsername());
        String accessToken = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new Token(accessToken, haveProfile));
    }

    @Data
    static class Token{
        private String accessToken;
        private boolean haveProfile;
        public Token(String accessToken, boolean haveProfile) {
            this.accessToken = accessToken; this.haveProfile = haveProfile;
        }
    }
}
