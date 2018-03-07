package com.patrick.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void insertUser(){
        userRepository.save(new User("testuser", "test@gmail.com"));
    }

    @Test
    public void findUser(){
        User u = new User("testuser", "test@gmail.com");
        entityManager.persist(u);
        entityManager.flush();

        Optional<User> fetchedUser = userRepository.findById(1L);
        assertTrue(fetchedUser.isPresent());
        assertEquals(u, fetchedUser.get());
    }

    @Test
    public void modifyUserIfExists(){
        User u = new User("testuser", "test@gmail.com");
        entityManager.persist(u);
        entityManager.flush();

        User modified = new User("modified@gmail.com","modified@gmail.com");
        modified.setId(1L);
        userRepository.save(modified);

        Optional<User> found = userRepository.findById(1L);
        assertTrue(found.isPresent());
        assertEquals(found.get(), modified);
    }
}