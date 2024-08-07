package dev.m2t.unlucky.controller;

import dev.m2t.unlucky.dto.BaseResponse;
import dev.m2t.unlucky.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/invites")
    public ResponseEntity<BaseResponse> getPendingUserInvites(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username"); // or whatever claim holds the username
        return ResponseEntity.ok(userService.getPendingUserInvites(username));
    }

    @GetMapping("/{username}")
    public ResponseEntity<BaseResponse> getUser(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUser(username));
    }

}
