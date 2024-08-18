package com.library.services;

import com.library.model.entities.User;
import com.library.model.request.CreateUserDto;
import com.library.model.request.SignInDto;
import com.library.model.request.updateUserDto;
import com.library.model.response.AppResponse;
import com.library.repositories.BookRepository;
import com.library.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Override
    public AppResponse addUser(CreateUserDto user) {
        log.info("Creating user: {}", user);
        try{
            if(user == null){
                return  new AppResponse("Request object cannot be null", HttpStatus.BAD_REQUEST.toString(), null);
            }
            log.info("watch me here");
            Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
            if(existingUser.isPresent()){
                log.info("existingUser: {}", existingUser.get());
                return  new AppResponse("User already exists", HttpStatus.BAD_REQUEST.toString(), "");
            }

            User newUser = User.builder()
                    .type(user.getType().toString())
                    .firstName(user.getFirstName())
                    .gender(user.getGender())
                    .lastName(user.getLastName())
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .build();

            userRepository.save(newUser);

            return new AppResponse("User added successfully", HttpStatus.OK.toString(), newUser);
        }catch (Exception e){
            return new AppResponse(e.getMessage(), HttpStatus.BAD_REQUEST.toString(), null);
        }
    }


    @Override
    public AppResponse updateUser(updateUserDto user) {
        try {
            if(user == null){
                return new AppResponse("Request object cannot be null", HttpStatus.BAD_REQUEST.toString(), null);
            }
            Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
            if(existingUser.isEmpty()){
                return new AppResponse("User does not exist", HttpStatus.BAD_REQUEST.toString(), null);
            }
            User existingUserObject = existingUser.get();
            if(user.getUsername() != null){
                existingUserObject.setUsername(user.getUsername());
            }
            if(user.getPassword() != null){
                existingUserObject.setPassword(user.getPassword());
            }
            if(user.getFirstName() != null){
                existingUserObject.setFirstName(user.getFirstName());
            }
            if(user.getLastName() != null){
                existingUserObject.setLastName(user.getLastName());
            }
            userRepository.save(existingUserObject);
            return new AppResponse("User updated successfully", "00", existingUserObject);
        }catch(Exception e){
            return new AppResponse(e.getMessage(), HttpStatus.BAD_REQUEST.toString(), null);
        }
    }
    @Override
    public AppResponse deleteUser(Long id) {
        try{
            Optional<User> existingUser = userRepository.findById(id);
            if(existingUser.isEmpty()){
                return  new AppResponse("User does not exist", HttpStatus.BAD_REQUEST.toString(), null);
            }

            userRepository.delete(existingUser.get());

            return new AppResponse("User deleted successfully", "00", null);
        }catch (Exception e){
            return new AppResponse(e.getMessage(), HttpStatus.BAD_REQUEST.toString(), null);
        }
    }

    @Override
    public AppResponse getUserById(Long id) {
        try{
            Optional<User> existingUser = userRepository.findById(id);
            return existingUser.map(user -> new AppResponse("User detail retrieved successfully", "00", user)).
                    orElseGet(() -> new AppResponse("User does not exist", HttpStatus.BAD_REQUEST.toString(), null));

        }catch (Exception e){
            return new AppResponse(e.getMessage(), HttpStatus.BAD_REQUEST.toString(), null);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public AppResponse getUserByEmail(String email) {
        return null;
    }

    @Override
    public AppResponse signIn(SignInDto signInDto) {
        try{
            Optional<User> existingUser = userRepository.findByUsername(signInDto.getUsername());
            if(existingUser.isEmpty()){
                return  new AppResponse("User does not exist", HttpStatus.BAD_REQUEST.toString(), null);
            }

            if(!existingUser.get().getPassword().equals(signInDto.getPassword())){
                return new AppResponse("Wrong password", HttpStatus.BAD_REQUEST.toString(), null);
            }

            return new AppResponse("User logged in successfully", "00", existingUser);
        }catch (Exception exception){
            return new AppResponse(exception.getMessage(), HttpStatus.BAD_REQUEST.toString(), null);
        }
    }
}
