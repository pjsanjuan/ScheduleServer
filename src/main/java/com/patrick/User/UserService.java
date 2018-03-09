package com.patrick.User;

import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;

public interface UserService {
    //FetchAll
    Collection<User> fetchAll();
    //FetchOne
    User fetchOne(Long id) throws EntityNotFoundException;
    //CreateOne
    void createOne(User u) throws DataIntegrityViolationException;
    //Modify
    void modifyOne(User u) throws EntityNotFoundException;
}
