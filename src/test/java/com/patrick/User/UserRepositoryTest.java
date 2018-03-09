package com.patrick.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createUser(){
        userRepository.createUser("testuser", "test@gmail.com");
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

        u.setEmail("modified@gmail.com");
        u.setUsername("modifiedUsername");
        userRepository.saveAndFlush(u);

        Optional<User> found = userRepository.findById(1L);
        assertTrue(found.isPresent());
        assertEquals(found.get(), u);
    }

    @Test
    public void duplicateUser(){
        thrown.expect(DataIntegrityViolationException.class);
        User u = new User("testuser", "test@gmail.com");
        userRepository.saveAndFlush(u);
        User u2 = new User("testuser", "test@gmail.com");
        userRepository.saveAndFlush(u2);
    }
}