package com.patrick.User;

import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Optional;


public interface UserService {
    Collection<User> fetchAll();

    User fetchOne(Long id) throws EntityNotFoundException;

    void createOne(User u) throws DataIntegrityViolationException;

    void modifyOne(User u) throws EntityNotFoundException;

    Optional<User> findOneByUsername(String username);
}
