package com.patrick.User;

import com.patrick.Security.AccountCredentials;
import com.patrick.Shift.Shift;
import com.patrick.Shift.ShiftService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private ShiftService shiftService;

    @Autowired
    public UserController(UserService userService, ShiftService shiftService) {
        this.userService = userService;
        this.shiftService = shiftService;
    }

    @GetMapping("")
    ResponseEntity<Collection<User>> getUsers() {
        return new ResponseEntity<>(userService.fetchAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<User> getOneUser(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.fetchOne(id), HttpStatus.OK);
    }

    @PostMapping("")
    ResponseEntity<?> createUser(@RequestBody AccountCredentials credentials) {
        User user = new User();
        user.setUsername(credentials.getUsername());
        user.setPassword(credentials.getPassword());
        try {
            userService.createOne(user);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User passed is a duplicate.");
        }
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/" + user.getUsername()).build()
                .toUri();
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

    @GetMapping("/{username}/shifts")
    Collection<Shift> getUserShifts(@PathVariable("username") String username) {
        return shiftService.findShiftsByUsername(username);
    }
}
