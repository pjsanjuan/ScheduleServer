package com.patrick.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;



@RunWith(SpringRunner.class) // docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/junit4/SpringJUnit4ClassRunner.html
@DataJpaTest // docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/autoconfigure/orm/jpa/DataJpaTest.html
public class UserRepositoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createUser() {
        userRepository.saveAndFlush(new User("testuser", "testpass"));
    }

    @Test
    public void findUser() {
        User u = new User("testuser", "testpass");
        entityManager.persist(u);
        entityManager.flush();

        Optional<User> fetchedUser = userRepository.findById(u.getId());
        assertTrue(fetchedUser.isPresent());
        assertEquals(u, fetchedUser.get());
    }

    @Test
    public void modifyUserIfExists() {
        User u = new User("testuser", "testpass");
        entityManager.persist(u);
        entityManager.flush();

        u.setUsername("modifiedUsername");
        u.setPassword("modifiedPass");
        userRepository.saveAndFlush(u);

        Optional<User> found = userRepository.findById(u.getId());
        assertTrue(found.isPresent());
        assertEquals(found.get(), u);
    }

    @Test
    public void duplicateUser() {
        thrown.expect(DataIntegrityViolationException.class);
        User u = new User("testuser", "testpass");
        userRepository.saveAndFlush(u);
        User u2 = new User("testuser", "testpass");
        userRepository.saveAndFlush(u2);
    }

    @Test
    public void findByUsername() {
        User u = new User("testuser", "testpass");
        entityManager.persist(u);
        entityManager.flush();

        User fetchedUser = userRepository.findByUsername("testuser");
        assertNotNull(fetchedUser);
    }
}