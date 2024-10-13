package com.example.pilates_studio.controller;

import com.example.pilates_studio.model.Users;
import com.example.pilates_studio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /*
    @PostMapping("/register")
    public Users register(@RequestBody Users user){
        return userService.register(user);
    }
    */
}
