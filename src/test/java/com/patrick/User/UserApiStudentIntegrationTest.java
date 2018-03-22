package com.patrick.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patrick.Security.AccountCredentials;
import com.patrick.Security.TokenAuthenticationService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Architecture for this test borrowed from: https://stackoverflow.com/a/45247733/3893713
 * Test cases from the perspective of a STUDENT
 */
//https://docs.spring.io/spring-security/site/docs/3.1.x/reference/el-access.html
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserApiStudentIntegrationTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserRepository userRepository;
    private MockMvc mvc;
    private String jwt = TokenAuthenticationService
            .createToken("testuser", Collections.singletonList(new SimpleGrantedAuthority("STUDENT")));

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
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    public void test_getUsers() throws Exception {
        mvc.perform(get("/users").header("Authorization", jwt))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void test_getOneUser_Self() throws Exception {
        //Setup data
        User user = new User("testuser", "testpass");
        userRepository.saveAndFlush(user); //should have ID 1

        mvc.perform(get("/users/testuser").header("Authorization", jwt))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.username", Matchers.is("testuser")));
    }

    @Test
    public void test_getOneUser_Other() throws Exception {
        //Setup data
        User user = new User("testuser", "testpass");
        userRepository.saveAndFlush(user); //should have ID 1

        mvc.perform(get("/users/wronguser")
                .header("Authorization", jwt)
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void test_createOneUser() throws Exception {
        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(new AccountCredentials("testuser", "testpass")))
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void test_modifyOneUser() throws Exception {
        mvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(new User("testuser", "testpass")))
        ).andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
