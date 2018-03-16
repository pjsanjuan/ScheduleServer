package com.patrick.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        User u = new User("test", "test@gmail.com");
        u.setId(1L);

        List<User> users = new ArrayList<User>() {{
            add(u);
        }};
        Mockito.when(userRepository.findAll()).thenReturn(users);
        Mockito.when(userRepository.getOne(1L)).thenReturn(u);
    }

    @Test
    public void test_createOne() {
        //Setup
        User u = new User("Rick James", "rj@gmail.com");
        //Exercise
        userService.createOne(u);
        //Verify
        Mockito.verify(userRepository).save(u);
    }

    @Test
    public void test_fetchOne() {
        //Exercise
        User u = userService.fetchOne(1L);
        //Verify
        Mockito.verify(userRepository).getOne(1L);
    }

    @Test
    public void test_fetchAll() {
        //Exercise
        Collection<User> users = userService.fetchAll();
        //Verify
        Mockito.verify(userRepository).findAll();
    }

    @Test
    public void test_modifyOne() {

    }

    //https://docs.spring.io/spring-boot/docs/2.0.0.M5/api/org/springframework/boot/test/mock/mockito/MockBean.html
    @TestConfiguration
    @Import(UserServiceImpl.class)
    static class Config {
    }
}