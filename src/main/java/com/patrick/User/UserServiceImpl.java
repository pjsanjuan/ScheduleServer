package com.patrick.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Collection<User> fetchAll() {
        return userRepository.findAll();
    }

    @Override
    public User fetchOne(Long id) throws EntityNotFoundException {
        return userRepository.getOne(id);
    }


    @Override
    public void createOne(User u) throws DataIntegrityViolationException {
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        userRepository.save(u); //should throw exception if violating a constraint
    }

    @Override
    public void modifyOne(User u) throws EntityNotFoundException {
        Optional<User> optionalUser = userRepository.findById(u.getId());
        if (!optionalUser.isPresent())
            throw new EntityNotFoundException(); //If user doesn't exist, throw entity not found exception
        else
            userRepository.save(u); //otherwise call .save on the User object

    }

    @Override
    public Optional<User> findOneByUsername(String username) {
        return Optional.of(userRepository.findByUsername(username));
    }
}
