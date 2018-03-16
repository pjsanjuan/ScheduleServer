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

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

//    @Before
//    public void setUp() {
//        User u = new User("test", "test@gmail.com");
//        u.setId(1L);
//
//        List<User> users = new ArrayList<User>() {{
//            add(u);
//        }};
//        Mockito.when(userRepository.findAll()).thenReturn(users);
//        Mockito.when(userRepository.getOne(1L)).thenReturn(u);
//    }

    @Test
    public void test_createOne() {
        //Setup
        User u = new User("testuser", "testpass");
        //Exercise
        userService.createOne(u);
        //Verify
        Mockito.verify(userRepository).save(u);
    }

    @Test
    public void test_fetchOne() {
        //Setup
        User u = new User("test", "test@gmail.com");
        u.setId(1L);
        Mockito.when(userRepository.getOne(1L)).thenReturn(u);

        //Exercise
        User fetchedUser = userService.fetchOne(1L);
        //Verify
        Mockito.verify(userRepository).getOne(1L);
        org.junit.Assert.assertEquals((Long) 1L, u.getId());
    }

    @Test
    public void test_fetchAll() {
        //Setup
        List<User> users = new ArrayList<User>() {{
            add(new User("testuser", "testpass"));
        }};
        Mockito.when(userRepository.findAll()).thenReturn(users);
        //Exercise
        Collection<User> fetchedUsers = userService.fetchAll();
        //Verify
        Mockito.verify(userRepository).findAll();
        org.junit.Assert.assertEquals(1, fetchedUsers.size());
    }

    @Test
    public void test_modifyOne_saves_if_no_error() {
        //Setup
        Optional<User> optionalUser = Optional.of(new User("testuser", "testpass"));
        Mockito.doReturn(optionalUser).when(userRepository).findById(Mockito.anyLong());
        //Exercise
        User user = new User("testuser", "testpass");
        user.setId(1L);
        userService.modifyOne(user);
        //Verify
        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test(expected = EntityNotFoundException.class)
    public void test_modifyOne_handles_errors_throw_when_saving() {
        //Setup
        Optional<User> emptyOptional = Optional.empty();
        Mockito.doReturn(emptyOptional).when(userRepository).findById(Mockito.anyLong());
        //Exercise
        userService.modifyOne(new User("testuser", "testpass"));
        //Verify
        Mockito.verify(userRepository).findById(Mockito.anyLong());
    }

    //https://docs.spring.io/spring-boot/docs/2.0.0.M5/api/org/springframework/boot/test/mock/mockito/MockBean.html
    @TestConfiguration
    @Import(UserServiceImpl.class)
    static class Config {
    }
}