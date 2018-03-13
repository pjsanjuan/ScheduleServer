package com.patrick.Security;

import com.patrick.User.User;
import com.patrick.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userService.findOneByUsername(username);
        if (optionalUser.isPresent())
            return new org.springframework.security.core.userdetails.User(
                    optionalUser.get().getUsername(),
                    optionalUser.get().getPassword(),
                    Collections.emptyList()
            );
        throw new UsernameNotFoundException(username);
    }
}
