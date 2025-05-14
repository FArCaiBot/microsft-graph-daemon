package com.farcai.daemon.controllers;

import com.farcai.daemon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
 @RequestMapping("users")
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("drives")
    public ResponseEntity<?> getDrives(){
        return ResponseEntity.ok(userService.getDriveRoot());
    }
}
