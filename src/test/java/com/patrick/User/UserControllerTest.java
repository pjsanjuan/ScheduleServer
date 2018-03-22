package com.patrick.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patrick.Security.AccountCredentials;
import com.patrick.Shift.Shift;
import com.patrick.Shift.ShiftService;
import com.patrick.Task.Task;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {UserController.class})
public class UserControllerTest {

    @Autowired
    UserController userController;
    @MockBean
    private UserService userService;
    @MockBean
    private ShiftService shiftService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .alwaysDo(print())
                .build();
    }

    @Test
    public void test_getUsers_Normal() throws Exception {
        //Setup
        Mockito.doReturn(Collections.emptyList()).when(userService).fetchAll();
        //Exercise and Assert
        mvc.perform(get("/users")).andExpect(status().isOk());
    }

    @Test
    public void test_getUserShifts() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("testpass");
        Task t = new Task("Cleanup");
        Shift testShift = new Shift(OffsetDateTime.now(), OffsetDateTime.now(), testUser, t);

        //Setup
        Mockito.doReturn(Collections.singletonList(testShift)).when(shiftService).findShiftsByUsername(testUser.getUsername());
        mvc.perform(get("/users/testuser/shifts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));

    }

    @Test
    public void test_getOneUser_Normal() throws Exception {
        //Setup
        User user = new User("testuser", "testpass");
        Mockito.doReturn(user).when(userService).fetchOneByUsername(user.getUsername());
        //Exercise and Assert
        mvc.perform(get("/users/" + user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", Matchers.is("testuser")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    public void test_getOneUser_NotExist() throws Exception {
        //Setup
        Mockito.doReturn(null).when(userService).fetchOne(1L);
        //Exercise and Assert
        mvc.perform(get("/users/1")).andExpect(status().isNotFound());
    }

    @Test
    public void test_createUser_Normal() throws Exception {
        //Setup
        AccountCredentials credentials = new AccountCredentials("test", "testpass");
        String jsonString = objectMapper.writeValueAsString(credentials);

        //Resultant user after taking in the Account Credentials
        User u = new User();
        u.setUsername(credentials.getUsername());
        u.setPassword(credentials.getPassword());
        //Exercise and Assert
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
        //Verify
        Mockito.verify(userService).createOne(u);
    }

    @Test
    public void test_createUser_Duplicate() throws Exception {
        //Setup
        AccountCredentials credentials = new AccountCredentials("testuser", "testpass");
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
        ).andExpect(status().isConflict()).andReturn();

        //Verify
        Mockito.verify(userService).createOne(u);
    }

    @Test
    public void test_updateUser_Normal() throws Exception {
        //Setup
        User u = new User("testuser", "testpass");
        String jsonString = objectMapper.writeValueAsString(u);

        //Exercise
        mvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString)
        ).andExpect(status().is2xxSuccessful()).andReturn();
    }
}