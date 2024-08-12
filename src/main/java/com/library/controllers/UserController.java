package com.library.controllers;

import com.library.model.entities.User;
import com.library.model.request.CreateUserDto;
import com.library.model.request.SignInDto;
import com.library.model.response.AppResponse;
import com.library.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;


    @PostMapping(path="/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public AppResponse addUser(@RequestBody CreateUserDto data) {
        return userService.addUser(data);
    }

    @PostMapping("/login")
    public AppResponse login(@RequestBody SignInDto data) {
        return userService.signIn(data);
    }

    @PutMapping("/update")
    public AppResponse updateUser(@RequestBody  CreateUserDto data) {
        return userService.updateUser(data);
    }

    @GetMapping(path="/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
