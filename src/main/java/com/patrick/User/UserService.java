package com.patrick.User;

import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;


public interface UserService {
    Collection<User> fetchAll();

    User fetchOne(Long id) throws EntityNotFoundException;

    void createOne(User u) throws DataIntegrityViolationException;

    void modifyOne(User u) throws EntityNotFoundException;

    User fetchOneByUsername(String username) throws EntityNotFoundException;
}
