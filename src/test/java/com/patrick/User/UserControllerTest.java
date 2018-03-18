package com.patrick.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patrick.Security.AccountCredentials;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {UserController.class})
public class UserControllerTest {

    @Autowired
    UserController userController;
    @MockBean
    private UserService userService;
    private MockMvc mvc;

    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void test_getUsers_Normal() throws Exception {
        //Setup
        Mockito.doReturn(Collections.emptyList()).when(userService).fetchAll();
        //Exercise and Assert
        mvc.perform(get("/users")).andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void test_getOneUser_Normal() throws Exception {
        //Setup
        Mockito.doReturn(new User("test", "testpass"))
                .when(userService).fetchOne(1L);
        //Exercise and Assert
        mvc.perform(get("/users/1")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    public void test_getOneUser_NotExist() throws Exception {
        //Setup
        Mockito.doReturn(null).when(userService).fetchOne(1L);
        //Exercise and Assert
        mvc.perform(get("/users/1")).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void test_createUser_Normal() throws Exception {
        //Setup
        ObjectMapper objectMapper = new ObjectMapper();
        AccountCredentials credentials = new AccountCredentials("test", "testpass");
        String jsonString = objectMapper.writeValueAsString(credentials);

        //Resultant user after taking in the Account Credentials
        User u = new User();
        u.setUsername(credentials.getUsername());
        u.setPassword(credentials.getPassword());
        //Exercise and Assert
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString)
        ).andExpect(MockMvcResultMatchers.status().isCreated());
        //Verify
        Mockito.verify(userService).createOne(u);
    }

    @Test
    public void test_createUser_Duplicate() throws Exception {
        //Setup
        AccountCredentials credentials = new AccountCredentials("testuser", "testpass");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(credentials);
        //Resultant user after taking in the Account Credentials
        User u = new User();
        u.setUsername(credentials.getUsername());
        u.setPassword(credentials.getPassword());
        //Stub
        Mockito.doThrow(new DataIntegrityViolationException(""))
                .when(userService).createOne(u);

        //Exercise and Assert
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString)
        ).andExpect(MockMvcResultMatchers.status().isConflict()).andReturn();

        //Verify
        Mockito.verify(userService).createOne(u);
    }

    @Test
    public void test_updateUser_Normal() throws Exception {
        //Setup
        User u = new User("testuser", "testpass");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(u);
        //Stub
        Mockito.doNothing().when(userService).modifyOne(u);

        //Exercise
        mvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString)
        ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
    }
}