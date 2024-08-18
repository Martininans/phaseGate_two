package com.library.model.request;

import lombok.Data;

@Data
public class updateUserDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
}
