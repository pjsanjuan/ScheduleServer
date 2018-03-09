package com.patrick;

import com.patrick.User.User;
import com.patrick.User.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@DataJpaTest
public class DeleteTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void exampleTest() {
        User u = new User("testuser", "test@gmail.com");
        entityManager.persist(u);
        entityManager.flush();
        assertTrue(userRepository.exists(Example.of(new User("testuser", "test@gmail.com"))));
    }
}
