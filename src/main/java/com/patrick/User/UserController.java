package com.patrick.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    ResponseEntity<Collection<User>> getUsers() {
        return new ResponseEntity<>(userService.fetchAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<User> getOneUser(@PathVariable("id") Long id){
        return new ResponseEntity<>(userService.fetchOne(id), HttpStatus.OK);
    }

    @PostMapping("")
    ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            userService.createOne(user);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User passed is a duplicate.");
        }
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/" + user.getUsername()).build().toUri();
        return ResponseEntity.created(location).build();

    }


    @PutMapping("/{id}")
    ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable("id") Long id) {
        try{
            user.setId(id);
            userService.modifyOne(user);
        } catch (EntityNotFoundException e){
            return ResponseEntity.badRequest().body("User with ID: " + id + "could not be found");
        }
        return ResponseEntity.ok().build();
    }
}
