package com.patrick.User;

import com.patrick.Security.AccountCredentials;
import com.patrick.Shift.Shift;
import com.patrick.Shift.ShiftService;
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
    private ShiftService shiftService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, ShiftService shiftService) {
        this.userService = userService;
        this.shiftService = shiftService;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    Collection<User> getUsers() {
        return userService.fetchAll();
    }

    @GetMapping("/{username}")
    @PreAuthorize("#username == principal OR hasAuthority('ADMIN')")
    User getOneUser(@PathVariable("username") String username) {
        return userService.fetchOneByUsername(username);
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
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/" + user.getUsername()).build()
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable("username") String username) {
        try {
            User dbUser = userService.fetchOneByUsername(username);
            dbUser.setEmail(user.getEmail());
            userService.modifyOne(dbUser);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body("User with ID: " + user.getUsername() + "could not be found");
        }
    }

    @GetMapping("/{username}/shifts")
    Collection<Shift> getUserShifts(@PathVariable("username") String username) {
        return shiftService.findShiftsByUsername(username);
    }
}
