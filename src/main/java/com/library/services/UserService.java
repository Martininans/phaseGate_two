package com.library.services;

import com.library.model.entities.User;
import com.library.model.request.CreateUserDto;
import com.library.model.request.SignInDto;
import com.library.model.response.AppResponse;

import java.util.List;

public interface UserService {
    AppResponse addUser(CreateUserDto user);
    AppResponse updateUser(CreateUserDto user);
    AppResponse deleteUser(Long id);
    AppResponse getUserById(Long id);
    List<User> getAllUsers();
    AppResponse getUserByEmail(String email);
    AppResponse signIn(SignInDto signInDto);
}
