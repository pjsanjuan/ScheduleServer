package com.patrick.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    UserRepository userRepository;


    @Before
    public void setUp() {
        Mockito.when(userService.fetchAll()).thenReturn(new ArrayList<User>() {{
            add(new User("testuser", "test@gmail.com"));
            add(new User("Rick James", "rj@gmail.com"));
        }});
        Mockito.when(userService.fetchOne(1L)).thenReturn(new User("testuser", "test@gmail.com"));
        Mockito.doNothing()
                .doThrow(new DataIntegrityViolationException(""))
                .when(userService).createOne(new User("testuser", "test@gmail.com"));
    }

    @Test
    public void getUsers() throws Exception {
        mvc.perform(get("/users")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void createUser_NoDuplicate() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User u = new User("Harrison Ford", "hf@gmail.com");
        String jsonString = objectMapper.writeValueAsString(u);
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString)
        ).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        Mockito.verify(userService).createOne(u);
    }

    @Test
    public void createUser_Duplicate() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User u = new User("Harrison Ford", "hf@gmail.com");
        String jsonString = objectMapper.writeValueAsString(u);

        //First create call
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString)
        ).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();

        //Duplicate create call
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString)
        ).andExpect(MockMvcResultMatchers.status().isConflict()).andReturn();

        Mockito.verify(userService, Mockito.times(2)).createOne(u);
    }

    @Test
    public void updateUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User u = new User("testuser", "test@gmail.com");
        String jsonString = objectMapper.writeValueAsString(u);

        mvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonString)
        ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful()).andReturn();
    }
}