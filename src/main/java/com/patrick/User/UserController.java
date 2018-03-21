package com.patrick.User;

import com.patrick.Security.AccountCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping("/users")
//https://docs.spring.io/spring-security/site/docs/3.1.x/reference/el-access.html
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<Collection<User>> getUsers() {
        return new ResponseEntity<>(userService.fetchAll(), HttpStatus.OK);
    }

    @GetMapping("/{username}")
    @PreAuthorize("#username == principal")
    ResponseEntity<User> getOneUser(@PathVariable("username") String username) {
        return new ResponseEntity<>(userService.fetchOneByUsername(username), HttpStatus.OK);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<?> createUser(@RequestBody AccountCredentials credentials) {
        User user = new User();
        user.setUsername(credentials.getUsername());
        user.setPassword(credentials.getPassword());
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
        try {
            user.setId(id);
            userService.modifyOne(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("User with ID: " + id + "could not be found");
        }
        return ResponseEntity.ok().build();
    }
}
