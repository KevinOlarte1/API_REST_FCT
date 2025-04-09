package com.kevinolarte.resibenissa.controllers;

import com.kevinolarte.resibenissa.dto.UserDto;
import com.kevinolarte.resibenissa.models.User;
import com.kevinolarte.resibenissa.repositories.UserRepository;
import com.kevinolarte.resibenissa.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.OptionalInt;

@RequestMapping("/resi/users")
@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody UserDto userDto) {
        try{
            User user = userService.save(userDto);
            return ResponseEntity.ok().body(user);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) Long idResidencia,
            @RequestParam(required = false) Boolean enable,
            @RequestParam(required = false) String email) {
        try {
            List<User> users = userService.getUsers(idResidencia, enable, email);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



}
