package com.patrick.Shift;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patrick.Security.TokenAuthenticationService;
import com.patrick.Task.Task;
import com.patrick.Task.TaskRepository;
import com.patrick.User.User;
import com.patrick.User.UserRepository;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test cases from the perspective of a ADMIN
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//https://docs.spring.io/spring-security/site/docs/3.1.x/reference/el-access.html
public class ShiftApiAdminIntegrationTest {
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ObjectMapper objectMapper;

    //Test Data
    private User u = new User("testuser", "testpass");
    private Task t = new Task("Cleanup");
    private Shift s = new Shift(OffsetDateTime.now(), OffsetDateTime.now(), u, t);

    private MockMvc mvc;
    private String jwt = TokenAuthenticationService
            .createToken("admin", Collections.singletonList(new SimpleGrantedAuthority("ADMIN")));

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo(print())
                .build();
    }

    @After
    public void tearDown() {
        shiftRepository.deleteAll();
        userRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    public void test_getShifts() throws Exception {
        userRepository.saveAndFlush(u);
        taskRepository.saveAndFlush(t);
        shiftRepository.saveAndFlush(s);

        mvc.perform(get("/shifts").header("Authorization", jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void test_getOneShift() throws Exception {
        userRepository.saveAndFlush(u);
        taskRepository.saveAndFlush(t);
        shiftRepository.saveAndFlush(s);

        mvc.perform(get("/shifts/" + s.getId()).header("Authorization", jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

    }

    @Test
    public void test_createShift() throws Exception {
        userRepository.saveAndFlush(u);
        taskRepository.saveAndFlush(t);
        Shift s = new Shift(OffsetDateTime.now(), OffsetDateTime.now(), u, t);
        mvc.perform(post("/shifts")
                .header("Authorization", jwt)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(s))
        ).andExpect(status().isCreated()).andExpect(header().exists("Location"));
    }

    @Test
    public void test_updateShift() throws Exception {
        userRepository.saveAndFlush(u);
        taskRepository.saveAndFlush(t);
        shiftRepository.saveAndFlush(s);

        mvc.perform(put("/shifts/" + s.getId())
                .header("Authorization", jwt)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(s))
        ).andExpect(status().isOk());
    }
}
