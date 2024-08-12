package com.library.model.request;

import com.library.enums.Gender;
import com.library.enums.UserType;
import lombok.Data;

@Data
public class CreateUserDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Gender gender;
    private UserType type;
}
