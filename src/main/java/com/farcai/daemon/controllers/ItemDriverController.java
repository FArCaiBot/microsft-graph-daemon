package com.farcai.daemon.controllers;

import com.farcai.daemon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("drives")
public class ItemDriverController {

    @Autowired
    private UserService userService;

    @GetMapping("/path")
    public ResponseEntity<?> getByPath(@RequestParam("url") String url){
        return ResponseEntity.ok(userService.getDriveItemByPath(url));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> getById(@PathVariable String itemId){
        return ResponseEntity.ok(userService.getDriveItemById(itemId));
    }
}
