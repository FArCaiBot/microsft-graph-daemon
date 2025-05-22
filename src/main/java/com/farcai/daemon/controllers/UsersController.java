package com.farcai.daemon.controllers;

import com.farcai.daemon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("drives")
    public ResponseEntity<?> getDrives() {
        return ResponseEntity.ok(userService.getDriveRoot());
    }

    @GetMapping("sharing-link")
    public ResponseEntity<?> getSharedLink(
            @RequestParam String externalId,
            @RequestParam String rol,
            @RequestParam(required = false) String specificUsers
    ) {
        return ResponseEntity.ok(userService.shareWithSpecificUserOnly(externalId, rol, specificUsers));
    }

    @GetMapping("permissions")
    public ResponseEntity<?> getPermissionList(@RequestParam String externalId) {
        return ResponseEntity.ok(userService.getPermissions(externalId));
    }

    @PostMapping("/invite")
    public ResponseEntity<?> setInvitation(@RequestParam String email, @RequestParam String name){
        return ResponseEntity.ok(userService.setInvitation(email, name));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable String userId){
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

}
